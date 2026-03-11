package com.d_shield_parent.presentation.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.d_shield_parent.R

@Composable
fun HelpScreen(navController: NavController) {

    val context        = LocalContext.current
    var complaint      by remember { mutableStateOf("") }
    var showSubmitted  by remember { mutableStateOf(false) }

    val phoneNumber = "+91 9667371301"
    val email       = "Support@DShieldpro.com"
    val whatsappUrl = "https://wa.me/917428437677"
    val instagramUrl= "https://instagram.com/dshield"
    val youtubeUrl  = "https://youtube.com/@dshield"

    if (showSubmitted) {
        AlertDialog(
            onDismissRequest = { showSubmitted = false },
            title = { Text("✅ Submitted!") },
            text  = { Text("Tumhari complaint submit ho gayi. Hum jald reply karenge.") },
            confirmButton = {
                Button(
                    onClick = { showSubmitted = false },
                    colors  = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A3ADB))
                ) { Text("OK", color = Color.White) }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFD6E4FF), Color(0xFFF0F4FF), Color(0xFFD6E4FF))
                )
            )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 25.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.clickable { navController.navigateUp() }
            )
            Spacer(Modifier.width(16.dp))
            Text(
                "Help",
                fontSize   = 20.sp,
                fontWeight = FontWeight.Bold,
                color      = Color(0xFF1A3ADB)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(Modifier.height(8.dp))

            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(20.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier            = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    Text(
                        "Get in touch",
                        fontSize   = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color(0xFF1A3ADB)
                    )

                    Spacer(Modifier.height(10.dp))

                    Text(
                        "If you have any inquiries get in touch with us.\nWe'll be happy to help you.",
                        fontSize  = 14.sp,
                        color     = Color(0xFF444444),
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(Modifier.height(28.dp))

                    InfoField(
                        label = "Call us",
                        value = phoneNumber,
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+$phoneNumber"))
                            context.startActivity(intent)
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    InfoField(
                        label = "Email us",
                        value = email,
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("mailto:$email")
                            }
                            context.startActivity(intent)
                        }
                    )

                    Spacer(Modifier.height(16.dp))

                    // ── Write a Complaint ─────────────────
                    Text(
                        "Write a Complaint",
                        fontSize   = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color(0xFF1A3ADB),
                        modifier   = Modifier.fillMaxWidth()
                    )

                    Spacer(Modifier.height(8.dp))

                    OutlinedTextField(
                        value         = complaint,
                        onValueChange = { complaint = it },
                        placeholder   = { Text("Type here.......", color = Color.Gray) },
                        modifier      = Modifier
                            .fillMaxWidth()
                            .height(140.dp),
                        shape  = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = Color(0xFF1A3ADB),
                            unfocusedBorderColor = Color.Transparent,
                            focusedContainerColor   = Color(0xFFE8EEFF),
                            unfocusedContainerColor = Color(0xFFE8EEFF)
                        )
                    )

                    Spacer(Modifier.height(16.dp))

                    // ── Submit Button ─────────────────────
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (complaint.isNotEmpty()) {
                                    complaint     = ""
                                    showSubmitted = true
                                }
                            },
                            enabled  = complaint.isNotEmpty(),
                            modifier = Modifier.width(140.dp).height(50.dp),
                            shape    = RoundedCornerShape(12.dp),
                            colors   = ButtonDefaults.buttonColors(
                                containerColor         = Color(0xFF1A3ADB),
                                disabledContainerColor = Color(0xFFAAAAAA)
                            )
                        ) {
                            Text("Submit", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Privacy & Terms ───────────────────────────
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "Privacy Policy",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF222222),
                    modifier   = Modifier.clickable { /* navigate to privacy */ }
                )
                Spacer(Modifier.width(24.dp))
                Text(
                    "Terms and Condition",
                    fontSize   = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color      = Color(0xFF222222),
                    modifier   = Modifier.clickable { /* navigate to terms */ }
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Social Media ──────────────────────────────
            Text(
                "Connect with Our Social Media",
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = Color(0xFF222222)
            )

            Spacer(Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                // WhatsApp
                SocialIcon(
                    iconColor = Color(0xFF25D366),
                    iconRes = R.drawable.whatsappimg,
                    onClick = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl)))
                    }
                )

                Spacer(Modifier.width(20.dp))

//                // Instagram
//                SocialIcon(
//                    iconColor = Color(0xFF833AB4),
//                    iconRes = R.drawable.instagramimg,
//                    onClick = {
//                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl)))
//                    }
//                )

                Spacer(Modifier.width(20.dp))

//                // YouTube
//                SocialIcon(
//                    iconRes = R.drawable.ic_youtube,
//                    onClick = {
//                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(youtubeUrl)))
//                    }
//                )
            }

            Spacer(Modifier.height(30.dp))
        }
    }
}


@Composable
fun InfoField(label: String, value: String, onClick: () -> Unit) {
    Text(
        text       = label,
        fontSize   = 15.sp,
        fontWeight = FontWeight.Bold,
        color      = Color(0xFF1A3ADB),
        modifier   = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFFE8EEFF))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Text(value, fontSize = 15.sp, color = Color(0xFF444444))
    }
}

@Composable
fun SocialIcon(
    iconColor: Color = Color.Gray,
    iconRes  : Int,
    onClick  : () -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .clip(CircleShape)
            .background(iconColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter            = painterResource(id = iconRes),
            contentDescription = null,
            tint               = Color.White,
            modifier           = Modifier.size(28.dp)
        )
    }
}
