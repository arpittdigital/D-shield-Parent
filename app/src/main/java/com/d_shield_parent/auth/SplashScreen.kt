package com.d_shield_parent.auth

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.d_shield_parent.Dashboard.AppColors
import com.d_shield_parent.R
import com.d_shield_parent.SharedPreference.shareprefManager
import kotlinx.coroutines.delay

@Composable
fun Splash1(navController: NavController) {
    val context = LocalContext.current
    var isArrowVisible by remember { mutableStateOf(true) }

    LaunchedEffect(true) {
        // ✅ Use shareprefManager instead of direct SharedPreferences
        val token = shareprefManager.getToken()
        val userType = shareprefManager.getUserType()

        if (!token.isNullOrEmpty()) {
            isArrowVisible = false
            delay(2000)

            userType?.let {
                when(UserType.valueOf(it)) {
                    UserType.RETAILER -> navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Splash1.route) { inclusive = true }
                        launchSingleTop = true
                    }
                    UserType.DISTRIBUTOR -> navController.navigate(Routes.Dashboard.route) {
                        popUpTo(Routes.Splash1.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.PrimaryDark),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "DShield Pro Logo",
                modifier = Modifier
                    .size(280.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.weight(1f))

            if (isArrowVisible) {
                IconButton(
                    onClick = { navController.navigate(Routes.Splash2.route) },
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .size(48.dp)
                        .background(Color(0xFFD4AF37), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White
                    )
                }
            }
        }
    }
}



@Composable
fun Splash2(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.PrimaryDark)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Illustration Image
            Image(
                painter = painterResource(id = R.drawable.splash1img),
                contentDescription = "High Speed Illustration",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .padding(horizontal = 16.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "High Speed Faster",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your data absolutely safe because we encrypt all the data that linked",
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            // Page Indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                PageIndicator(isActive = false)
                Spacer(modifier = Modifier.width(8.dp))
                PageIndicator(isActive = true)
                Spacer(modifier = Modifier.width(8.dp))
                PageIndicator(isActive = false)
                Spacer(modifier = Modifier.width(8.dp))
                PageIndicator(isActive = false)
            }

            // Navigation Arrow - Fixed to navigate to Splash3
            IconButton(
                onClick = { navController.navigate(Routes.Splash3.route) },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFD4AF37), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next",
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun Splash3(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.PrimaryDark)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Illustration Image
            Image(
                painter = painterResource(id = R.drawable.splash2img),
                contentDescription = "Safe and Secured",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .padding(horizontal = 16.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "100% Safe & Secured",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your data absolutely safe because we encrypt all the data that linked",
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            // Page Indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                PageIndicator(isActive = false)
                Spacer(modifier = Modifier.width(8.dp))
                PageIndicator(isActive = false)
                Spacer(modifier = Modifier.width(8.dp))
                PageIndicator(isActive = true)
                Spacer(modifier = Modifier.width(8.dp))
                PageIndicator(isActive = false)
            }

            // Navigation Arrow - Fixed to navigate to Splash4
            IconButton(
                onClick = { navController.navigate(Routes.Splash4.route) },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFD4AF37), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next",
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun Splash4(navController: NavController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.PrimaryDark)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Illustration Image
            Image(
                painter = painterResource(id = R.drawable.splash3img),
                contentDescription = "3 Times Faster",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .padding(horizontal = 16.dp),
                contentScale = ContentScale.Fit
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "UPTO 3 Times Faster",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Your data absolutely safe because we encrypt all the data that linked",
                    fontSize = 14.sp,
                    color = Color.Black,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )
            }

            // Page Indicators
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                PageIndicator(isActive = false)
                Spacer(modifier = Modifier.width(8.dp))
                PageIndicator(isActive = false)
                Spacer(modifier = Modifier.width(8.dp))
                PageIndicator(isActive = false)
                Spacer(modifier = Modifier.width(8.dp))
                PageIndicator(isActive = true)
            }

            // Navigation Arrow - Navigate to Login or Home
            IconButton(
                onClick = {
                    // Navigate to main app screen
                    navController.navigate(Routes.Login.route) {
                        // Remove splash screens from back stack
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(Color(0xFFD4AF37), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Get Started",
                    tint = Color.Black
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PageIndicator(isActive: Boolean) {
    Box(
        modifier = Modifier
            .size(if (isActive) 10.dp else 8.dp)
            .background(
                color = if (isActive) Color(0xFFD4AF37) else Color.LightGray,
                shape = CircleShape
            )
    )
}