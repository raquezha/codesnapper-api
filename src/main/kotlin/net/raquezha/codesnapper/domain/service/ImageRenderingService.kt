package net.raquezha.codesnapper.domain.service

import net.raquezha.codesnapper.domain.model.HighlightedCode
import net.raquezha.codesnapper.domain.model.ImageConfiguration

interface ImageRenderingService {
    fun renderToPng(
        highlightedCode: HighlightedCode,
        config: ImageConfiguration,
    ): ByteArray
}
