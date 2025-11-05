package dev.jay.passwordmanager

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dev.jay.passwordmanager.data.AppDatabase
import dev.jay.passwordmanager.data.DetailState
import dev.jay.passwordmanager.data.PasswordRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AddState(
    val accountName: String = "",
    val userName: String = "",
    val password: String = "",
    val isSaving: Boolean = false,
    val editingId: Long? = null
)

class PasswordViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = PasswordRepository(AppDatabase.get(app).passwordDao())

    val passwords = repo.getAll().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    private val _addState = MutableStateFlow(AddState())
    val addState: StateFlow<AddState> = _addState

    private val _events = MutableSharedFlow<String>()
    val events = _events

    private val _detailState = MutableStateFlow(DetailState())
    val detailState: StateFlow<DetailState> = _detailState

    // Field Update Handlers
    fun updateAccountName(v: String) {
        _addState.value = _addState.value.copy(accountName = v)
    }

    fun updateUserName(v: String) {
        _addState.value = _addState.value.copy(userName = v)
    }

    fun updatePassword(v: String) {
        _addState.value = _addState.value.copy(password = v)
    }

    fun save() {
        val s = _addState.value

        when {
            s.accountName.isBlank() -> {
                viewModelScope.launch { _events.emit("Account name is required") }
                return
            }

            s.userName.isBlank() -> {
                viewModelScope.launch { _events.emit("Username/Email is required") }
                return
            }

            s.password.isBlank() -> {
                viewModelScope.launch { _events.emit("Password is required") }
                return
            }
        }

        viewModelScope.launch {
            try {
                _addState.value = s.copy(isSaving = true)
                repo.add(s.accountName, s.userName, s.password)
                _addState.value = AddState()
                _events.emit("Saved successfully")
            } catch (e: Exception) {
                _events.emit(e.localizedMessage ?: "Failed to save")
            }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            repo.delete(id)
        }
    }

    fun loadDetail(id: Long) {
        viewModelScope.launch {
            _detailState.value = DetailState(loading = true)
            try {
                val data = repo.getDecrypted(id)!!
                _detailState.value = DetailState(
                    id = data.id,
                    accountName = data.accountName,
                    userName = data.userName,
                    password = data.passwordPlain
                )
            } catch (e: Exception) {
                _detailState.value = DetailState(error = "Failed to decrypt")
            }
        }
    }

    fun startEdit(id: Long, accountName: String, userName: String, password: String) {
        _addState.value = AddState(
            accountName = accountName,
            userName = userName,
            password = password,
            editingId = id
        )
    }

    fun update() {
        val s = _addState.value
        if (s.editingId == null) return

        when {
            s.accountName.isBlank() -> viewModelScope.launch { _events.emit("Account name is required") }
            s.userName.isBlank() -> viewModelScope.launch { _events.emit("Username/Email is required") }
            s.password.isBlank() -> viewModelScope.launch { _events.emit("Password is required") }
            else -> viewModelScope.launch {
                try {
                    _addState.value = s.copy(isSaving = true)
                    repo.update(s.editingId, s.accountName, s.userName, s.password)
                    _addState.value = AddState()
                    _events.emit("Updated successfully")
                } catch (e: Exception) {
                    _events.emit(e.localizedMessage ?: "Failed to update")
                }
            }
        }
    }
}