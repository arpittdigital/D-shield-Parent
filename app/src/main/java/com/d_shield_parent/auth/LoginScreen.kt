package com.d_shield_parent.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d_shield_parent.Dashboard.AppColors
import com.d_shield_parent.R

@Composable
fun LoginScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val goldColor = Color(0xFFD4AF37)
    val darkGold = Color(0xFFB8962A)
    val lightBg = Color(0xFFFFF9E6)

    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            val destination = if (uiState.selectedUserType == UserType.DISTRIBUTOR)
                "distributor_dashboard_screen"
            else
                "dashboard_screen"

            navController.navigate(destination) {
                popUpTo("login_screen") { inclusive = true }
            }
        }
    }

    LaunchedEffect(uiState.navigateTo) {
        if (uiState.navigateTo.isNotEmpty()) {
            navController.navigate(uiState.navigateTo) {
                popUpTo("login_screen") { inclusive = true }
            }
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage.isNotEmpty()) {
            // show a Toast or Snackbar
        }
    }
    Column(
        modifier = modifier
            .fillMaxSize()
            .fillMaxWidth()
            .imePadding()
            .background(AppColors.PrimaryDark)
            .verticalScroll(scrollState)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Logo
        Box(
            modifier = Modifier.size(180.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "D-Shield Pro Logo"
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Log in",
            fontSize = 36.sp,
            fontWeight = FontWeight.Bold,
            color = goldColor
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(end = 24.dp)
            ) {
                RadioButton(
                    selected = uiState.selectedUserType == UserType.RETAILER,
                    onClick = { viewModel.onEvent(LoginEvent.OnUserTypeChanged(UserType.RETAILER)) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = goldColor,
                        unselectedColor = Color.Gray
                    )
                )
                Text(
                    text = "Retailer",
                    fontSize = 16.sp,
                    color = Color(0xFF5D4E37)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = uiState.selectedUserType == UserType.DISTRIBUTOR,
                    onClick = { viewModel.onEvent(LoginEvent.OnUserTypeChanged(UserType.DISTRIBUTOR)) },
                    colors = RadioButtonDefaults.colors(
                        selectedColor = goldColor,
                        unselectedColor = Color.Gray
                    )
                )
                Text(
                    text = "Distributor",
                    fontSize = 16.sp,
                    color = Color(0xFF5D4E37)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = uiState.phoneNumber,
            onValueChange = { viewModel.onEvent(LoginEvent.OnPhoneNumberChanged(it)) },
            label = { Text("Enter phone number") },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = uiState.phoneError.isNotEmpty(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = goldColor,
                unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                focusedLabelColor = goldColor,
                cursorColor = goldColor,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black
            ),
            shape = RoundedCornerShape(12.dp)
        )

        if (uiState.phoneError.isNotEmpty()) {
            Text(
                text = uiState.phoneError,
                color = MaterialTheme.colorScheme.error,
                fontSize = 12.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Method Toggle

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                TextButton(
                    onClick = { viewModel.onEvent(LoginEvent.OnLoginMethodChanged(LoginMethod.MPIN)) }
                ) {
                    Text(
                        text = "Login with M-PIN",
                        color = if (uiState.loginMethod == LoginMethod.MPIN) goldColor else Color.Gray,
                        fontWeight = if (uiState.loginMethod == LoginMethod.MPIN) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                TextButton(
                    onClick = { viewModel.onEvent(LoginEvent.OnLoginMethodChanged(LoginMethod.PASSWORD)) }
                ) {
                    Text(
                        text = "Login with password",
                        color = if (uiState.loginMethod == LoginMethod.PASSWORD) goldColor else Color.Gray,
                        fontWeight = if (uiState.loginMethod == LoginMethod.PASSWORD) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 14.sp
                    )
                }
            }


        Spacer(modifier = Modifier.height(16.dp))

        // Conditional Input Fields
        if (uiState.loginMethod == LoginMethod.MPIN) {
            // 4-Digit M-PIN Input (OTP Style)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Enter 4-digit M-PIN",
                    fontSize = 14.sp,
                    color = Color(0xFF5D4E37),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                OtpInputField(
                    otpText = uiState.mpin,
                    onOtpTextChange = { value, _ ->
                        viewModel.onEvent(LoginEvent.OnMpinChanged(value))
                    },
                    digitCount = 4,
                    goldColor = goldColor,
                    isError = uiState.mpinError.isNotEmpty()
                )

                if (uiState.mpinError.isNotEmpty()) {
                    Text(
                        text = uiState.mpinError,
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, top = 8.dp)
                    )
                }
            }
        } else {
            // Password Input
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { viewModel.onEvent(LoginEvent.OnPasswordChanged(it)) },
                label = { Text("Enter password") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                singleLine = true,
                visualTransformation = if (uiState.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { viewModel.onEvent(LoginEvent.OnPasswordVisibilityToggled) }) {
                        Icon(
                            imageVector = if (uiState.passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = if (uiState.passwordVisible) "Hide password" else "Show password",
                            tint = goldColor
                        )
                    }
                },
                isError = uiState.passwordError.isNotEmpty(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = goldColor,
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    focusedLabelColor = goldColor,
                    cursorColor = goldColor,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                shape = RoundedCornerShape(12.dp)
            )

            if (uiState.passwordError.isNotEmpty()) {
                Text(
                    text = uiState.passwordError,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Login Button
        Button(
            onClick = {
               viewModel.onEvent(LoginEvent.OnLoginClicked)
                      },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = darkGold
            ),
            shape = RoundedCornerShape(12.dp),
            enabled = !uiState.isLoading
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.Black,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = "Log In",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Set M-PIN button
        if (uiState.loginMethod == LoginMethod.PASSWORD) {
            TextButton(
                onClick = { viewModel.onEvent(LoginEvent.OnSetMpinClicked) }
            ) {
                // Empty content
            }
        }

        // Extra bottom space for better scrolling when keyboard is open
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
fun OtpInputField(
    otpText: String,
    onOtpTextChange: (String, Boolean) -> Unit,
    digitCount: Int = 4,
    goldColor: Color,
    isError: Boolean = false
) {
    BasicTextField(
        value = otpText,
        onValueChange = {
            if (it.length <= digitCount && it.all { char -> char.isDigit() }) {
                onOtpTextChange(it, it.length == digitCount)
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        visualTransformation = PasswordVisualTransformation(),
        decorationBox = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(digitCount) { index ->
                    val char = when {
                        index < otpText.length -> "●"
                        else -> ""
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp)
                            .border(
                                width = 2.dp,
                                color = when {
                                    isError -> MaterialTheme.colorScheme.error
                                    index < otpText.length -> goldColor
                                    else -> Color.Gray.copy(alpha = 0.5f)
                                },
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(
                                Color.White,
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = char,
                            style = TextStyle(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color.Black
                            )
                        )
                    }
                }
            }
        }
    )
}


