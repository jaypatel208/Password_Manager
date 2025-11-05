package dev.jay.passwordmanager.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passwords")
data class PasswordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val accountName: String,
    val userName: String,
    val passwordCipher: ByteArray,
    val passwordIv: ByteArray,
    val createdAt: Long = System.currentTimeMillis()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as PasswordEntity

        if (id != other.id) return false
        if (createdAt != other.createdAt) return false
        if (accountName != other.accountName) return false
        if (userName != other.userName) return false
        if (!passwordCipher.contentEquals(other.passwordCipher)) return false
        if (!passwordIv.contentEquals(other.passwordIv)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + accountName.hashCode()
        result = 31 * result + userName.hashCode()
        result = 31 * result + passwordCipher.contentHashCode()
        result = 31 * result + passwordIv.contentHashCode()
        return result
    }
}