package io.github.ricardormdev.clockifyplugin.api.websocket

import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.SocketChannel
import io.netty.channel.socket.nio.NioSocketChannel
import io.netty.handler.codec.http.HttpClientCodec
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpObjectAggregator
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory
import io.netty.handler.codec.http.websocketx.WebSocketVersion
import java.net.URI

class WebSocketClient(uri: String) {

    private val uri: URI = URI.create(uri)
    private lateinit var channel: Channel
    private val group = NioEventLoopGroup()

    private var adapters: ArrayList<Pair<ClockifyEvent, () -> Unit>> = ArrayList()

    fun open() {
        val bootstrap = Bootstrap()
        val protocol = uri.scheme

        if(protocol != "wss") {
            throw IllegalArgumentException("Protocol ($protocol) not supported. ")
        }

        val handler = WebSocketClientHandler(
            WebSocketClientHandshakerFactory.newHandshaker(
                uri, WebSocketVersion.V13, null, false, HttpHeaders.EMPTY_HEADERS,1280000
            ), adapters
        )

        bootstrap.group(group).
                channel(NioSocketChannel::class.java).
                handler(object : ChannelInitializer<SocketChannel>() {
                    override fun initChannel(ch: SocketChannel?) {
                        val pipeline = ch?.pipeline()
                        pipeline?.apply {
                            addLast("http-codec", HttpClientCodec())
                            addLast("aggregator", HttpObjectAggregator(65536))
                            addLast("ws-handler", handler)
                        }
                    }
                })

        channel = bootstrap.connect(uri.host, uri.port).sync().channel()
        handler.handshakeFuture().sync()

    }

    fun registerAdapter(accept: ClockifyEvent, consumer: () -> Unit) {
        adapters.add(Pair(accept, consumer))
    }

    fun authenticate(token: String) {
        this.channel.writeAndFlush(TextWebSocketFrame(token))
    }

    fun close() {
        channel.writeAndFlush(CloseWebSocketFrame())
        channel.closeFuture().sync()
    }

}