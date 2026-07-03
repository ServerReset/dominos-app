package com.dominos.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dominos.app.ui.theme.DominosGreen
import com.dominos.app.ui.theme.DominosRed

data class TrackingStage(
    val name: String,
    val icon: @Composable () -> Unit,
    val isComplete: Boolean,
    val isActive: Boolean
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    orderId: String,
    onBack: () -> Unit
) {
    val stages = remember {
        listOf(
            TrackingStage("Order Placed", { Icon(Icons.Default.Receipt, contentDescription = null) }, false, false),
            TrackingStage("Preparing", { Icon(Icons.Default.Restaurant, contentDescription = null) }, false, false),
            TrackingStage("Baking", { Icon(Icons.Default.LocalFireDepartment, contentDescription = null) }, false, false),
            TrackingStage("Quality Check", { Icon(Icons.Default.FactCheck, contentDescription = null) }, false, false),
            TrackingStage("Out for Delivery", { Icon(Icons.Default.DirectionsCar, contentDescription = null) }, false, false),
            TrackingStage("Delivered", { Icon(Icons.Default.CheckCircle, contentDescription = null) }, false, false)
        )
    }

    var currentStage by remember { mutableIntStateOf(0) }
    val timer = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (currentStage < stages.size - 1) {
            kotlinx.coroutines.delay(5000)
            if (currentStage < stages.size - 1) currentStage++
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Order") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        @Suppress("DEPRECATION")
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DominosRed,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Order #$orderId",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(32.dp))

            stages.forEachIndexed { index, stage ->
                val isComplete = index <= currentStage
                val isActive = index == currentStage

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Stage circle
                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        color = when {
                            isActive -> DominosRed
                            isComplete -> DominosGreen
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        tonalElevation = if (isActive) 4.dp else 0.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            stage.icon()
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        stage.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                        color = when {
                            isComplete -> MaterialTheme.colorScheme.onSurface
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }

                if (index < stages.size - 1) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(32.dp)
                            .padding(start = 23.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Divider(
                            modifier = Modifier.fillMaxHeight(),
                            color = if (index < currentStage) DominosGreen else MaterialTheme.colorScheme.surfaceVariant,
                            thickness = 2.dp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            if (currentStage < stages.size - 1) {
                Text(
                    "Estimated wait: 20-30 minutes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            } else {
                Card(
                    colors = CardDefaults.cardColors(containerColor = DominosGreen.copy(alpha = 0.1f))
                ) {
                    Text(
                        "Your order has been delivered!",
                        modifier = Modifier.padding(16.dp),
                        color = DominosGreen,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
