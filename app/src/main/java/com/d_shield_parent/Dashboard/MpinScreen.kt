package com.d_shield_parent.Dashboard

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIos
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d_shield_parent.Dashboard.viewModel.MpinState
import com.d_shield_parent.Dashboard.viewModel.MpinViewmodel
import com.d_shield_parent.SharedPreference.shareprefManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MpinScreen(
    navController: NavController,
    viewmodel: MpinViewmodel = viewModel()
) {
    var mobileNo by remember { mutableStateOf("") }
    var mpin by remember { mutableStateOf("") }
    var mpinVisible by remember { mutableStateOf(false) }
    var mobileNoError by remember { mutableStateOf<String?>(null) }
    var mpinError by remember { mutableStateOf<String?>(null) }

    val mpinSuccess by viewmodel.mpinState.collectAsState()
    val context = LocalContext.current

    // Navigation effect
    LaunchedEffect(mpinSuccess) {
        if (mpinSuccess is MpinState.Success) {
            navController.navigate("dashboard_screen") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    // Validation functions
    fun validateMobileNo(mobile: String): Boolean {
        return when {
            mobile.isEmpty() -> {
                mobileNoError = "Mobile number is required"
                false
            }
            mobile.length != 10 -> {
                mobileNoError = "Mobile number must be 10 digits"
                false
            }
            !mobile.all { it.isDigit() } -> {
                mobileNoError = "Mobile number must contain only digits"
                false
            }
            else -> {
                mobileNoError = null
                true
            }
        }
    }

    fun validateMpin(pin: String): Boolean {
        return when {
            pin.isEmpty() -> {
                mpinError = "M-Pin is required"
                false
            }
            pin.length < 4 -> {
                mpinError = "M-Pin must be at least 4 digits"
                false
            }

            !pin.all { it.isDigit() } -> {
                mpinError = "M-Pin must contain only digits"
                false
            }
            else -> {
                mpinError = null
                true
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.PrimaryDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 50.dp, bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIos,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }

                Text(
                    text = "Create M-Pin",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.weight(1f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                // Spacer for symmetry
                Spacer(modifier = Modifier.width(48.dp))
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Mobile Number Field
            Text(
                text = "Mobile Number",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = mobileNo,
                onValueChange = {
                    if (it.length <= 10) {
                        mobileNo = it.filter { char -> char.isDigit() }
                        mobileNoError = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter 10-digit mobile number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = mobileNoError != null,
                supportingText = {
                    mobileNoError?.let {
                        Text(
                            text = it,
                            color = Color.Black,

                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFff5722),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // M-Pin Field
            Text(
                text = "M-Pin",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = mpin,
                onValueChange = {
                    if (it.length <= 6) {
                        mpin = it.filter { char -> char.isDigit() }
                        mpinError = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Enter 4 digit M-Pin") },
                visualTransformation = if (mpinVisible)
                    VisualTransformation.None
                else
                    PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                trailingIcon = {
                    IconButton(onClick = { mpinVisible = !mpinVisible }) {
                        Icon(
                            imageVector = if (mpinVisible)
                                Icons.Default.VisibilityOff
                            else
                                Icons.Default.Visibility,
                            contentDescription = if (mpinVisible)
                                "Hide M-Pin"
                            else
                                "Show M-Pin"
                        )
                    }
                },
                isError = mpinError != null,
                supportingText = {
                    mpinError?.let {
                        Text(
                            text = it,
                            color = Color.Black
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFff5722),
                    unfocusedBorderColor = Color.Gray.copy(alpha = 0.5f),
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                )
            )

            Spacer(modifier = Modifier.weight(1f))

            // Submit Button
            Button(
                onClick = {
                    val isMobileValid = validateMobileNo(mobileNo)
                    val isMpinValid = validateMpin(mpin)
                  val savedPhone = shareprefManager.getPhone() ?: ""

                    if (isMobileValid && isMpinValid) {
                        if (mobileNo != savedPhone) {
                            mobileNoError = "Number doesn't match your login number"
                            Toast.makeText(
                                context,
                                "Mobile number must match the number you logged in with",
                                Toast.LENGTH_LONG
                            ).show()
                        } else {
                            viewmodel.setMpin(mpin = mpin)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFff5722)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp
                )
            ) {
                Text(
                    text = "Submit",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        if (mpinSuccess is MpinState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFFff5722))
            }
        }
    }
}