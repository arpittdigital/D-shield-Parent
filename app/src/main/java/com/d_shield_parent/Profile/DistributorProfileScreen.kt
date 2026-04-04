//package com.d_shield_parent.Profile
//
//import android.Manifest
//import android.net.Uri
//import android.widget.Toast
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.Image
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.imePadding
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.size
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.text.KeyboardOptions
//import androidx.compose.foundation.text.selection.TextSelectionColors
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.ArrowBackIosNew
//import androidx.compose.material.icons.filled.CameraAlt
//import androidx.compose.material.icons.filled.Check
//import androidx.compose.material.icons.filled.Close
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material.icons.filled.EditOff
//import androidx.compose.material.icons.filled.Image
//import androidx.compose.material.icons.filled.Logout
//import androidx.compose.material.icons.outlined.Badge
//import androidx.compose.material.icons.outlined.Email
//import androidx.compose.material.icons.outlined.LocationOn
//import androidx.compose.material.icons.outlined.Lock
//import androidx.compose.material.icons.outlined.Person
//import androidx.compose.material.icons.outlined.Phone
//import androidx.compose.material.icons.outlined.Store
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Button
//import androidx.compose.material3.ButtonDefaults
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CircularProgressIndicator
//import androidx.compose.material3.HorizontalDivider
//import androidx.compose.material3.Icon
//import androidx.compose.material3.IconButton
//import androidx.compose.material3.OutlinedButton
//import androidx.compose.material3.OutlinedTextField
//import androidx.compose.material3.OutlinedTextFieldDefaults
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.painterResource
//import androidx.compose.ui.text.font.FontFamily
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.input.KeyboardType
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.core.content.FileProvider
//import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.lifecycle.viewmodel.initializer
//import androidx.lifecycle.viewmodel.viewModelFactory
//import androidx.navigation.NavController
//import coil.compose.rememberAsyncImagePainter
//import com.d_shield_parent.Dashboard.AppColors
//import com.d_shield_parent.Dashboard.ProfileViewModel
//import com.d_shield_parent.R
//import java.io.File
//import kotlin.text.ifEmpty
//
//@Composable
//fun ProfileScreen(
//    navController: NavController,
//    viewModel: ProfileViewModel = viewModel(
//        factory = viewModelFactory {
//            initializer {
//                val app = this[APPLICATION_KEY]!!
//                ProfileViewModel(app)
//            }
//        }
//    )
//) {
//    val context = LocalContext.current
//    val profileData by viewModel.profileData.collectAsState()
//    val isLoggingOut by viewModel.isLoggingOut.collectAsState()
//    val logoutSuccess by viewModel.logoutSuccess.collectAsState()
//    val isEditable by viewModel.isEditable.collectAsState()
//    val isLoading by viewModel.isLoading.collectAsState()
//    val errorMessage by viewModel.errorMessage.collectAsState()
//    val showLogoutDialog by viewModel.showLogoutDialog.collectAsState()
//
//    var showImagePickerDialog by remember { mutableStateOf(false) }
//
//    val cameraPermissionLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.RequestPermission()
//    ) { isGranted ->
//        if (isGranted) {
//            showImagePickerDialog = true
//        } else {
//            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    val cameraImageUri = remember { mutableStateOf<Uri?>(null) }
//    val cameraLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicture()
//    ) { success ->
//        if (success) {
//            cameraImageUri.value?.let { uri ->
//                viewModel.updateProfileImage(uri)
//                Toast.makeText(context, "Image captured successfully", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    val galleryLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let {
//            viewModel.updateProfileImage(it)
//            Toast.makeText(context, "Image selected successfully", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//    // ✅ Navigate to login after successful logout
//    LaunchedEffect(logoutSuccess) {
//        if (logoutSuccess) {
//            Toast.makeText(context, "Logged out successfully", Toast.LENGTH_SHORT).show()
//            navController.navigate("login_screen") {
//                popUpTo(0) { inclusive = true }
//            }
//        }
//    }
//
//    // ✅ Show error toast
//    LaunchedEffect(errorMessage) {
//        errorMessage?.let {
//            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
//            viewModel.clearError()
//        }
//    }
//
//    // ✅ Image Picker Dialog
//    if (showImagePickerDialog) {
//        AlertDialog(
//            onDismissRequest = { showImagePickerDialog = false },
//            title = {
//                Text(
//                    "Select Image Source",
//                    fontWeight = FontWeight.Bold,
//                    color = AppColors.PrimaryDark
//                )
//            },
//            text = {
//                Text("Choose where to pick your profile picture from")
//            },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        showImagePickerDialog = false
//                        try {
//                            val photoFile = File(
//                                context.cacheDir,
//                                "profile_${System.currentTimeMillis()}.jpg"
//                            )
//                            val uri = FileProvider.getUriForFile(
//                                context,
//                                "${context.packageName}.provider",
//                                photoFile
//                            )
//                            cameraImageUri.value = uri
//                            cameraLauncher.launch(uri)
//                        } catch (e: Exception) {
//                            Toast.makeText(context, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
//                        }
//                    },
//                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.PrimaryDark)
//                ) {
//                    Icon(Icons.Default.CameraAlt, contentDescription = null)
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("Camera")
//                }
//            },
//            dismissButton = {
//                Button(
//                    onClick = {
//                        showImagePickerDialog = false
//                        galleryLauncher.launch("image/*")
//                    },
//                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.Accent)
//                ) {
//                    Icon(Icons.Default.Image, contentDescription = null)
//                    Spacer(modifier = Modifier.width(8.dp))
//                    Text("Gallery")
//                }
//            }
//        )
//    }
//
//    // ✅ Logout Confirmation Dialog
//    if (showLogoutDialog) {
//        AlertDialog(
//            onDismissRequest = { viewModel.hideLogoutDialog() },
//            icon = {
//                Icon(
//                    imageVector = Icons.Default.Logout,
//                    contentDescription = null,
//                    tint = Color.Red,
//                    modifier = Modifier.size(48.dp)
//                )
//            },
//            title = {
//                Text(
//                    "Confirm Logout",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 20.sp
//                )
//            },
//            text = {
//                Text(
//                    "Are you sure you want to logout?",
//                    fontSize = 16.sp
//                )
//            },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        viewModel.logout()
//                    },
//                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
//                ) {
//                    Text("Yes, Logout", fontWeight = FontWeight.Bold)
//                }
//            },
//            dismissButton = {
//                OutlinedButton(
//                    onClick = { viewModel.hideLogoutDialog() }
//                ) {
//                    Text("Cancel")
//                }
//            },
//            containerColor = Color.White,
//            shape = RoundedCornerShape(16.dp)
//        )
//    }
//
//    // ✅ Show loading indicator
//    if (isLoading) {
//        Box(
//            modifier = Modifier.fillMaxSize().background(AppColors.PrimaryDark),
//            contentAlignment = Alignment.Center
//        ) {
//            Column(
//                horizontalAlignment = Alignment.CenterHorizontally,
//                verticalArrangement = Arrangement.Center
//            ) {
//                CircularProgressIndicator(
//                    color = Color.White,
//                    strokeWidth = 4.dp,
//                    modifier = Modifier.size(60.dp)
//                )
//                Spacer(modifier = Modifier.height(20.dp))
//                Text(
//                    "Loading Profile...",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 18.sp,
//                    color = Color.White
//                )
//            }
//        }
//        return
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .imePadding()
//            .verticalScroll(rememberScrollState())
//            .background(AppColors.PrimaryDark)
//            .padding(top = 30.dp),
//        horizontalAlignment = Alignment.Start,
//    ) {
//        Spacer(modifier = Modifier.height(5.dp))
//
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 10.dp, vertical = 10.dp),
//            horizontalArrangement = Arrangement.SpaceBetween,
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Row(verticalAlignment = Alignment.CenterVertically) {
//                IconButton(
//                    onClick = { navController.popBackStack() }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.ArrowBackIosNew,
//                        tint = Color.Black,
//                        contentDescription = "Back"
//                    )
//                }
//                Spacer(modifier = Modifier.width(18.dp))
//                Text(
//                    text = "Profile",
//                    color = Color.Black,
//                    fontWeight = FontWeight.W800,
//                    fontSize = 21.sp,
//                    fontFamily = FontFamily.Monospace
//                )
//                Spacer(modifier = Modifier.width(10.dp))
//                IconButton(onClick = {
//                    if (isEditable) viewModel.cancelEdit() else viewModel.enableEdit()
//                }) {
//                    Icon(
//                        imageVector = if (isEditable) Icons.Default.EditOff else Icons.Default.Edit,
//                        contentDescription = if (isEditable) "Cancel Edit" else "Edit Profile",
//                        tint = if (isEditable) Color.Red else AppColors.PrimaryDark
//                    )
//                }
//            }
//
//            Button(
//                onClick = { viewModel.showLogoutConfirmation() },
//                enabled = !isLoggingOut,
//                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
//                shape = RoundedCornerShape(12.dp),
//                elevation = ButtonDefaults.buttonElevation(4.dp)
//            ) {
//                if (isLoggingOut) {
//                    CircularProgressIndicator(
//                        modifier = Modifier.size(20.dp),
//                        color = Color.White,
//                        strokeWidth = 2.dp
//                    )
//                } else {
//                    Icon(
//                        imageVector = Icons.Default.Logout,
//                        contentDescription = "Logout",
//                        tint = Color.White
//                    )
//                    Spacer(modifier = Modifier.width(6.dp))
//                    Text("Logout", fontWeight = FontWeight.Bold)
//                }
//            }
//        }
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        Column(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Image(
//                painter = if (profileData.profileImageUri != null) {
//                    rememberAsyncImagePainter(profileData.profileImageUri)
//                } else {
//                    painterResource(R.drawable.placeholder)
//                },
//                contentDescription = "Profile Image",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier
//                    .size(130.dp)
//                    .clip(CircleShape)
//                    .background(Color.White, CircleShape)
//                    .border(3.dp, AppColors.PrimaryDark, CircleShape)
//                    .clickable {
//                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
//                    }
//            )
//        }
//        Spacer(modifier = Modifier.height(20.dp))
//        Text(
//            modifier = Modifier.align(Alignment.CenterHorizontally),
//            text = profileData.username.ifEmpty { "Loading..." },
//            fontWeight = FontWeight.Bold,
//            fontSize = 24.sp,
//            color = AppColors.PrimaryDark
//        )
//        Spacer(modifier = Modifier.height(15.dp))
//        // ✅ Stats Cards
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 15.dp),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            StatsCard("Wallet", "₹${profileData.walletBalance}")
//            StatsCard("Enrolled", "${profileData.enrolledDevices}")
//            StatsCard("Active", "${profileData.activeDevices}")
//            StatsCard("Locked", "${profileData.lockedDevices}")
//        }
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        Column(
//            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//
//            // ── Section header ──
//            Row(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(bottom = 16.dp),
//                verticalAlignment = Alignment.CenterVertically
//            ) {
//                HorizontalDivider(modifier = Modifier.weight(1f), color = AppColors.PrimaryDark.copy(alpha = 0.3f))
//                Text(
//                    text = "  Profile Details  ",
//                    fontSize = 13.sp,
//                    fontWeight = FontWeight.SemiBold,
//                    color = AppColors.PrimaryDark.copy(alpha = 0.6f)
//                )
//                HorizontalDivider(modifier = Modifier.weight(1f), color = AppColors.PrimaryDark.copy(alpha = 0.3f))
//            }
//
//            ProfileField(
//                label = "Name",
//                value = profileData.shop_name,
//                onValueChange = { viewModel.updateShopName(it) },
//                enabled = true,
//                icon = Icons.Outlined.Person
//            )
//            Spacer(modifier = Modifier.height(14.dp))
//
//            ProfileField(
//                label = "Phone Number",
//                value = profileData.phoneNo,
//                onValueChange = { viewModel.updatePhoneNo(it) },
//                enabled = true,
//                icon = Icons.Outlined.Phone,
//                keyboardType = KeyboardType.Phone
//            )
//            Spacer(modifier = Modifier.height(14.dp))
//
//            ProfileField(
//                label = "Unique ID",
//                value = profileData.uniqueId,
//                onValueChange = { viewModel.updateUniqueId(it) },
//                enabled = false,
//                icon = Icons.Outlined.Badge,
//                isLocked = true
//            )
//            Spacer(modifier = Modifier.height(14.dp))
//
//            ProfileField(
//                label = "Email Address",
//                value = profileData.email,
//                onValueChange = { viewModel.updateEmail(it) },
//                enabled = true,
//                icon = Icons.Outlined.Email
//            )
//            Spacer(modifier = Modifier.height(14.dp))
//
//            ProfileField(
//                label = "Store",
//                value = profileData.store,
//                onValueChange = { viewModel.updateStore(it) },
//                enabled = true,
//                icon = Icons.Outlined.Store
//            )
//            Spacer(modifier = Modifier.height(14.dp))
//
//            ProfileField(
//                label = "Address",
//                value = profileData.address,
//                onValueChange = { viewModel.updateAddress(it) },
//                enabled = true,
//                icon = Icons.Outlined.LocationOn
//            )
//            Spacer(modifier = Modifier.height(28.dp))
//
//            // ── Action Buttons ──
//            run {
//                Row(
//                    modifier = Modifier.fillMaxWidth(),
//                    horizontalArrangement = Arrangement.spacedBy(12.dp)
//                ) {
//                    Button(
//                        modifier = Modifier
//                            .weight(1f)
//                            .height(52.dp),
//                        shape = RoundedCornerShape(14.dp),
//                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.Success),
//                        elevation = ButtonDefaults.buttonElevation(6.dp),
//                        onClick = {
//                            viewModel.saveProfile()
//                            Toast.makeText(context, "Profile saved successfully!", Toast.LENGTH_SHORT).show()
//                        }
//                    ) {
//                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(20.dp))
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text("Save", fontSize = 16.sp, fontWeight = FontWeight.Bold)
//                    }
//
//                    OutlinedButton(
//                        modifier = Modifier
//                            .weight(1f)
//                            .height(52.dp),
//                        shape = RoundedCornerShape(14.dp),
//                        border = androidx.compose.foundation.BorderStroke(1.5.dp, AppColors.Danger),
//                        onClick = { viewModel.cancelEdit() }
//                    ) {
//                        Icon(Icons.Default.Close, contentDescription = null,
//                            modifier = Modifier.size(20.dp), tint = AppColors.Danger)
//                        Spacer(modifier = Modifier.width(8.dp))
//                        Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppColors.Danger)
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(50.dp))
//        }
//    }
//}
//
//@Composable
//fun StatsCard(label: String, value: String) {
//    Card(
//        modifier = Modifier
//            .width(80.dp)
//            .height(80.dp),
//        shape = RoundedCornerShape(12.dp),
//        colors = CardDefaults.cardColors(containerColor = Color.White),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.Center
//        ) {
//            Text(
//                text = value,
//                fontSize = 20.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.Black
//            )
//            Text(
//                text = label,
//                fontSize = 12.sp,
//                color = Color.Gray
//            )
//        }
//    }
//}
//
//@Composable
//fun ProfileField(
//    label: String,
//    value: String,
//    onValueChange: (String) -> Unit,
//    enabled: Boolean,
//    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Outlined.Person,
//    isLocked: Boolean = false,
//    keyboardType: KeyboardType = KeyboardType.Text
//) {
//    OutlinedTextField(
//        modifier = Modifier.fillMaxWidth(),
//        value = value,
//        onValueChange = onValueChange,
//        enabled = enabled && !isLocked,
//        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
//        label = { Text(label) },
//        leadingIcon = {
//            Icon(
//                imageVector = icon,
//                contentDescription = null,
//                modifier = Modifier.size(20.dp)
//            )
//        },
//        trailingIcon = if (isLocked) {
//            {
//                Icon(
//                    Icons.Outlined.Lock,
//                    contentDescription = "Read only",
//                    modifier = Modifier.size(18.dp)
//                )
//            }
//        } else null,
//        singleLine = label != "Address",
//        maxLines = if (label == "Address") 3 else 1,
//        shape = RoundedCornerShape(12.dp),
//        colors = OutlinedTextFieldDefaults.colors(
//            // Text colors
//            focusedTextColor = Color.Black,
//            unfocusedTextColor = Color.Black,
//            disabledTextColor = Color.Gray,
//
//            // Border colors — this is what makes border visible while typing
//            focusedBorderColor = Color(0xFF0000FF), // bright blue for testing
//            unfocusedBorderColor = Color(0xFF000000), // black
//            disabledBorderColor = Color(0xFFE0E0E0),
//
//            // Container (background) colors
//            focusedContainerColor = Color.White,
//            unfocusedContainerColor = Color.White,
//            disabledContainerColor = Color(0xFFF9FAFB),
//
//            // Label colors
//            focusedLabelColor = Color(0xFF333333),   // dark gray when focused
//            unfocusedLabelColor = Color(0xFF888888),
//            disabledLabelColor = Color.LightGray,
//
//            // Icon colors
//            focusedLeadingIconColor = AppColors.PrimaryDark,
//            unfocusedLeadingIconColor = Color.Gray,
//            disabledLeadingIconColor = Color.LightGray,
//
//            // Cursor
//            cursorColor = AppColors.PrimaryDark,
//
//            // Selection highlight
//            selectionColors = TextSelectionColors(
//                handleColor = AppColors.PrimaryDark,
//                backgroundColor = AppColors.PrimaryDark.copy(alpha = 0.2f)
//            )
//        )
//    )
//}