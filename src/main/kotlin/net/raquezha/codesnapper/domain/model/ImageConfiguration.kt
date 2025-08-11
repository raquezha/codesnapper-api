package net.raquezha.codesnapper.domain.model

data class ImageConfiguration(
    val width: Int = 800,
    val height: Int = 600,
    val padding: Int = 40,
    val fontSize: Int = 14,
    val fontFamily: String = "JetBrains Mono",
    val backgroundColor: String = "#282c34",
    val lineHeight: Float = 1.4f,
)
