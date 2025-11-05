package dev.jay.passwordmanager.data

data class DetailState(
    val id: Long? = null,
    val loading: Boolean = false,
    val accountName: String? = null,
    val userName: String? = null,
    val password: String? = null,
    val error: String? = null
)
