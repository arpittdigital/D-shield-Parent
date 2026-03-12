package com.d_shield_parent.Profile

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.d_shield_parent.Dashboard.ProfileViewModel
import com.d_shield_parent.R
import java.io.File


private val BrandBlue = Color(0xFF3949AB)
private val BrandBlueDark = Color(0xFF1A237E)
private val BrandBlueLight = Color(0xFF5C6BC0)
private val SurfaceGray = Color(0xFFF4F6FB)
private val CardWhite = Color(0xFFFFFFFF)
private val TextPrimary = Color(0xFF1A1F2E)
private val TextSecondary = Color(0xFF6B7280)
private val DividerColor = Color(0xFFF0F2F7)

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = viewModel()
) {
    val context = LocalContext.current
    val profileData by viewModel.profileData.collectAsState()
    val isLoggingOut by viewModel.isLoggingOut.collectAsState()
    val logoutSuccess by viewModel.logoutSuccess.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val showLogoutDialog by viewModel.showLogoutDialog.collectAsState()

    var showImagePickerDialog by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) showImagePickerDialog = true
        else Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
    }

    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraImageUri.value?.let { uri ->
                viewModel.updateProfileImage(uri)
                Toast.makeText(context, "Photo updated", Toast.LENGTH_SHORT).show()
            }
        }
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            viewModel.updateProfileImage(it)
            Toast.makeText(context, "Photo updated", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(logoutSuccess) {
        if (logoutSuccess) {
            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
            navController.navigate("login_screen") {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    // Image Picker Dialog
    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("Change Profile Photo", fontWeight = FontWeight.Bold) },
            text = { Text("Choose a source for your profile picture") },
            confirmButton = {
                Button(
                    onClick = {
                        showImagePickerDialog = false
                        try {
                            val photoFile = File(context.cacheDir, "profile_${System.currentTimeMillis()}.jpg")
                            val uri = FileProvider.getUriForFile(context, "${context.packageName}.provider", photoFile)
                            cameraImageUri.value = uri
                            cameraLauncher.launch(uri)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandBlue)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Camera")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = {
                    showImagePickerDialog = false
                    galleryLauncher.launch("image/*")
                }) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Gallery")
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Logout Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.hideLogoutDialog() },
            icon = {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.Red.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red, modifier = Modifier.size(28.dp))
                }
            },
            title = { Text("Logout?", fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center) },
            text = { Text("You'll need to sign in again to access your account.", textAlign = TextAlign.Center, color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = { viewModel.logout() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Yes, Logout", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { viewModel.hideLogoutDialog() },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) { Text("Cancel") }
            },
            containerColor = CardWhite,
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Loading
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize().background(BrandBlue),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator(color = Color.White, strokeWidth = 3.dp, modifier = Modifier.size(52.dp))
                Spacer(modifier = Modifier.height(16.dp))
                Text("Loading Profile...", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize().background(SurfaceGray)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            // ── Hero Header ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(270.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(BrandBlueDark, BrandBlue, BrandBlueLight)
                        )
                    )
            ) {
                // Back arrow
//                IconButton(
//                    onClick = { navController.navigateUp() },
//                    modifier = Modifier
//                        .align(Alignment.TopStart)
//                        .padding(top = 36.dp, start = 8.dp)
//                ) {
//                    Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", tint = Color.White)
//                }

                // Centered profile content
                Column(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        Image(
                            painter = if (profileData.profileImageUri != null)
                                rememberAsyncImagePainter(profileData.profileImageUri)
                            else painterResource(R.drawable.placeholder),
                            contentDescription = "Profile",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(3.dp, Color.White, CircleShape)
                                .clickable { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) }
                        )
                        // Camera badge
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .background(Color.White, CircleShape)
                                .border(1.5.dp, BrandBlue, CircleShape)
                                .clickable { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(14.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = profileData.username.ifEmpty { "Retailer" },
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(5.dp))
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 4.dp)
                    ) {
                        Text("Retailer", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }
            }

            // ── Stats Row — floating over header ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatsPill("Wallet", "₹${profileData.walletBalance}", Color(0xFF4CAF50), Icons.Outlined.AccountBalanceWallet, Modifier.weight(1f))
                StatsPill("Enrolled", "${profileData.enrolledDevices}", Color(0xFF2196F3), Icons.Outlined.Devices, Modifier.weight(1f))
                StatsPill("Active", "${profileData.activeDevices}", Color(0xFF9C27B0), Icons.Outlined.CheckCircle, Modifier.weight(1f))
                StatsPill("Locked", "${profileData.lockedDevices}", Color(0xFFFF5722), Icons.Outlined.Lock, Modifier.weight(1f))
            }

            // ── Profile Details Card ──
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-12).dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {

                    SectionLabel("Personal Info")

                    ProfileInfoRow(
                        icon = Icons.Outlined.Store,
                        label = "Shop Name",
                        value = profileData.shop_name.ifEmpty { "—" },
                        iconBg = Color(0xFFE8EAF6)
                    )
                    RowDivider()
                    ProfileInfoRow(
                        icon = Icons.Outlined.Person,
                        label = "Full Name",
                        value = profileData.username.ifEmpty { "—" },
                        iconBg = Color(0xFFE3F2FD)
                    )
                    RowDivider()
                    ProfileInfoRow(
                        icon = Icons.Outlined.Phone,
                        label = "Phone Number",
                        value = profileData.phoneNo.ifEmpty { "—" },
                        iconBg = Color(0xFFE8F5E9)
                    )

                    SectionLabel("Account Info")

                    ProfileInfoRow(
                        icon = Icons.Outlined.Badge,
                        label = "Unique ID",
                        value = profileData.uniqueId.ifEmpty { "—" },
                        iconBg = Color(0xFFFFF3E0),
                        valueColor = BrandBlue
                    )
                    RowDivider()
                    ProfileInfoRow(
                        icon = Icons.Outlined.Email,
                        label = "Email Address",
                        value = profileData.email.ifEmpty { "—" },
                        iconBg = Color(0xFFFCE4EC)
                    )
                    RowDivider()
                    ProfileInfoRow(
                        icon = Icons.Outlined.LocationOn,
                        label = "Address",
                        value = profileData.address.ifEmpty { "—" },
                        iconBg = Color(0xFFE8EAF6),
                        isLast = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // ── Logout Button ──
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-8).dp)
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                elevation = ButtonDefaults.buttonElevation(0.dp),
                onClick = { viewModel.showLogoutConfirmation() },
                enabled = !isLoggingOut
            ) {
                if (isLoggingOut) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Red, strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun SectionLabel(title: String) {
    Text(
        text = title.uppercase(),
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = BrandBlue,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 4.dp)
    )
}

@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    iconBg: Color = Color(0xFFF0F2F7),
    valueColor: Color = TextPrimary,
    isLast: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(iconBg, RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = BrandBlue, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = label, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 14.sp, color = valueColor, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun RowDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 68.dp, end = 16.dp),
        color = DividerColor,
        thickness = 1.dp
    )
}

@Composable
fun StatsPill(label: String, value: String, accentColor: Color, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.shadow(6.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 11.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(text = label, fontSize = 10.sp, color = TextSecondary)
        }
    }
}