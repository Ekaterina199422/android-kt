package ru.netologia.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.netologia.auth.AppAuth
import ru.netologia.repository.IPostRepository
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.EmptyCoroutineContext

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val auth: AppAuth,
    private val repository: IPostRepository
) : ViewModel() {

    fun getUserLogin(login: String, pass: String) {
        CoroutineScope(EmptyCoroutineContext).launch {
            try {
                val authState = repository.updateUser(login, pass)
                Log.e("MY", authState.id.toString())
                auth.setAuth(authState.id, authState.token ?: "x-token")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


}