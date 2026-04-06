package com.d_shield_parent.Dashboard

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.d_shield_parent.Dashboard.viewModel.AddCustomerViewModel
import com.d_shield_parent.Dashboard.viewModel.CustomerViewModel
import com.d_shield_parent.R
import com.d_shield_parent.RoomDatabase.addCustomerList
import com.d_shield_parent.navigation.Routes
import com.d_shield_parent.otp.SmsBroadcastReceiver
import com.d_shield_parent.viewModel.DocumentViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AddCustomerFlow(navController: NavController,
                    onCustomerAdded: () -> Unit = {}) {

    val viewModel: AddCustomerViewModel = viewModel()
    val context = LocalContext.current

    //  Add SMS Receiver
    val smsReceiver = remember { SmsBroadcastReceiver() }

    //  Register/Unregister SMS Receiver
    DisposableEffect(Unit) {
        val filter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)

        // Use ContextCompat — fixes lint warning on all API levels
        ContextCompat.registerReceiver(
            context,
            smsReceiver,
            filter,
            ContextCompat.RECEIVER_EXPORTED  // SMS comes from Google Play Services (external)
        )

        onDispose {
            context.unregisterReceiver(smsReceiver)
        }
    }


        //  Start SMS Retriever when OTP is sent + auto-fill OTP
    LaunchedEffect(viewModel.isOtpSent) {
        if (viewModel.isOtpSent) {
            SmsRetriever.getClient(context).startSmsRetriever()

            smsReceiver.otpReceiveListener = object : SmsBroadcastReceiver.OtpReceiveListener {
                override fun onOtpReceived(receivedOtp: String) {

                    viewModel.otp = receivedOtp //  Syncs to ViewModel
                }
                override fun onOtpTimeout() {
                    // Optional: show resend option
                }
            }
        }
    }
//    LaunchedEffect(viewModel.otp) {
//        otp = viewModel.otp
//    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) { }
    }

    // ... rest of your existing code unchanged
    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    var currentPage by remember { mutableStateOf(0) }
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var aadharCard by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var pancard by remember { mutableStateOf("") }
    var alternate by remember { mutableStateOf("") }
    var productName by remember { mutableStateOf("") }
    var serialNumber by remember { mutableStateOf("") }
    var imei1 by remember { mutableStateOf("") }
    var imei2 by remember { mutableStateOf("") }
    var totalAmount by remember { mutableStateOf("") }
    var downPayment by remember { mutableStateOf("") }
    var loanAmount by remember { mutableStateOf("") }
    var rateOfInterest by remember { mutableStateOf("") }
    var interestAmount by remember { mutableStateOf("") }
    var totalAmountWithInterest by remember { mutableStateOf("") }
    var monthlyInstallment by remember { mutableStateOf("") }
    var totalInstallment by remember { mutableStateOf("") }
    var loanFrequency by remember { mutableStateOf("") }
    var emiDay by remember { mutableStateOf("") }
    var agreementDate by remember { mutableStateOf("") }
    var firstInstallment by remember { mutableStateOf("") }
    var billingInvoice by remember { mutableStateOf("") }
    var emiDates by remember { mutableStateOf(listOf<String>()) }
    var showValidationError by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize().background(AppColors.PrimaryDark)) {
        Spacer(modifier = Modifier.height(25.dp))
        Row(
            modifier = Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (currentPage > 0) currentPage-- else navController.popBackStack()
            }) {
                Icon(Icons.Default.ArrowBackIosNew, "Back", tint = Color.Black)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when (currentPage) {
                    0 -> "Add Customer Details"
                    1 -> "Add Product Details"
                    else -> "Upload Document"
                },
                color = Color.Black,
                fontWeight = FontWeight.W800,
                fontSize = 21.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        LinearProgressIndicator(
            progress = (currentPage + 1) / 3f,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).height(4.dp),
            color = Color(0xFFff5722),
            trackColor = Color.White.copy(alpha = 0.3f)
        )

        when (currentPage) {
            0 -> CustomerDetailsPage(
                name = name, onNameChange = { name = it },
                email = email, onEmailChange = { email = it },
                aadharCard = aadharCard, onAadharCardChange = { aadharCard = it },
                address = address, onAddressChange = { address = it },
                pancard = pancard, onPanCardChange = { pancard = it },
                alternate = alternate, onAlternateChange = { alternate = it },
                vm = viewModel,             //  Pass AddCustomerViewModel
                onNext = { currentPage = 1 }
            )
            1 -> ProductDetailsPage(
                productName, { productName = it },
                serialNumber, { serialNumber = it },
                imei1, { imei1 = it },
                imei2, { imei2 = it },
                emiDay, { emiDay = it },
                totalAmount, { totalAmount = it },
                downPayment, { downPayment = it },
                loanAmount, { loanAmount = it },
                rateOfInterest, { rateOfInterest = it },
                interestAmount, { interestAmount = it },
                totalAmountWithInterest, { totalAmountWithInterest = it },
                monthlyInstallment, { monthlyInstallment = it },
                totalInstallment, { totalInstallment = it },
                loanFrequency, { loanFrequency = it },
                agreementDate, { agreementDate = it },
                firstInstallment, { firstInstallment = it },
                billingInvoice, { billingInvoice = it },
                emiDates, { emiDates = it },
                { currentPage = 2 }
            )
            2 -> UploadDocumentPage(
                name = name,
                mobile = viewModel.mobile,
                email = email,
                aadharCard, address, pancard, alternate, productName, imei1, imei2, totalAmount, emiDay,
                loanAmount, loanFrequency, rateOfInterest, agreementDate, firstInstallment,
                downPayment, billingInvoice, totalInstallment, emiDates, monthlyInstallment,
                { currentPage = 1 }, navController,
                onCustomerAdded = onCustomerAdded
            )
        }
    }
}

fun calculateEMIDetails(
    productPrice: String,
    downPayment: String,
    rateOfInterest: String,
    totalInstallments: String
): EMICalculation {
    val price = productPrice.toDoubleOrNull() ?: 0.0
    val down = downPayment.toDoubleOrNull() ?: 0.0
    val rate = rateOfInterest.toDoubleOrNull() ?: 0.0
    val installments = totalInstallments.toIntOrNull() ?: 0

    if (price <= 0 || installments <= 0) return EMICalculation()

    val loanAmount = price - down
    val interestAmount = (loanAmount * rate) / 100
    val totalAmountWithInterest = loanAmount + interestAmount
    val monthlyInstallment = totalAmountWithInterest / installments

    return EMICalculation(
        loanAmount = String.format("%.2f", loanAmount),
        interestAmount = String.format("%.2f", interestAmount),
        totalAmountWithInterest = String.format("%.2f", totalAmountWithInterest),
        monthlyInstallment = String.format("%.2f", monthlyInstallment)
    )
}

data class EMICalculation(
    val loanAmount: String = "",
    val interestAmount: String = "",
    val totalAmountWithInterest: String = "",
    val monthlyInstallment: String = ""
)

fun generateEmiDates(firstDate: String, totalInstallments: Int): List<String> {
    if (firstDate.isEmpty() || totalInstallments <= 0) return emptyList()
    val dateFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    val calendar = Calendar.getInstance()
    try {
        calendar.time = dateFormat.parse(firstDate) ?: return emptyList()
    } catch (e: Exception) {
        return emptyList()
    }
    val dates = mutableListOf<String>()
    dates.add(firstDate)
    for (i in 1 until totalInstallments) {
        calendar.add(Calendar.MONTH, 1)
        dates.add(dateFormat.format(calendar.time))
    }
    return dates
}

// ─── Page 1: Customer Details ─────────────────────────────────────────────────

@Composable
fun CustomerDetailsPage(
    name: String, onNameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    aadharCard: String, onAadharCardChange: (String) -> Unit,
    address: String, onAddressChange: (String) -> Unit,
    pancard: String, onPanCardChange: (String) -> Unit,
    alternate: String, onAlternateChange: (String) -> Unit,
    vm: AddCustomerViewModel,   // Pass AddCustomerViewModel directly
    onNext: () -> Unit
) {
    var showValidationError by remember { mutableStateOf(false) }
    var validationMessage by remember { mutableStateOf("") }
    // Removed: val vm: OtpViewModel = viewModel()

    fun validateFields(): Boolean {
        return when {
            name.isBlank() -> { validationMessage = "Please enter customer name"; false }
            vm.mobile.length != 10 -> { validationMessage = "Please enter valid 10-digit mobile number"; false }
            !vm.isOtpVerified -> { validationMessage = "Please verify your mobile number"; false }
            email.isNotBlank() && !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                validationMessage = "Please enter valid email address"; false
            }
            aadharCard.isNotBlank() && aadharCard.length != 12 -> {
                validationMessage = "Aadhaar number must be 12 digits"; false
            }
            address.isBlank() -> { validationMessage = "Please enter address"; false }
            else -> true
        }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp, vertical = 15.dp)
                .height(180.dp)
                .background(Color(0xFFE8F5E9)),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painterResource(R.drawable.addcusimg), null,
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {

            CustomerTextField(
                label = "Name *",
                value = name,
                leadingIcon = Icons.Default.Person,
                onValueChange = onNameChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mobile from AddCustomerViewModel
            // Mobile field — locked after OTP verified
            CustomerTextField(
                label = "Mobile Number *",
                value = vm.mobile,
                keyboardType = KeyboardType.Phone,
                leadingIcon = Icons.Default.Phone,
                readOnly = vm.isOtpVerified,
                onValueChange = {
                    if (!vm.isOtpVerified) vm.mobile = it  // Lock field after verified
                },
                enabled = !vm.isOtpVerified  // Disable field after verified
            )

            Spacer(modifier = Modifier.height(10.dp))

// Error message
            vm.errorMessage?.let {
                Text(
                    text = it,
                    color = Color.Red,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

// Hide entire OTP section after verified
            if (!vm.isOtpVerified) {

                // Send OTP Button
                if (!vm.isOtpSent) {
                    Button(
                        onClick = { vm.sendOtp() },
                        enabled = !vm.isLoading
                    ) {
                        if (vm.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Send OTP")
                        }
                    }
                }

                // OTP Field + Verify Button
                if (vm.isOtpSent) {
                    CustomerTextField(
                        label = "OTP",
                        value = vm.otp,
                        keyboardType = KeyboardType.NumberPassword,
                        leadingIcon = Icons.Default.Lock,
                        onValueChange = { vm.otp = it }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { vm.verifyOtp() },
                        enabled = !vm.isLoading
                    ) {
                        if (vm.isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Verify OTP")
                        }
                    }
                }

            } else {
                // Show verified badge with locked icon instead of OTP section
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                ) {
                    Icon(
                        Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Mobile Verified",
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Spacer(modifier = Modifier.height(12.dp))

            CustomerTextField(
                label = "E-mail",
                value = email,
                keyboardType = KeyboardType.Email,
                leadingIcon = Icons.Default.Email,
                onValueChange = onEmailChange
            )
            Spacer(modifier = Modifier.height(12.dp))
            CustomerTextField(
                label = "Aadhaar Card",
                value = aadharCard,
                keyboardType = KeyboardType.Number,
                leadingIcon = Icons.Default.Badge,
                onValueChange = onAadharCardChange
            )
            Spacer(modifier = Modifier.height(12.dp))
            CustomerTextField(
                label = "Address *",
                value = address,
                leadingIcon = Icons.Default.Home,
                onValueChange = onAddressChange
            )
            Spacer(modifier = Modifier.height(12.dp))
            CustomerTextField(
                label = "PAN Card",
                value = pancard,
                leadingIcon = Icons.Default.CreditCard,
                onValueChange = onPanCardChange
            )
            Spacer(modifier = Modifier.height(12.dp))
            CustomerTextField(
                label = "Alternate Mobile",
                value = alternate,
                keyboardType = KeyboardType.Phone,
                leadingIcon = Icons.Default.PhoneAndroid,
                onValueChange = onAlternateChange
            )
            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    if (validateFields()) onNext() else showValidationError = true
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFff5722))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text("CONTINUE", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, "Continue", tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showValidationError) {
        AlertDialog(
            onDismissRequest = { showValidationError = false },
            title = { Text("Validation Error") },
            text = { Text(validationMessage) },
            confirmButton = {
                Button(
                    onClick = { showValidationError = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFff5722))
                ) { Text("OK") }
            }
        )
    }
}

// ─── Improved CustomerTextField with icon, divider, placeholder ───────────────

@Composable
fun CustomerTextField(
    label: String,
    value: String,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: ImageVector? = null,
    onValueChange: (String) -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF3949AB), Color(0xFF283593))
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Text(
                text = label,
                modifier = Modifier.width(110.dp),
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White.copy(alpha = 0.85f)
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(30.dp)
                    .background(Color.White.copy(alpha = 0.3f))
            )
            TextField(
                value = value,
                onValueChange = onValueChange,
                enabled = enabled,
                readOnly = readOnly,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                textStyle = TextStyle(
                    fontSize = 15.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                ),
                placeholder = {
                    Text(
                        text = "Enter ${label.trimEnd('*').trim().lowercase()}",
                        color = Color.White.copy(alpha = 0.4f),
                        fontSize = 13.sp
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    disabledTextColor = Color.White,
                    cursorColor = Color.White
                )
            )
        }
    }
}

// ─── Read Only Field ──────────────────────────────────────────────────────────

@Composable
fun CustomerReadOnlyField(modifier: Modifier = Modifier, label: String, value: String) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(60.dp).background(
                brush = Brush.linearGradient(colors = listOf(Color(0xFF4CAF50), Color(0xFF8BC34A)))
            ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 15.dp).width(150.dp),
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Text(
                modifier = Modifier.padding(end = 15.dp),
                text = value.ifEmpty { "Auto" },
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

// ─── Date Picker Field ────────────────────────────────────────────────────────

@Composable
fun CustomerDatePickerField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    val context = LocalContext.current
    Card(
        modifier = modifier.fillMaxWidth().clickable {
            showDatePicker(context, value) { selectedDate -> onValueChange(selectedDate) }
        },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(60.dp).background(
                brush = Brush.linearGradient(colors = listOf(AppColors.PrimaryLight, AppColors.PrimaryDark))
            ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 15.dp).width(120.dp),
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            Row(
                modifier = Modifier.weight(1f).padding(start = 10.dp, end = 15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = value.ifEmpty { "Select Date" },
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (value.isEmpty()) Color.White.copy(alpha = 0.6f) else Color.White
                )
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = "Select Date",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

fun showDatePicker(context: Context, currentDate: String, onDateSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    if (currentDate.isNotEmpty()) {
        try {
            val parts = currentDate.split("/")
            if (parts.size == 3) {
                calendar.set(parts[0].toInt(), parts[1].toInt() - 1, parts[2].toInt())
            }
        } catch (e: Exception) { }
    }
    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            val formattedDate = String.format("%04d/%02d/%02d", year, month + 1, dayOfMonth)
            onDateSelected(formattedDate)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

// ─── Text Field with Scanner ──────────────────────────────────────────────────

@Composable
fun CustomerTextFieldWithScanner(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    onScanClick: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(60.dp).background(
                brush = Brush.linearGradient(colors = listOf(AppColors.PrimaryLight, AppColors.PrimaryDark))
            ),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(start = 15.dp).width(100.dp),
                text = label,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.White
            )
            TextField(
                modifier = Modifier.weight(1f).padding(start = 10.dp),
                value = value,
                onValueChange = onValueChange,
                textStyle = TextStyle(fontSize = 15.sp, color = Color.White, fontWeight = FontWeight.Medium),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                colors = TextFieldDefaults.colors(
                    disabledTextColor = Color.White,
                    disabledContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            IconButton(onClick = onScanClick, modifier = Modifier.padding(end = 8.dp)) {
                Icon(Icons.Default.QrCodeScanner, "Scan Barcode", tint = Color.Black, modifier = Modifier.size(28.dp))
            }
        }
    }
}

// ─── Page 2: Product Details ──────────────────────────────────────────────────

@Composable
fun ProductDetailsPage(
    productName: String, onProductNameChange: (String) -> Unit,
    serialNumber: String, onSerialNumberChange: (String) -> Unit,
    imei1: String, onImei1Change: (String) -> Unit,
    imei2: String, onImei2Change: (String) -> Unit,
    emiDay: String, onEmiDayChange: (String) -> Unit,
    totalAmount: String, ontotalAmountChange: (String) -> Unit,
    downPayment: String, onDownPaymentChange: (String) -> Unit,
    loanAmount: String, onLoanAmountChange: (String) -> Unit,
    rateOfInterest: String, onRateOfInterestChange: (String) -> Unit,
    interestAmount: String, onInterestAmountChange: (String) -> Unit,
    totalAmountWithInterest: String, onTotalAmountWithInterestChange: (String) -> Unit,
    monthlyInstallment: String, onMonthlyInstallmentChange: (String) -> Unit,
    totalInstallment: String, onTotalInstallmentChange: (String) -> Unit,
    loanFrequency: String, onLoanFrequencyChange: (String) -> Unit,
    agreementDate: String, onAgreementDateChange: (String) -> Unit,
    firstInstallment: String, onFirstInstallmentChange: (String) -> Unit,
    billingInvoice: String, onBillingInvoiceChange: (String) -> Unit,
    emiDates: List<String>, onEmiDatesChange: (List<String>) -> Unit,
    onSubmit: () -> Unit
) {
    val context = LocalContext.current
    val options = GmsBarcodeScannerOptions.Builder().setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS).build()
    val scanner = GmsBarcodeScanning.getClient(context, options)

    var showValidationError by remember { mutableStateOf(false) }
    var validationMessage by remember { mutableStateOf("") }

    fun productvalidateFields(): Boolean {
        return when {
            productName.isBlank() -> { validationMessage = "Please enter product name"; false }
            imei1.isBlank() -> { validationMessage = "Please enter IMEI 1 must be 15 digits"; false }
            loanAmount.isBlank() -> { validationMessage = "Please enter loan amount"; false }
            downPayment.isBlank() -> { validationMessage = "Please enter down payment"; false }
            rateOfInterest.isBlank() -> { validationMessage = "Please enter rate of interest"; false }
            monthlyInstallment.isBlank() -> { validationMessage = "Please enter monthly installment"; false }
            totalInstallment.isBlank() -> { validationMessage = "Please enter total installment"; false }
            agreementDate.isBlank() -> { validationMessage = "Please enter agreement date"; false }
            totalAmount.isBlank() -> { validationMessage = "Please enter total amount"; false }
            emiDay.isBlank() -> { validationMessage = "Please enter EMI day"; false }
            firstInstallment.isBlank() -> { validationMessage = "Please enter loan start date"; false }
            else -> true
        }
    }

    fun scanBarcode(onResult: (String) -> Unit) {
        scanner.startScan()
            .addOnSuccessListener { barcode -> barcode.rawValue?.let { onResult(it) } }
            .addOnCanceledListener { }
            .addOnFailureListener { }
    }

    LaunchedEffect(totalAmount, downPayment, rateOfInterest, totalInstallment) {
        val calculation = calculateEMIDetails(totalAmount, downPayment, rateOfInterest, totalInstallment)
        if (calculation.loanAmount.isNotEmpty()) {
            onLoanAmountChange(calculation.loanAmount)
            onInterestAmountChange(calculation.interestAmount)
            onTotalAmountWithInterestChange(calculation.totalAmountWithInterest)
            onMonthlyInstallmentChange(calculation.monthlyInstallment)
        }
    }

    LaunchedEffect(firstInstallment, totalInstallment) {
        val totalCount = totalInstallment.toIntOrNull() ?: 0
        if (firstInstallment.isNotEmpty() && totalCount > 0) {
            val generatedDates = generateEmiDates(firstInstallment, totalCount)
            if (generatedDates.isNotEmpty() && generatedDates != emiDates) {
                onEmiDatesChange(generatedDates)
            }
        } else if (totalCount == 0) {
            onEmiDatesChange(emptyList())
        }
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Spacer(modifier = Modifier.height(20.dp))
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp)) {

            CustomerTextField(
                label = "Product Name",
                value = productName,
                leadingIcon = Icons.Default.Inventory,
                onValueChange = onProductNameChange
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomerTextField(
                label = "Serial No.",
                value = serialNumber,
                leadingIcon = Icons.Default.Numbers,
                onValueChange = onSerialNumberChange
            )
            Spacer(modifier = Modifier.height(15.dp))

            CustomerTextFieldWithScanner(
                modifier = Modifier, label = "IMEI 1", value = imei1,
                onValueChange = onImei1Change,
                onScanClick = { scanBarcode { onImei1Change(it) } }
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomerTextFieldWithScanner(
                modifier = Modifier, label = "IMEI 2", value = imei2,
                onValueChange = onImei2Change,
                onScanClick = { scanBarcode { onImei2Change(it) } }
            )
            Spacer(modifier = Modifier.height(25.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFff5722)),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Calculate, null, tint = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("EMI Calculator", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(15.dp))

            CustomerTextField(
                label = "Product Price",
                value = totalAmount,
                keyboardType = KeyboardType.Decimal,
                leadingIcon = Icons.Default.CurrencyRupee,
                onValueChange = ontotalAmountChange
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomerTextField(
                label = "Down Payment",
                value = downPayment,
                keyboardType = KeyboardType.Decimal,
                leadingIcon = Icons.Default.Payments,
                onValueChange = onDownPaymentChange
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomerTextField(
                label = "Interest %",
                value = rateOfInterest,
                keyboardType = KeyboardType.Decimal,
                leadingIcon = Icons.Default.Percent,
                onValueChange = onRateOfInterestChange
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomerTextField(
                label = "Installments",
                value = totalInstallment,
                keyboardType = KeyboardType.Number,
                leadingIcon = Icons.Default.DateRange,
                onValueChange = onTotalInstallmentChange
            )
            Spacer(modifier = Modifier.height(20.dp))

            CustomerReadOnlyField(modifier = Modifier, "Loan Amount", "₹$loanAmount")
            Spacer(modifier = Modifier.height(15.dp))
            CustomerReadOnlyField(modifier = Modifier, "Interest Amount", "₹$interestAmount")
            Spacer(modifier = Modifier.height(15.dp))
            CustomerReadOnlyField(modifier = Modifier, "Total with Interest", "₹$totalAmountWithInterest")
            Spacer(modifier = Modifier.height(15.dp))
            CustomerReadOnlyField(modifier = Modifier, "Monthly Installment", "₹$monthlyInstallment")
            Spacer(modifier = Modifier.height(20.dp))

            CustomerDatePickerField(
                label = "EMI Day",
                value = emiDay,
                onValueChange = { selectedDate ->
                    val day = selectedDate.split("/").getOrNull(2)?.padStart(2, '0') ?: ""
                    onEmiDayChange(day)
                }
            )
            Spacer(modifier = Modifier.height(20.dp))
            CustomerDatePickerField(modifier = Modifier, label = "Agreement Date", value = agreementDate, onValueChange = onAgreementDateChange)
            Spacer(modifier = Modifier.height(15.dp))
            CustomerDatePickerField(
                label = "1st EMI Date",
                value = firstInstallment,
                onValueChange = { date -> onFirstInstallmentChange(date.replace("/", "-")) }
            )
            Spacer(modifier = Modifier.height(15.dp))
            CustomerTextField(
                label = "Billing Invoice",
                value = billingInvoice,
                leadingIcon = Icons.Default.Receipt,
                onValueChange = onBillingInvoiceChange
            )
            Spacer(modifier = Modifier.height(15.dp))

            if (emiDates.isNotEmpty()) {
                Text(
                    text = "EMI Schedule (${emiDates.size} Installments)",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 10.dp)
                )
                emiDates.forEachIndexed { index, date ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(3.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().height(65.dp).background(
                                brush = Brush.linearGradient(colors = listOf(Color(0xFF6A1B9A), Color(0xFF8E24AA)))
                            ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Card(
                                modifier = Modifier.padding(start = 12.dp).size(45.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
                            ) {
                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("${index + 1}", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                            }
                            Text(
                                modifier = Modifier.padding(start = 12.dp).weight(1f),
                                text = "EMI ${index + 1}",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Row(modifier = Modifier.padding(end = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = date, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White, textAlign = TextAlign.End)
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(Icons.Default.CheckCircle, "Auto Generated", tint = Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
            Button(
               // onClick = { if (productvalidateFields()) onSubmit() else showValidationError = true },
                onClick = { onSubmit() },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFff5722))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Text("CONTINUE TO DOCUMENTS", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.Default.ArrowForward, "Continue", tint = Color.White)
                }
            }

            if (showValidationError) {
                AlertDialog(
                    onDismissRequest = { showValidationError = false },
                    title = { Text("Validation Error") },
                    text = { Text(validationMessage) },
                    confirmButton = { TextButton(onClick = { showValidationError = false }) { Text("OK") } }
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}


fun compressImageUri(context: Context, uri: Uri, maxSizeKb: Int = 1024): Uri {
    val inputStream = context.contentResolver.openInputStream(uri)
    val originalBitmap = BitmapFactory.decodeStream(inputStream)

    var quality = 90
    var outputBytes: ByteArray

    do {
        val outputStream = ByteArrayOutputStream()
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        outputBytes = outputStream.toByteArray()
        quality -= 10
    } while (outputBytes.size > maxSizeKb * 1024 && quality > 10)

    val compressedFile = File(context.cacheDir, "compressed_${System.currentTimeMillis()}.jpg")
    compressedFile.writeBytes(outputBytes)
    return Uri.fromFile(compressedFile)
}
// ─── Page 3: Upload Documents ─────────────────────────────────────────────────

@Composable
fun UploadDocumentPage(
    name: String, mobile: String, email: String, aadharCard: String, address: String,
    pancard: String, alternate: String, productName: String, imei1: String, imei2: String,
    totalAmount: String, emiDay: String,
    loanAmount: String, loanFrequency: String, rateOfInterest: String, agreementDate: String,
    firstInstallment: String, downPayment: String, billingInvoice: String, totalInstallment: String,
    emiDates: List<String>, monthlyInstallment: String,
    onBack: () -> Unit, navController: NavController,
    customerviewModel: CustomerViewModel = viewModel(),
    viewModel: DocumentViewModel = viewModel(),
    onCustomerAdded: () -> Unit = {},
) {
    val context = LocalContext.current
    var showErrorDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    var showSizeWarningDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { viewModel.initializeUris(context) }

    val compressedPhoto = viewModel.photo?.let { compressImageUri(context, it) }
    val compressedAadhaarFront = viewModel.aadhaarFront?.let { compressImageUri(context, it) }
    val compressedAadhaarBack = viewModel.aadhaarBack?.let { compressImageUri(context, it) }
    val compressedPan = viewModel.panCard?.let { compressImageUri(context, it) }
    // Camera launchers
    val cameraPhoto = rememberCameraLauncher(viewModel.photoUri) { viewModel.photo = it }
    val cameraAadhaarFront = rememberCameraLauncher(viewModel.aadhaarFrontUri) { viewModel.aadhaarFront = it }
    val cameraAadhaarBack = rememberCameraLauncher(viewModel.aadhaarBackUri) { viewModel.aadhaarBack = it }
    val cameraPan = rememberCameraLauncher(viewModel.panCardUri) { viewModel.panCard = it }

    // Gallery launchers
    val galleryPhoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { viewModel.photo = it }
    val galleryAadhaarFront = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { viewModel.aadhaarFront = it }
    val galleryAadhaarBack = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { viewModel.aadhaarBack = it }
    val galleryPan = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { viewModel.panCard = it }

    fun submitCustomer() {
        customerviewModel.addCustomerToApi(
            photoUri = compressedPhoto,
            aadhaarFrontUri = compressedAadhaarFront,
            aadhaarBackUri = compressedAadhaarBack,
            panCardUri = compressedPan,
            name = name, mobile = mobile, email = email, address = address,
            aadharCard = aadharCard, pancard = pancard, alternate = alternate,
            imei1 = imei1, imei2 = imei2, productName = productName, serialNumber = "",
            totalAmount = totalAmount, loanstartdate = firstInstallment,
            loanAmount = loanAmount, downPayment = downPayment,
            monthlyInstallment = monthlyInstallment, totalInstallment = totalInstallment,
            rateOfInterest = rateOfInterest, agreementDate = agreementDate,
            billingInvoice = billingInvoice, emiday = emiDay, retailerId = 1,
            signatureFilePath = viewModel.signaturePath,
            onSuccess = { responseDeviceId ->
                Log.d("AddCustomer", "API Success - Device ID: $responseDeviceId")
                onCustomerAdded()
                val emiDatesString = emiDates.joinToString(",")
                val paymentStatusString = List(emiDates.size) { "false" }.joinToString(",")
                try {
                    val customer = addCustomerList(
                        id = 0, name, mobile, email, aadharCard, address, pancard,
                        alternate, productName, imei1, imei2, loanAmount, loanFrequency,
                        rateOfInterest, agreementDate, firstInstallment, downPayment,
                        billingInvoice, totalInstallment, emiDatesString, paymentStatusString,
                        viewModel.photo?.toString(), viewModel.aadhaarFront?.toString(),
                        viewModel.aadhaarBack?.toString(), viewModel.panCard?.toString(),
                        viewModel.signaturePath
                    )
                    customerviewModel.addCustomer(customer)
                    Log.d("AddCustomer", "Room DB save completed")
                    navController.navigate("qr_screen/$responseDeviceId") {
                        popUpTo(Routes.AddCustomer.route) { inclusive = true }
                    }
                } catch (e: Exception) {
                    Log.e("AddCustomer", "Error saving to Room: ${e.message}", e)
                    errorMessage = "Saved to server but local save failed"
                    showErrorDialog = true
                }
            },
            onError = { error ->
                errorMessage = when {
                    error.contains("413") -> "Files are too large to upload. Please use smaller images."
                    error.contains("timeout", ignoreCase = true) -> "Connection timed out. Please try again."
                    error.contains("500") -> "Server error. Please try again later."
                    else -> error
                }
                showErrorDialog = true
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Image(
            painterResource(R.drawable.uploaddocbanner), null,
            modifier = Modifier.fillMaxWidth().height(180.dp).padding(15.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(Modifier.height(15.dp))
        Column(Modifier.padding(horizontal = 20.dp)) {
            Text(
                "Upload Required Documents",
                fontSize = 20.sp, fontWeight = FontWeight.Bold,
                color = Color.Black, modifier = Modifier.padding(bottom = 16.dp)
            )

            DocumentUploadCard(
                label = "Customer Photo",
                uri = viewModel.photo,
                onCamera = { viewModel.photoUri?.let { cameraPhoto.launch(it) } },
                onGallery = { galleryPhoto.launch("image/*") }
            )
            Spacer(Modifier.height(15.dp))
            DocumentUploadCard(
                label = "Aadhaar Card Front",
                uri = viewModel.aadhaarFront,
                onCamera = { viewModel.aadhaarFrontUri?.let { cameraAadhaarFront.launch(it) } },
                onGallery = { galleryAadhaarFront.launch("image/*") }
            )
            Spacer(Modifier.height(15.dp))
            DocumentUploadCard(
                label = "Aadhaar Card Back",
                uri = viewModel.aadhaarBack,
                onCamera = { viewModel.aadhaarBackUri?.let { cameraAadhaarBack.launch(it) } },
                onGallery = { galleryAadhaarBack.launch("image/*") }
            )
            Spacer(Modifier.height(15.dp))
            DocumentUploadCard(
                label = "PAN Card",
                uri = viewModel.panCard,
                onCamera = { viewModel.panCardUri?.let { cameraPan.launch(it) } },
                onGallery = { galleryPan.launch("image/*") }
            )
            Spacer(Modifier.height(20.dp))

            Text("Customer Signature", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Spacer(Modifier.height(10.dp))
            SignatureBox(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                context = context,
                onDraw = { path -> viewModel.updateSignature(path, context) }
            )
            Spacer(Modifier.height(25.dp))

            if (customerviewModel.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    color = Color(0xFFff5722)
                )
                Spacer(Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    val urisToCheck = listOf(
                        viewModel.photo, viewModel.aadhaarFront,
                        viewModel.aadhaarBack, viewModel.panCard
                    )
                    val oversized = urisToCheck.filterNotNull().any { uri ->
                        context.contentResolver.openFileDescriptor(uri, "r")
                            ?.use { it.statSize > 2 * 1024 * 1024 } == true
                    }
                    if (oversized) {
                        showSizeWarningDialog = true
                    } else {
                        submitCustomer()
                    }
                },
                modifier = Modifier.fillMaxWidth().height(55.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(Color(0xFFff5722)),
                enabled = !customerviewModel.isLoading
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Icon(Icons.Default.CheckCircle, "Submit", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        if (customerviewModel.isLoading) "SUBMITTING..." else "SUBMIT & COMPLETE",
                        color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(Modifier.height(30.dp))
        }
    }

    // Error dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = { Text(errorMessage) },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFff5722))
                ) { Text("OK") }
            }
        )
    }

    // File size warning dialog
//    if (showSizeWarningDialog) {
//        AlertDialog(
//            onDismissRequest = { showSizeWarningDialog = false },
//            title = { Text("File Size Warning") },
//            text = { Text("One or more files exceed 2MB. Large files may cause upload issues. Do you want to submit anyway?") },
//            confirmButton = {
//                Button(
//                    onClick = {
//                        showSizeWarningDialog = false
//                        submitCustomer()
//                    },
//                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFff5722))
//                ) { Text("Submit Anyway") }
//            },
//            dismissButton = {
//                OutlinedButton(onClick = { showSizeWarningDialog = false }) {
//                    Text("Go Back")
//                }
//            }
//        )
//    }
}

@Composable
fun rememberGalleryLauncher(onResult: (Uri?) -> Unit) =
    rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { onResult(it) }


// ─── Camera Launcher ──────────────────────────────────────────────────────────

@Composable
fun rememberCameraLauncher(
    targetUri: Uri?,
    onCaptured: (Uri?) -> Unit
): ManagedActivityResultLauncher<Uri, Boolean> {
    return rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) onCaptured(targetUri)
    }
}

// ─── Signature Box ────────────────────────────────────────────────────────────

@Composable
fun SignatureBox(modifier: Modifier = Modifier, context: Context, onDraw: (Path) -> Unit) {
    val pathPoints = remember { mutableStateListOf<Offset>() }
    val currentPath = remember { Path() }

    Column {
        Box(
            modifier = modifier
                .background(Color.White, RoundedCornerShape(12.dp))
                .border(2.dp, Color(0xFFff5722).copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { start ->
                            pathPoints.add(start)
                            currentPath.moveTo(start.x, start.y)
                        },
                        onDrag = { change, _ ->
                            val boxSize = this.size
                            val x = change.position.x.coerceIn(0f, boxSize.width.toFloat())
                            val y = change.position.y.coerceIn(0f, boxSize.height.toFloat())
                            val clippedPoint = Offset(x, y)
                            pathPoints.add(clippedPoint)
                            currentPath.lineTo(clippedPoint.x, clippedPoint.y)
                            onDraw(currentPath)
                            change.consume()
                        }
                    )
                }
        ) {
            Canvas(Modifier.fillMaxSize()) {
                val drawPath = Path()
                if (pathPoints.isNotEmpty()) {
                    drawPath.moveTo(pathPoints[0].x, pathPoints[0].y)
                    for (i in 1 until pathPoints.size) drawPath.lineTo(pathPoints[i].x, pathPoints[i].y)
                }
                drawPath(path = drawPath, color = Color.Black, style = Stroke(width = 5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            if (pathPoints.isEmpty()) {
                Column(modifier = Modifier.align(Alignment.Center), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Edit, null, tint = Color.Gray.copy(alpha = 0.3f), modifier = Modifier.size(48.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Sign here with your finger", color = Color.Gray.copy(alpha = 0.5f), fontSize = 14.sp)
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = { pathPoints.clear(); currentPath.reset(); onDraw(currentPath) }) {
                Icon(Icons.Default.Refresh, "Clear", tint = Color(0xFFff5722))
                Spacer(Modifier.width(4.dp))
                Text("Clear Signature", color = Color(0xFFff5722))
            }
        }
    }
}

// ─── Document Upload Card ─────────────────────────────────────────────────────

@Composable
fun DocumentUploadCard(label: String, uri: Uri?, onCamera: () -> Unit, onGallery: () -> Unit) {
    var showOptions by remember { mutableStateOf(false) }

    Column {
        Button(
            onClick = { showOptions = true },
            modifier = Modifier.fillMaxWidth().height(70.dp),
            shape = RoundedCornerShape(13.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().height(48.dp)
                    .background(
                        brush = Brush.linearGradient(colors = listOf(AppColors.PrimaryLight, AppColors.PrimaryDark)),
                        shape = RoundedCornerShape(13.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(label, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Icon(Icons.Default.CameraAlt, "Capture", tint = Color.White)
            }
        }

        // Two option dialog on click
        if (showOptions) {
            AlertDialog(
                onDismissRequest = { showOptions = false },
                title = { Text("Upload $label") },
                text = {
                    Column {
                        // Camera option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showOptions = false
                                    onCamera()
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CameraAlt, "Camera",
                                tint = Color(0xFFff5722),
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(Modifier.width(16.dp))
                            Text("Take Photo", fontSize = 16.sp)
                        }

                        Divider()

                        // Gallery option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showOptions = false
                                    onGallery()
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Photo, "Gallery",
                                tint = Color(0xFFff5722),
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(Modifier.width(16.dp))
                            Text("Choose from Gallery", fontSize = 16.sp)
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showOptions = false }) {
                        Text("Cancel", color = Color(0xFFff5722))
                    }
                }
            )
        }

        uri?.let {
            Spacer(Modifier.height(10.dp))
            Card(
                modifier = Modifier.fillMaxWidth().height(200.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Box {
                    Image(
                        rememberAsyncImagePainter(it), "Captured $label",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Icon(
                        Icons.Default.CheckCircle, "Uploaded",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.align(Alignment.TopEnd).padding(8.dp).size(32.dp)
                    )
                }
            }
        }
    }
}