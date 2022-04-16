package io.github.ricardormdev.clockifyplugin

import java.util.logging.Level

fun random8String() : String {
    val allowedChars = ('a'..'z')
    return (1..8).map { allowedChars.random() }.joinToString("")
}

fun String.print() : String {
    println("[ClockifyPlugin] $this")
    return "[ClockifyPlugin] $this"
}

fun String.info() {
    this.print()
}

fun Int.toFormattedTime() : String {
    val minutes = (this / 60)
    val hours = (minutes / 60)

    return "${hours % 24}h ${minutes % 60}m ${this % 60}s"
}