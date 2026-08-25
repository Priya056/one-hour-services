package com.marketplace.onehour.helper.presentation.history

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class HistoryJobItem(
    val bookingId: String,
    val customerName: String,
    val category: String,
    val date: String,
    val amount: String,
    val status: String // COMPLETED, CANCELLED
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HelperJobHistoryScreen(
    onNavigateBack: () -> Unit
) {
    val historyList = remember {
        listOf(
            HistoryJobItem("BK-8842", "Priya Sharma", "Electrical Repair", "25 Aug 2026, 3:00 PM", "₹499.00", "COMPLETED"),
            HistoryJobItem("BK-8835", "Rahul Verma", "Switchboard Install", "24 Aug 2026, 5:30 PM", "₹599.00", "COMPLETED"),
            HistoryJobItem("BK-8820", "Ananya Roy", "Wiring Troubleshooting", "23 Aug 2026, 11:00 AM", "₹499.00", "CANCELLED"),
            HistoryJobItem("BK-8812", "Amit Patel", "Light Fixture Repair", "22 Aug 2026, 4:00 PM", "₹799.00", "COMPLETED")
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Helper Job History") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(historyList, key = { it.bookingId }) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = item.bookingId, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Badge(
                                containerColor = if (item.status == "COMPLETED") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                            ) {
                                Text(
                                    text = item.status,
                                    color = if (item.status == "COMPLETED") Color(0xFF2E7D32) else Color(0xFFC62828),
                                    modifier = Modifier.padding(4.dp),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Customer: ${item.customerName}", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Text(text = item.category, fontSize = 13.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = item.date, fontSize = 12.sp, color = Color.Gray)
                            Text(text = item.amount, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }
    }
}
