package com.gabr.gabc.qook.domain.user

sealed class UserFailure(open val error: String) {
    data class SignInFailed(override val error: String) : UserFailure(error)
    data class NotAuthenticated(override val error: String) : UserFailure(error)
    data class UserCreationFailed(override val error: String) : UserFailure(error)
    data class UserDoesNotExist(override val error: String) : UserFailure(error)
    data class UserTranslationFailed(override val error: String) : UserFailure(error)
    data class UpdateAvatarFailure(override val error: String) : UserFailure(error)
    data class PasswordChangeFailure(override val error: String) : UserFailure(error)
}
