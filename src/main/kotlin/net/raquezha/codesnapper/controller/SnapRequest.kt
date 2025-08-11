package net.raquezha.codesnapper.controller

import kotlinx.serialization.Serializable

@Serializable
data class SnapRequest(
    val code: String,
    val language: String,
    val theme: String,
    val darkMode: Boolean = true
)

