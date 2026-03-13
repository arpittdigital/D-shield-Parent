package com.d_shield_parent.presentation.auth

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import com.d_shield_parent.R

// ── Brand Colors ──────────────────────────────────────────────────────────────
private val BrandBlue      = Color(0xFF1A3ADB)
private val BrandBlueDark  = Color(0xFF0F2299)
private val BrandBlueLight = Color(0xFFE8EEFF)
private val AccentTeal     = Color(0xFF00C2B2)
private val TextPrimary    = Color(0xFF111827)
private val TextSecondary  = Color(0xFF6B7280)
private val DividerColor   = Color(0xFFE5E7EB)

@Composable
fun HelpScreen(navController: NavController) {

    val context       = LocalContext.current
    var complaint     by remember { mutableStateOf("") }
    var showSuccess   by remember { mutableStateOf(false) }
    var isSubmitting  by remember { mutableStateOf(false) }
    var showPrivacy   by remember { mutableStateOf(false) }
    var showTerms     by remember { mutableStateOf(false) }

    val phoneNumber  = "+91 9667371301"
    val email        = "Support@DShieldpro.com"
    val whatsappUrl  = "https://wa.me/917428437677"
    val instagramUrl = "https://instagram.com/dshield"

    // ── Success Dialog ────────────────────────────────────────────────────────
    if (showSuccess) {
        SuccessDialog(onDismiss = { showSuccess = false })
    }
    if (showPrivacy) {
        PolicyDialog(
            title     = "Privacy Policy",
            content   = """
                Last updated: March 2026

                1. Information We Collect
                We collect information you provide directly, such as your name, email address, and device details when you register or use D-Shield.

                2. How We Use Your Information
                We use the information to provide parental control features, send important updates, respond to support requests, and improve our services.

                3. Data Sharing
                We do not sell your personal data. We may share data with trusted service providers who assist us in operating our app, subject to confidentiality agreements.

                4. Data Retention
                We retain your data for as long as your account is active or as needed to provide services. You may request deletion at any time.

                5. Security
                We use industry-standard encryption and security measures to protect your data.

                6. Contact Us
                For privacy-related queries, email us at Support@DShieldpro.com
            """.trimIndent(),
            onDismiss = { showPrivacy = false }
        )
    }
    if (showTerms) {
        PolicyDialog(
            title     = "Terms & Conditions",
            content   = """
                Last updated: March 2026

                1. Acceptance of Terms
                By downloading or using D-Shield, you agree to be bound by these Terms & Conditions.

                2. Use of the App
                D-Shield is intended for lawful parental monitoring of minor children's devices. You must not use the app for any unlawful surveillance or unauthorized monitoring.

                3. Account Responsibility
                You are responsible for maintaining the confidentiality of your account credentials and for all activities that occur under your account.

                4. Subscription & Billing
                Subscription fees are billed in advance. Refunds are subject to our refund policy. We reserve the right to change pricing with prior notice.

                5. Limitation of Liability
                D-Shield shall not be liable for any indirect, incidental, or consequential damages arising from the use of our service.

                6. Termination
                We reserve the right to suspend or terminate accounts that violate these terms.

                7. Changes to Terms
                We may update these terms from time to time. Continued use of the app constitutes acceptance of the revised terms.

                8. Contact
                For any queries, reach us at Support@DShieldpro.com
            """.trimIndent(),
            onDismiss = { showTerms = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F8FC))
    ) {

        // ── Top App Bar ───────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .shadow(elevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 48.dp, bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BrandBlueLight)
                        .clickable { navController.navigateUp() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = BrandBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(Modifier.width(14.dp))
                Text(
                    "Help & Support",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )
            }
        }

        // ── Scrollable Content ────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            // ── Hero Banner ───────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(BrandBlue, BrandBlueDark)
                        )
                    )
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        "We're here to help 👋",
                        fontSize   = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Reach out to us anytime.",
                        fontSize   = 13.sp,
                        color      = Color.White.copy(alpha = 0.8f),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Contact Options ───────────────────────────────────────────────
            Text(
                "Contact Us",
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextSecondary,
                modifier   = Modifier.padding(start = 4.dp, bottom = 10.dp)
            )

            ContactCard(
                label       = "Call Us",
                value       = phoneNumber,
                actionIcon  = Icons.Default.Phone,
                actionBg    = Color(0xFF22C55E),
                actionTint  = Color.White,
                onClick     = {
                    context.startActivity(
                        Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                    )
                }
            )

            Spacer(Modifier.height(10.dp))

            ContactCard(
                label      = "Email Us",
                value      = email,
                actionIcon = Icons.Default.Email,
                actionBg   = BrandBlue,
                actionTint = Color.White,
                onClick    = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$email")
                    }
                    context.startActivity(intent)
                }
            )

            Spacer(Modifier.height(24.dp))

            // ── Complaint Box ─────────────────────────────────────────────────
            Text(
                "Send a Message",
                fontSize   = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextSecondary,
                modifier   = Modifier.padding(start = 4.dp, bottom = 10.dp)
            )

            Card(
                modifier  = Modifier.fillMaxWidth(),
                shape     = RoundedCornerShape(16.dp),
                colors    = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                        value         = complaint,
                        onValueChange = { complaint = it },
                        placeholder   = {
                            Text(
                                "Describe your issue in detail...",
                                color    = Color(0xFFBBBBBB),
                                fontSize = 14.sp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp),
                        shape  = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor      = BrandBlue,
                            unfocusedBorderColor    = DividerColor,
                            focusedContainerColor   = Color.White,
                            unfocusedContainerColor = Color(0xFFFAFAFA)
                        )
                    )

                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            "${complaint.length} / 500",
                            fontSize = 12.sp,
                            color    = TextSecondary
                        )

                        Button(
                            onClick = {
                                if (complaint.isNotEmpty()) {
                                    complaint   = ""
                                    showSuccess = true
                                }
                            },
                            enabled  = complaint.isNotEmpty(),
                            shape    = RoundedCornerShape(10.dp),
                            colors   = ButtonDefaults.buttonColors(
                                containerColor         = BrandBlue,
                                disabledContainerColor = Color(0xFFD1D5DB)
                            ),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                        ) {
                            Text(
                                "Submit",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color      = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Social Media ──────────────────────────────────────────────────
            Text(
                "Reach Out Us On",
                fontSize   = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color      = TextSecondary,
                modifier   = Modifier.padding(start = 4.dp, bottom = 10.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SocialChip(
                    label    = "WhatsApp",
                    color    = Color(0xFF44E882),
                    iconRes  = R.drawable.whatsappimg,
//                    modifier = Modifier.weight(0.1f),
                    onClick  = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(whatsappUrl)))
                    }
                )
                SocialChip(
                    label    = "Instagram",
                    color    = Color(0xFFEA4B80),
                    iconRes  = R.drawable.instagramimg,
                    modifier = Modifier.weight(0.1f),
                    onClick  = {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(instagramUrl)))
                    }
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Footer Links ──────────────────────────────────────────────────
            Divider(color = DividerColor)
            Spacer(Modifier.height(16.dp))

            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                Text(
                    "Privacy Policy",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color      = BrandBlue,
                    modifier   = Modifier.clickable(
                        indication            = null,
                        interactionSource     = remember { MutableInteractionSource() },
                        onClick               = { showPrivacy = true }
                    )
                )
                Spacer(Modifier.width(8.dp))
                Text("·", color = TextSecondary, fontSize = 16.sp)
                Spacer(Modifier.width(8.dp))
                Text(
                    "Terms & Conditions",
                    fontSize   = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color      = BrandBlue,
                    modifier   = Modifier.clickable(
                        indication        = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick           = { showTerms = true }
                    )
                )
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Contact Card ──────────────────────────────────────────────────────────────
@Composable
fun ContactCard(
    label      : String,
    value      : String,
    actionIcon : ImageVector,
    actionBg   : Color,
    actionTint : Color,
    onClick    : () -> Unit
) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier          = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Label + value
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
                Spacer(Modifier.height(2.dp))
                Text(value, fontSize = 15.sp, color = TextPrimary, fontWeight = FontWeight.SemiBold)
            }

            // Right action icon — clickable
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(actionBg)
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    actionIcon,
                    contentDescription = label,
                    tint     = actionTint,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

// ── Social Chip ───────────────────────────────────────────────────────────────
@Composable
fun SocialChip(
    label   : String,
    color   : Color,
    iconRes : Int,
    modifier: Modifier = Modifier,
    onClick : () -> Unit
) {
    Card(
        modifier  = modifier.clickable { onClick() },
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier          = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter            = painterResource(id = iconRes),
                    contentDescription = null,
                    tint               = Color.Unspecified,
                    modifier           = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
    }
}

// ── Policy Dialog (Privacy / Terms) ──────────────────────────────────────────
@Composable
fun PolicyDialog(title: String, content: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties       = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier  = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 20.dp),
            shape     = RoundedCornerShape(24.dp),
            colors    = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Header
                Row(
                    modifier          = Modifier
                        .fillMaxWidth()
                        .background(BrandBlue)
                        .padding(horizontal = 24.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        title,
                        fontSize   = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color      = Color.White,
                        modifier   = Modifier.weight(1f)
                    )
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onDismiss() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("✕", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Scrollable content
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {
                    content.split("\n\n").forEach { paragraph ->
                        val isHeading = paragraph.matches(Regex("^\\d+\\..*"))
                        Text(
                            text       = paragraph,
                            fontSize   = if (isHeading) 14.sp else 13.sp,
                            fontWeight = if (isHeading) FontWeight.Bold else FontWeight.Normal,
                            color      = if (isHeading) TextPrimary else TextSecondary,
                            lineHeight = 20.sp
                        )
                        Spacer(Modifier.height(12.dp))
                    }
                }

                // Close button
                Box(modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Button(
                        onClick        = onDismiss,
                        modifier       = Modifier.fillMaxWidth(),
                        shape          = RoundedCornerShape(12.dp),
                        colors         = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                        contentPadding = PaddingValues(vertical = 14.dp)
                    ) {
                        Text("Close", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                    }
                }
            }
        }
    }
}
@Composable
fun SuccessDialog(onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties       = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier  = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape     = RoundedCornerShape(24.dp),
            colors    = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier            = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEEF9EE)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint     = Color(0xFF22C55E),
                        modifier = Modifier.size(40.dp)
                    )
                }

                Spacer(Modifier.height(20.dp))

                Text(
                    "Message Sent!",
                    fontSize   = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color      = TextPrimary
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Your complaint has been submitted.\nWe'll get back to you shortly.",
                    fontSize   = 14.sp,
                    color      = TextSecondary,
                    textAlign  = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick  = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape    = RoundedCornerShape(12.dp),
                    colors   = ButtonDefaults.buttonColors(containerColor = BrandBlue),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Text("Done", fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                }
            }
        }
    }
}