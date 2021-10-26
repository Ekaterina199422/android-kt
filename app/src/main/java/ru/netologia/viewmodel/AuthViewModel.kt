package ru.netologia.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import ru.netologia.Auth.AuthState
import ru.netologia.application.NMediaApplication

class AuthViewModel : ViewModel(){
    val data: LiveData<AuthState> = NMediaApplication.appAuth // подписываемся на AppAuth
        .authStateFlow
        .asLiveData(Dispatchers.Default)
    val authenticated: Boolean
        get() = NMediaApplication.appAuth.authStateFlow.value.id != 0L // проверка id
}