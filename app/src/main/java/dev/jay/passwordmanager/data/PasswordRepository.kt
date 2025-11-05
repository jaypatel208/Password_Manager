package dev.jay.passwordmanager.data

import dev.jay.passwordmanager.security.CryptoManager
import kotlinx.coroutines.flow.Flow

data class DecryptedPassword(
    val id: Long,
    val accountName: String,
    val userName: String,
    val passwordPlain: String
)

class PasswordRepository(private val dao: PasswordDao) {

    suspend fun add(accountName: String, userName: String, passwordPlain: String): Long {
        val enc = CryptoManager.encrypt(passwordPlain)
        val entity = PasswordEntity(
            accountName = accountName.trim(),
            userName = userName.trim(),
            passwordCipher = enc.cipherText,
            passwordIv = enc.iv
        )
        return dao.insert(entity)
    }

    fun getAll(): Flow<List<PasswordEntity>> = dao.getAll()

    suspend fun getDecrypted(id: Long): DecryptedPassword? {
        val entity = dao.getById(id) ?: return null
        val plain = CryptoManager.decrypt(entity.passwordCipher, entity.passwordIv)
        return DecryptedPassword(
            id = entity.id,
            accountName = entity.accountName,
            userName = entity.userName,
            passwordPlain = plain
        )
    }

    suspend fun delete(id: Long) = dao.delete(id)

    suspend fun update(id: Long, accountName: String, userName: String, passwordPlain: String) {
        val enc = CryptoManager.encrypt(passwordPlain)
        val entity = PasswordEntity(
            id = id,
            accountName = accountName.trim(),
            userName = userName.trim(),
            passwordCipher = enc.cipherText,
            passwordIv = enc.iv
        )
        dao.insert(entity)
    }
}