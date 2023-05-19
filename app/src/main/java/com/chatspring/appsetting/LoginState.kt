package com.chatspring.appsetting
import android.app.Application
class LoginState : Application() {
    companion object{
        var isLoggedIn: Boolean = false
    }
}