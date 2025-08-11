package net.raquezha.codesnapper.domain.model

data class CodeSnippet(
    val code: String,
    val language: String,
    val theme: String,
    val darkMode: Boolean = true
)
