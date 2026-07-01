package com.example.ballighandroidapp.features.citizen.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.features.citizen.viewmodel.CitizenMainViewModel
import com.example.ballighandroidapp.helpers.local.data.entities.ReportEntity
import com.example.ballighandroidapp.ui.theme.Primary
import androidx.compose.foundation.BorderStroke

@Composable
fun CitizenHomeScreen(
    viewModel: CitizenMainViewModel,
    onReportClick: () -> Unit,
    onViewAllReports: () -> Unit
) {
    val state by viewModel.homeState.collectAsState()
    
    // Logic for Dynamic First Name display
    val displayName = state.userName?.trim()?.split(" ")?.firstOrNull() ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Welcome Section - Dynamic First Name
        Text(
            text = if (displayName.isNotEmpty()) 
                stringResource(id = R.string.format_welcome_user, displayName)
            else 
                stringResource(id = R.string.promo_welcome),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A),
            lineHeight = 40.sp
        )
        Text(
            text = stringResource(id = R.string.promo_improve_city),
            fontSize = 16.sp,
            color = Color(0xFF64748B),
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Primary Action Card (Reporting) - Re-aligned to Center-Focused Style
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clickable { onReportClick() },
            shape = RoundedCornerShape(32.dp),
            color = Primary,
            shadowElevation = 8.dp
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Decorative element
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 40.dp, y = (-40).dp)
                        .size(150.dp),
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.05f)
                ) {}

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(16.dp).size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(id = R.string.report_reporting_action),
                        color = Color.White,
                        fontSize = 28.sp, // Slightly increased typography size
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Stats Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                count = state.totalReports.toString(),
                label = stringResource(id = R.string.report_plural),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                count = state.resolvedReports.toString(),
                label = "Resolved",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Latest Reports Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Latest Reports",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            TextButton(onClick = onViewAllReports) {
                Text(
                    text = stringResource(id = R.string.action_view_all),
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Column for latest reports
        state.latestReports.forEach { report ->
            CitizenReportCard(report = report)
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun StatCard(count: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        color = Color.White
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = count, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A))
            Text(text = label, fontSize = 14.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun CitizenReportCard(report: ReportEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mock Image Placeholder
            Surface(
                modifier = Modifier.size(70.dp),
                shape = RoundedCornerShape(16.dp),
                color = Primary.copy(alpha = 0.05f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.splashscreen1photo),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "#BAL-${report.reportID}", color = Color.Gray, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    StatusBadge(status = report.status)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = report.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color(0xFF0F172A),
                    maxLines = 1
                )
                Text(
                    text = report.location ?: report.district,
                    color = Color(0xFF64748B),
                    fontSize = 13.sp,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun StatusBadge(status: Int) {
    val (text, color, bgColor) = when (status) {
        1 -> Triple("Under Review", Color(0xFFB45309), Color(0xFFFFFBEB))
        2 -> Triple("Waiting", Color(0xFF1D4ED8), Color(0xFFEFF6FF))
        3 -> Triple("Solved", Color(0xFF047857), Color(0xFFF0FDF4))
        else -> Triple("Pending", Color.Gray, Color.LightGray.copy(alpha = 0.2f))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = text, color = color, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}
