package com.d_shield_parent.auth

data class LoginUiState(
    val selectedUserType: UserType = UserType.RETAILER,
    val phoneNumber: String = "",
    val loginMethod: LoginMethod = LoginMethod.PASSWORD,
    val password: String = "",
    val mpin: String = "",
    val passwordVisible: Boolean = false,
    val phoneError: String = "",
    val loginSuccess: Boolean = false,
    val passwordError: String = "",
    val mpinError: String = "",
    val isLoading: Boolean = false,
    val navigateTo: String = "",
    val errorMessage: String = ""
)

enum class UserType {
    RETAILER,
    DISTRIBUTOR
}
enum class LoginMethod {
    PASSWORD,
    MPIN
}