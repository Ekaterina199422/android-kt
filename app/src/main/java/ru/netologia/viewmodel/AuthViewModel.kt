package ru.netologia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import ru.netologia.Auth.AppAuth
import ru.netologia.Auth.AuthState

class AuthViewModel : ViewModel(){
    val data: LiveData<AuthState> = AppAuth.getInstance() // подписываемся на AppAuth
        .authStateFlow
        .asLiveData(Dispatchers.Default)
    val authenticated: Boolean
        get() = AppAuth.getInstance().authStateFlow.value.id != 0L // проверка id
}