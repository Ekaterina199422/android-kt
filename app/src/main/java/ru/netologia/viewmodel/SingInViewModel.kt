package ru.netologia.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import ru.netologia.application.NMediaApplication
import ru.netologia.application.NMediaApplication.Companion.repository
import java.io.IOException
import kotlin.coroutines.EmptyCoroutineContext

class SingInViewModel:ViewModel() {
    fun getUserLogin(login: String, pass: String) {
        CoroutineScope(EmptyCoroutineContext).launch {
            try {
                val authState = repository.updateUser(login, pass)
                Log.e("MY", authState.id.toString())
                NMediaApplication.appAuth.setAuth(authState.id, authState.token ?: "x-token")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


}