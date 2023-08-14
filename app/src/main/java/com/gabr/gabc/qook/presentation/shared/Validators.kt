package com.gabr.gabc.qook.presentation.shared

import android.util.Patterns

class Validators {
    companion object {
        fun isEmailInvalid(email: String) = !Patterns.EMAIL_ADDRESS.matcher(email).matches()
        fun isPasswordInvalid(password: String) = password.trim().isEmpty() || password.length < 6
        fun isNameInvalid(name: String) = name.trim().isEmpty() || name.length > 24
        fun isRecipeNameInvalid(name: String) = name.trim().isEmpty() || name.length > 64
    }
}