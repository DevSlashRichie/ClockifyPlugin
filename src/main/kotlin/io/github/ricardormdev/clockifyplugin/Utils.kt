package io.github.ricardormdev.clockifyplugin

fun random8String() : String {
    val allowedChars = ('a'..'z')
    return (1..8).map { allowedChars.random() }.joinToString("")
}

fun String.print() : String {
    println("[CarlitosPlugin] $this")
    return "[CarlitosPlugin] $this"
}

fun Int.toFormattedTime() : String {
    val minutes = (this / 60)
    val hours = (minutes / 60)

    return "${hours % 24}h ${minutes % 60}m ${this % 60}s"
}