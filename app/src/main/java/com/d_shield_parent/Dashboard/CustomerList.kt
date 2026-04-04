package com.d_shield_parent.Dashboard

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import com.d_shield_parent.R
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
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
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshList()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

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
//                IconButton(onClick = { navController.popBackStack() }) {
//                    Icon(
//                        imageVector = Icons.Default.ArrowBackIos,
//                        tint = Color.Black,
//                        contentDescription = "Back"
//                    )
//                }
//                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    "Customer List",
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    color = Color.Black,
                    fontWeight = FontWeight.W800,
                    fontSize = 21.sp,
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
                Log.d("ListScreen", "Devices count: ${devices.size}")

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
                            CustomerItem(device = device, navController = navController)
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

fun formatDateForDisplay(date: String?): String {
    if (date.isNullOrEmpty()) return "N/A"
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val parsedDate = inputFormat.parse(date)
        outputFormat.format(parsedDate!!)
    } catch (e: Exception) {
        date // ← now safe since we checked null above
    }
}

@Composable
fun CustomerItem(
    device: DeviceModel,
    navController: NavController
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .clickable {
                    navController.navigate(Routes.CustomerDetail.createRoute(device.id))
                },
            elevation = CardDefaults.elevatedCardElevation(4.dp),
            colors = CardDefaults.cardColors(Color.White)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left - Avatar + Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f) // ✅ prevents arrow from being pushed off
                ) {
                    // Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF1E1E2E), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = device.customerName?.first()?.uppercaseChar()?.toString() ?: "?",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = device.customerName ?: "N/A",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = device.customerPhone ?: "N/A",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )

                        // Product Name
                        device.productName?.let { product ->
                            if (product.isNotBlank()) {
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.PhoneAndroid,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = product,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                        }

                        // Address
                        device.customerAddress?.let { addr ->
                            if (addr.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.LocationOn,
                                        contentDescription = null,
                                        tint = Color.Gray,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = addr,
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }

                        // Signature
                        device.signature?.let { sig ->
                            if (sig.isNotBlank()) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Edit,
                                        contentDescription = null,
                                        tint = Color(0xFF4CAF50),
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text(
                                        text = "Signature collected",
                                        fontSize = 12.sp,
                                        color = Color(0xFF4CAF50)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        // Status Badge
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (device.status?.equals("active", ignoreCase = true) == true)
                                        Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = device.status?.replaceFirstChar { it.uppercase() } ?: "N/A",
                                fontSize = 11.sp,
                                color = if (device.status?.equals("active", ignoreCase = true) == true)
                                    Color(0xFF4CAF50) else Color(0xFFD32F2F),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    } // end Column
                } // end inner Row

                // Right - Arrow
                Icon(
                    imageVector = Icons.Default.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color.LightGray,
                    modifier = Modifier.size(24.dp)
                )
            } // end outer Row
        } // end Card
    } // end Box
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerDetailScreen(
    deviceId: Int,
    navController: NavController,
    viewModel: CustomerListViewModel = viewModel()
) {
    val customerListState by viewModel.customerListState.collectAsState()

    val device = remember(customerListState) {
        if (customerListState is CustomerListState.Success) {
            (customerListState as CustomerListState.Success).data.devices.find { it.id == deviceId }
        } else null
    }

    var emiExpanded by remember { mutableStateOf(false) }
    val emiScheduleState by viewModel.getEMIScheduleState(deviceId).collectAsState()

    LaunchedEffect(emiExpanded) {
        if (emiExpanded) viewModel.fetchEMISchedule(deviceId)
    }

    if (device == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val isUnlocked = device.status?.equals("active", ignoreCase = true) == true ||
            device.status?.equals("unlocked", ignoreCase = true) == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        device.customerName ?: "Customer Detail",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBackIos,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {
                        viewModel.removeDevice(imei1 = device.imei1 ?: "")
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1E1E2E),
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
        ) {
            // ── Profile Card ──
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(Color(0xFF1E1E2E)),
                    elevation = CardDefaults.elevatedCardElevation(6.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(16.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = device.customerName?.first()?.uppercaseChar()?.toString() ?: "?",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = device.customerName ?: "N/A",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = device.customerPhone ?: "N/A",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Box(
                            modifier = Modifier
                                .background(
                                    color = if (isUnlocked) Color(0xFF4CAF50).copy(alpha = 0.2f)
                                    else Color(0xFFD32F2F).copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = device.status?.replaceFirstChar { it.uppercase() } ?: "N/A",
                                color = if (isUnlocked) Color(0xFF4CAF50) else Color(0xFFD32F2F),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            // ── Device Info ──
            item {
                SectionCard(title = "Device Info") {
                    InfoRow("Product", device.productName ?: "N/A")
                    InfoRow("IMEI 1", device.imei1 ?: "N/A")
                    InfoRow("IMEI 2", device.imei2 ?: "N/A")
                    InfoRow("Serial No.", device.serialNumber ?: "N/A")
                    InfoRow("MDM Status", device.mdmStatus ?: "N/A")
                }
            }

            // ── Loan Info ──
            item {
                SectionCard(title = "Loan Details") {
                    InfoRow("Loan Amount", "₹${device.loanAmount ?: "N/A"}")
                    InfoRow("Down Payment", "₹${device.downPayment ?: "N/A"}")
                    InfoRow("Monthly EMI", "₹${device.monthlyInstallment ?: "N/A"}")
                    InfoRow("Total Installments", device.totalInstallments?.toString() ?: "N/A")
                    InfoRow("Loan Start Date", formatDateForDisplay(device.loanStartDate))
                    InfoRow("Agreement Date", formatDateForDisplay(
                        device.agreementDate?.substringBefore("T")
                    ))
                }
            }

            // ── EMI Schedule ──
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(Color.White),
                    elevation = CardDefaults.elevatedCardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                if (emiScheduleState is EMIScheduleState.Success) {
                                    emiExpanded = !emiExpanded
                                } else {
                                    viewModel.fetchEMISchedule(deviceId)
                                }
                            },
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = Color(0xFF2196F3)
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
                                            val paidCount = state.data.data?.paid_installments ?: 0
                                            val totalCount = state.data.data?.total_installments?.toIntOrNull() ?: 0
                                            Text(
                                                "$paidCount / $totalCount Paid",
                                                fontSize = 13.sp,
                                                color = if (paidCount == totalCount) Color(0xFF4CAF50) else Color.Gray
                                            )
                                        }
                                        else -> Text("Tap to load", fontSize = 13.sp, color = Color.Gray)
                                    }
                                }
                            }
                            Icon(
                                imageVector = if (emiExpanded) Icons.Default.KeyboardArrowUp
                                else Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color.Gray
                            )
                        }

                        if (emiExpanded) {
                            Spacer(modifier = Modifier.height(12.dp))
                            when (val state = emiScheduleState) {
                                is EMIScheduleState.Loading -> {
                                    Box(
                                        modifier = Modifier.fillMaxWidth().padding(20.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(color = Color(0xFF2196F3))
                                    }
                                }
                                is EMIScheduleState.Success -> {

                                    val installments = state.data.data.installments

                                    if (installments.isNullOrEmpty()) {
                                        Text(
                                            text = "No EMI data available",
                                            color = Color.Gray,
                                            modifier = Modifier.padding(12.dp)
                                        )
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
                                    TextButton(onClick = { viewModel.fetchEMISchedule(deviceId) }) {
                                        Text("Retry", color = Color(0xFF2196F3))
                                    }
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }

            // ── Action Buttons ──
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E1E2E)),
                        onClick = { navController.navigate(Routes.QrScanner.createRoute(deviceId)) }
                    ) {
                        Text("Show QR", fontSize = 14.sp, color = Color.White)
                    }

                    Button(
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isUnlocked) Color(0xFFB90D0D) else Color(0xFF4CAF50)
                        ),
                        onClick = {
                            viewModel.updateDeviceStatus(
                                imei1 = device.imei1 ?: "",
                                currentStatus = device.status ?: ""
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
fun SectionCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.elevatedCardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E1E2E)
            )
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0), thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}