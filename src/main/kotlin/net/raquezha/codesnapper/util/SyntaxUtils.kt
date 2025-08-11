package net.raquezha.codesnapper.util

import dev.snipme.highlights.model.SyntaxLanguage
import dev.snipme.highlights.model.SyntaxTheme
import dev.snipme.highlights.model.SyntaxThemes

/**
 * SyntaxUtils
 *
 * Utility functions for parsing and converting syntax language and theme parameters.
 */

fun parseSyntaxLanguage(name: String?): SyntaxLanguage {
    return SyntaxLanguage.entries.firstOrNull {
        it.name.equals(name, ignoreCase = true)
    } ?: SyntaxLanguage.KOTLIN
}

fun parseSyntaxTheme(
    name: String?,
    darkMode: Boolean = true,
): SyntaxTheme {
    return when (name?.lowercase()) {
        "darcula" -> SyntaxThemes.darcula(darkMode)
        "monokai" -> SyntaxThemes.monokai(darkMode)
        "notepad" -> SyntaxThemes.notepad(darkMode)
        "matrix" -> SyntaxThemes.matrix(darkMode)
        "pastel" -> SyntaxThemes.pastel(darkMode)
        "atom", "atomone", "atom_one" -> SyntaxThemes.atom(darkMode)
        else -> SyntaxThemes.darcula(darkMode)
    }
}
