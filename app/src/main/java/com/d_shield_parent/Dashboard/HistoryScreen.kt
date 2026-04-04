package com.d_shield_parent.Dashboard

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.d_shield_parent.Dashboard.model.Transaction
import com.d_shield_parent.Dashboard.viewModel.TransactionUiState
import com.d_shield_parent.Dashboard.viewModel.TransactionViewModel
import com.d_shield_parent.SharedPreference.shareprefManager
import java.text.SimpleDateFormat
import java.util.Locale
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: TransactionViewModel = viewModel()
) {
    val context = LocalContext.current
    val token = shareprefManager.getToken() ?: "" // ← get token inside screen itself
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchHistory(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transaction History", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                is TransactionUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is TransactionUiState.Error -> {
                    Text(
                        text = (uiState as TransactionUiState.Error).message,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center).padding(16.dp)
                    )
                }

                is TransactionUiState.Success -> {
                    val transactions = (uiState as TransactionUiState.Success).transactions
                    if (transactions.isEmpty()) {
                        Text(
                            text = "No transactions found.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(transactions) { transaction ->
                                TransactionCard(transaction)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionCard(transaction: Transaction) {
    val isReverted = transaction.isReverted == 1
    val pointsColor = if (isReverted) Color(0xFFC62828) else Color(0xFF2E7D32)
    val pointsPrefix = if (isReverted) "-" else "+"
    val iconRes = if (isReverted) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(
                        color = if (isReverted) Color(0xFFFFEBEE) else Color(0xFFE8F5E9),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = iconRes,
                    contentDescription = transaction.code,
                    tint = pointsColor
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.from,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = Color(0xFF212121)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaction.description,
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaction.code,
                    fontSize = 11.sp,
                    color = Color(0xFFD4AF37) // gold color
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaction.date,
                    fontSize = 12.sp,
                    color = Color(0xFF9E9E9E)
                )
            }

            // Points
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$pointsPrefix${transaction.points}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = pointsColor
                )
                if (isReverted) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Reverted",
                        fontSize = 11.sp,
                        color = Color(0xFFC62828)
                    )
                }
            }
        }
    }
}

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