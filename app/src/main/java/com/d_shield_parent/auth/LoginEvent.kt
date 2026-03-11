package com.d_shield_parent.auth

sealed class LoginEvent {
    data class OnUserTypeChanged(val userType: UserType) : LoginEvent()
    data class OnPhoneNumberChanged(val phoneNumber: String) : LoginEvent()
    data class OnLoginMethodChanged(val loginMethod: LoginMethod) : LoginEvent()
    data class OnPasswordChanged(val password: String) : LoginEvent()
    data class OnMpinChanged(val mpin: String) : LoginEvent()
    data object OnPasswordVisibilityToggled : LoginEvent()
    data object OnLoginClicked : LoginEvent()
    data object OnSetMpinClicked : LoginEvent()
}