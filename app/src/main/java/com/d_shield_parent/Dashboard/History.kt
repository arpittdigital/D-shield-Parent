
package com.d_shield_parent.Dashboard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d_shield_parent.Dashboard.viewModel.HistoryListState
import com.d_shield_parent.Dashboard.viewModel.HistoryViewModel


data class History(
    val name: String,
    val number: String,
    val date: String,
    val imei1: String,
    val action: String // "Unlocked & Deleted"
)
object AppColors {
    val PrimaryDark = Color(0xFFFFFFFF)
    val PrimaryLight = Color(0xFF3949AB)
    val Accent = Color(0xFF1976d2)
    val CardBackground = Color(0xFFF5F7FA)
    val ButtonColor = Color(0xFFff5722)
    val TextPrimary = Color(0xFF212121)
    val TextSecondary = Color(0xFF757575)
    val Success = Color(0xFF4CAF50)
    val Warning = Color(0xFFFF9800)
    val Danger = Color(0xFFF44336)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: HistoryViewModel = viewModel()
) {

    val state by viewModel.historyListState.collectAsState()

    // API call on screen load
    LaunchedEffect(Unit) {
        viewModel.fetchHistoryList()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Unlock History",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.PrimaryLight
                )
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            when (state) {

                is HistoryListState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is HistoryListState.Error -> {
                    Text(
                        text = (state as HistoryListState.Error).message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is HistoryListState.Success -> {

                    val historyList =
                        (state as HistoryListState.Success).data.devices.map {
                            History(
                                name = it.customer_name,
                                number = it.customer_phone,
                                date = it.updated_at,
                                imei1 = it.imei1,
                                action = "Unlocked"
                            )
                        }

                    if (historyList.isEmpty()) {
                        EmptyHistoryUI()
                    } else {
                        HistoryListUI(historyList)
                    }
                }

                else -> {}
            }
        }
    }
}

@Composable
fun EmptyHistoryUI() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.LockOpen,
                contentDescription = null,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("No History Yet", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text("Deleted devices will appear here", fontSize = 14.sp)
        }
    }
}


@Composable
fun HistoryListUI(historyList: List<History>) {
    Column {
        StatsCard(historyList)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(historyList) { history ->
                HistoryItem(history)
            }
        }
    }
}


@Composable
fun StatsCard(historyList: List<History>) {
    val thisMonth = historyList.count { history ->
        val currentMonth = java.text.SimpleDateFormat("MM", java.util.Locale.getDefault())
            .format(java.util.Date())
        history.date.split("/").getOrNull(1) == currentMonth
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem("Total Unlocked", historyList.size.toString(), AppColors.Success)
            VerticalDivider()
            StatItem("This Month", thisMonth.toString(), AppColors.Accent)
            VerticalDivider()
            StatItem("Today", "0", AppColors.Warning)
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Black
        )
    }
}

@Composable
fun VerticalDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(40.dp)
            .background(Color.LightGray)
    )
}

@Composable
fun HistoryItem(history: History) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                AppColors.Success,
                                Color(0xFF66BB6A)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // User Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = history.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_call),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = history.number,
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_recent_history),
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = history.date,
                        fontSize = 12.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                // IMEI Info
                Text(
                    text = "IMEI: ${history.imei1}",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            // Status Badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = AppColors.Success.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LockOpen,
                        contentDescription = null,
                        tint = AppColors.Success,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Unlocked",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.Success
                    )
                }
            }
        }
    }
}