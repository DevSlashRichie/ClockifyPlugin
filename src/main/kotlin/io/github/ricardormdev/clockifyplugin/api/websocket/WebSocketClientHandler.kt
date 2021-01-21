package io.github.ricardormdev.clockifyplugin.api.websocket

import io.netty.channel.ChannelFuture
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelPromise
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.FullHttpResponse
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker
import io.netty.util.CharsetUtil

class WebSocketClientHandler(
        private val handshaker: WebSocketClientHandshaker,
        private val adapters: ArrayList<Pair<ClockifyEvent, () -> Unit>>
    ) : SimpleChannelInboundHandler<Any>() {

    private lateinit var handshakeFuture: ChannelPromise

    fun handshakeFuture() : ChannelFuture {
        return handshakeFuture
    }

    override fun handlerAdded(ctx: ChannelHandlerContext) {
        handshakeFuture = ctx.newPromise()
    }

    override fun channelActive(ctx: ChannelHandlerContext) {
        handshaker.handshake(ctx.channel())
    }

    override fun channelInactive(ctx: ChannelHandlerContext?) {
        // WebSocket Client disconnected.
    }

    override fun channelRead0(ctx: ChannelHandlerContext, msg: Any) {
        val ch = ctx.channel()
        if(!handshaker.isHandshakeComplete) {
           handshaker.finishHandshake(ch, msg as FullHttpResponse)
           handshakeFuture.setSuccess()
            return
        }

        if(msg is FullHttpResponse) {
            throw Exception(
                "Unexpected FullHttpResponse (getStatus=${msg.status}, content=${
                    msg.content().toString(
                        CharsetUtil.UTF_8
                    )
                })")
        }

        when (msg) {
            is TextWebSocketFrame -> {
                val data = msg.text()
                val event = ClockifyEvent.get(data)

                if(event != null) {
                    adapters.forEach {
                        if(event == it.first) {
                            it.second()
                        }
                    }
                } else {
                    println("An unexpected event came from the socket ($data)")
                }
            }
            is PongWebSocketFrame -> { }
            is CloseWebSocketFrame -> {
                println("Closed WebSocket")
                ch.close()
            }
        }
    }

    override fun exceptionCaught(ctx: ChannelHandlerContext?, cause: Throwable?) {
        cause?.printStackTrace()
        if (!handshakeFuture.isDone) {
            handshakeFuture.setFailure(cause)
        }

        ctx?.close()
    }


}