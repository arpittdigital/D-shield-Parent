package com.d_shield_parent.Dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d_shield_parent.Dashboard.model.DeviceModel
import com.d_shield_parent.Dashboard.model.InstallmentItem
import com.d_shield_parent.Dashboard.viewModel.CustomerListState
import com.d_shield_parent.Dashboard.viewModel.CustomerListViewModel
import com.d_shield_parent.Dashboard.viewModel.EMIScheduleState
import com.d_shield_parent.navigation.Routes
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ListScreen(
    navController: NavController,
    viewModel: CustomerListViewModel = viewModel()
) {
    val customerListState by viewModel.customerListState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.PrimaryDark),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.height(15.dp))

        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIos,
                        tint = Color.Black,
                        contentDescription = "Back",
                        modifier = Modifier.clickable { navController.popBackStack() }
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Customer List",
                    color = Color.Black,
                    fontWeight = FontWeight.W800,
                    fontSize = 21.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            IconButton(onClick = { viewModel.refreshList() }) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh",
                    tint = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        when (val state = customerListState) {
            is CustomerListState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Ready to load", color = Color.Gray)
                }
            }

            is CustomerListState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color.Black)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading customers...", color = Color.Black)
                    }
                }
            }

            is CustomerListState.Success -> {
                val devices = state.data.devices

                if (devices.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "No customers found",
                                fontSize = 18.sp,
                                color = Color.Gray
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "Add your first customer!",
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(devices) { device ->
                            CustomerItem(device = device, viewModel = viewModel, navController = navController)
                        }
                    }
                }
            }

            is CustomerListState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "Error loading customers",
                            fontSize = 18.sp,
                            color = Color.Red,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            state.message,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.refreshList() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Black
                            )
                        ) {
                            Text("Retry")
                        }
                    }
                }
            }
        }
    }
}

fun formatDateForDisplay(date: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val parsedDate = inputFormat.parse(date)
        outputFormat.format(parsedDate!!)
    } catch (e: Exception) {
        date
    }
}

@Composable
fun CustomerItem(
    device: DeviceModel,
    navController: NavController,
    viewModel: CustomerListViewModel = viewModel()
) {
    var isExpanded by remember { mutableStateOf(false) }

    // Get device ID (convert to Int)
    val deviceId = remember(device) {
        device.id.toInt() ?: 0
    }

    // Fetch EMI schedule when expanded
    LaunchedEffect(isExpanded, deviceId) {
        if (isExpanded && deviceId > 0) {
            viewModel.fetchEMISchedule(deviceId)
        }
    }

    // Observe EMI schedule state
    val emiScheduleState by viewModel.getEMIScheduleState(deviceId).collectAsState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(25.dp)),
            elevation = CardDefaults.elevatedCardElevation(8.dp),
            colors = CardDefaults.cardColors(Color.LightGray)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Image(
//                            modifier = Modifier.size(50.dp),
//                            painter = painterResource(id = R.drawable.placeholder),
//                            contentDescription = null
//                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = device.customer_name,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "ID: ${device.id}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Gray
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
//                        Image(
//                            modifier = Modifier.size(40.dp),
//                            painter = painterResource(R.drawable.mobileimg),
//                            contentDescription = null
//                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Icon(
                            modifier = Modifier
                                .size(28.dp)
                                .clickable { viewModel.removeDevice(imei1 = device.imei1 ?: "") },
                            imageVector = Icons.Default.Delete,
                            tint = Color.Red,
                            contentDescription = "Delete"
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Info Rows
                InfoRow("Mobile", device.phone_number ?: "N/A")
                Spacer(modifier = Modifier.height(12.dp))

                InfoRow("IMEI 1", device.imei1 ?: "N/A")
                Spacer(modifier = Modifier.height(12.dp))

                InfoRow("IMEI 2", device.imei2 ?: "N/A")
                Spacer(modifier = Modifier.height(12.dp))

                InfoRow("Product", device.product_name ?: "N/A")
                Spacer(modifier = Modifier.height(12.dp))

                InfoRow("Loan Amount", "₹${device.loan_amount}")
                Spacer(modifier = Modifier.height(12.dp))

                InfoRow("Monthly EMI", "₹${device.monthly_installment}")
                Spacer(modifier = Modifier.height(12.dp))

                InfoRow("Total Installments", device.total_installments)
                Spacer(modifier = Modifier.height(12.dp))

                InfoRow("Loan Start Date", formatDateForDisplay(device.loan_start_date))
                Spacer(modifier = Modifier.height(12.dp))

                InfoRow("Status", device.status)

                // EMI Payment Tracking Section
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isExpanded = !isExpanded },
                    colors = CardDefaults.cardColors(Color(0xFFF5F5F5)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarMonth,
                                contentDescription = null,
                                tint = Color(0xFF2196F3),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    "EMI Schedule",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )

                                when (val state = emiScheduleState) {
                                    is EMIScheduleState.Success -> {
                                        val paidCount = state.data.data.paid_installments
                                        val totalCount = state.data.data.total_installments.toIntOrNull() ?: 0
                                        Text(
                                            "$paidCount / $totalCount Paid",
                                            fontSize = 13.sp,
                                            color = if (paidCount == totalCount) Color(0xFF4CAF50) else Color.Gray
                                        )
                                    }
                                    else -> {
                                        Text(
                                            "Tap to load",
                                            fontSize = 13.sp,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }
                        }
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp
                            else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = Color.Gray
                        )
                    }
                }

                // Expandable EMI List
                if (isExpanded) {
                    Spacer(modifier = Modifier.height(12.dp))

                    when (val state = emiScheduleState) {
                        is EMIScheduleState.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(20.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = Color(0xFF2196F3),
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        is EMIScheduleState.Success -> {
                            val installments = state.data.data.installments

                            if (installments.isEmpty()) {
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp),
                                    colors = CardDefaults.cardColors(Color(0xFFFFF3E0))
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(20.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            "No installments found",
                                            color = Color.Gray,
                                            fontSize = 14.sp
                                        )
                                    }
                                }
                            } else {
                                installments.forEach { installment ->
                                    EMIInstallmentCard(
                                        installment = installment,
                                        deviceId = deviceId,
                                        viewModel = viewModel
                                    )
                                }
                            }
                        }

                        is EMIScheduleState.Error -> {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                colors = CardDefaults.cardColors(Color(0xFFFFEBEE))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        "Failed to load EMI schedule",
                                        color = Color(0xFFD32F2F),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    TextButton(
                                        onClick = { viewModel.fetchEMISchedule(deviceId) }
                                    ) {
                                        Text("Retry", color = Color(0xFF2196F3))
                                    }
                                }
                            }
                        }

                        is EMIScheduleState.Idle -> {
                            // Initial state - will load when expanded
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        modifier = Modifier
                            .height(40.dp)
                            .width(120.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black
                        ),
                        onClick = {  navController.navigate(
                            Routes.QrScanner.createRoute(deviceId)
                        ) }
                    ) {
                        Text("Show QR", fontSize = 14.sp, color = Color.White)
                    }

                    Spacer(modifier = Modifier.width(15.dp))

                    val isUnlocked = device.status.equals("active", ignoreCase = true) ||
                            device.status.equals("unlocked", ignoreCase = true)

                    Button(
                        modifier = Modifier
                            .height(40.dp)
                            .width(120.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isUnlocked)
                                Color(0xFFB90D0D)
                            else
                                Color(0xFF4CAF50)
                        ),
                        onClick = {
                            viewModel.updateDeviceStatus(
                                imei1 = device.imei1 ?: "",
                                currentStatus = device.status
                            )
                        }
                    ) {
                        Text(
                            text = if (isUnlocked) "Lock" else "Unlock",
                            fontSize = 14.sp,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EMIInstallmentCard(
    installment: InstallmentItem,
    deviceId: Int,
    viewModel: CustomerListViewModel
) {
    val isPaid = installment.status.equals("paid", ignoreCase = true)
    val isOverdue = installment.status.equals("overdue", ignoreCase = true)
    val displayDate = formatDateForDisplay(installment.due_date)

    val backgroundColor = when {
        isPaid -> Color(0xFFE8F5E9)
        isOverdue -> Color(0xFFFFEBEE)
        else -> Color(0xFFFFF3E0)
    }

    val badgeColor = when {
        isPaid -> Color(0xFF4CAF50)
        isOverdue -> Color(0xFFD32F2F)
        else -> Color(0xFFFF9800)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(backgroundColor),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // EMI Number Badge
                Card(
                    modifier = Modifier.size(40.dp),
                    colors = CardDefaults.cardColors(badgeColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            installment.installment_number,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        "EMI ${installment.installment_number}",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )
                    Text(
                        displayDate,
                        fontSize = 13.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "₹${installment.amount}",
                        fontSize = 12.sp,
                        color = Color(0xFF2196F3),
                        fontWeight = FontWeight.Bold
                    )
                    if (isPaid && !installment.paid_date.isNullOrEmpty()) {
                        Text(
                            "Paid: ${formatDateForDisplay(installment.paid_date)}",
                            fontSize = 11.sp,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Payment Status Toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    installment.status.capitalize(Locale.getDefault()),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = badgeColor
                )
                Spacer(modifier = Modifier.width(8.dp))
                Switch(
                    checked = isPaid,
                    onCheckedChange = { newStatus ->
                        if (newStatus && !isPaid) {
                            // Only allow marking as paid
                            viewModel.markInstallmentAsPaid(
                                deviceId = deviceId,
                                installmentId = installment.id
                            )
                        }
                    },
                    enabled = !isPaid, // Disable if already paid
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4CAF50),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = badgeColor,
                        disabledCheckedThumbColor = Color.White,
                        disabledCheckedTrackColor = Color(0xFF4CAF50)
                    )
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black
        )
        Text(
            text = value,
            fontSize = 15.sp,
            color = Color.DarkGray
        )
    }
}