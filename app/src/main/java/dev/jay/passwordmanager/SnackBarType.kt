package dev.jay.passwordmanager

enum class SnackBarType { SUCCESS, ERROR, DELETE }

data class AppSnackBarMessage(
    val text: String,
    val type: SnackBarType
)