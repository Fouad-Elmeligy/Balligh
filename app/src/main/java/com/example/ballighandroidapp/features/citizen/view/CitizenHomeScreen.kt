package com.example.ballighandroidapp.features.citizen.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.material3.Surface

@Composable
fun CitizenHomeScreen(
    viewModel: CitizenMainViewModel,
    onReportClick: () -> Unit,
    onViewAllReports: () -> Unit
) {
    val state by viewModel.homeState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Custom Top Bar (Since we have specific header requirements)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logodark),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(id = R.string.balligh_english),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary
                )
            }
            IconButton(onClick = { /* Notifications */ }) {
                Icon(imageVector = Icons.Default.Notifications, contentDescription = null, tint = Primary)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Header
        Text(
            text = stringResource(id = R.string.format_welcome_user, state.userName),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
        Text(
            text = stringResource(id = R.string.promo_improve_city),
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Primary Action Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clickable { onReportClick() },
            shape = RoundedCornerShape(32.dp),
            color = Primary,
            shadowElevation = 8.dp
        ) {
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
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
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
                label = stringResource(id = R.string.report_resolved),
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
                text = stringResource(id = R.string.report_latest),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            TextButton(onClick = onViewAllReports) {
                Text(text = stringResource(id = R.string.action_view_all), color = Primary)
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
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
        color = Color.White
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = count, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
            Text(text = label, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun CitizenReportCard(report: ReportEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mock Image
            Surface(
                modifier = Modifier.size(80.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color.LightGray.copy(alpha = 0.3f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.splashscreen1photo), // Placeholder
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "#BAL-${report.reportID}", color = Color.Gray, fontSize = 12.sp)
                    StatusBadge(status = report.status)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = report.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF0F172A))
                Text(text = report.location ?: report.district, color = Color.Gray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(painter = painterResource(id = android.R.drawable.ic_menu_today), contentDescription = null, modifier = Modifier.size(12.dp), tint = Color.Gray)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "2 days ago", color = Color.Gray, fontSize = 12.sp) // Mock date formatting
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: Int) {
    val (text, color, bgColor) = when (status) {
        1 -> Triple(stringResource(id = R.string.status_under_review), Color(0xFFB45309), Color(0xFFFFFBEB))
        2 -> Triple(stringResource(id = R.string.status_in_progress), Color(0xFF1D4ED8), Color(0xFFEFF6FF))
        3 -> Triple(stringResource(id = R.string.status_completed), Color(0xFF047857), Color(0xFFF0FDF4))
        else -> Triple(stringResource(id = R.string.status_under_review), Color.Gray, Color.LightGray)
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(color))
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = text, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

// Helper for row icons
@Composable
fun Image(painter: androidx.compose.ui.graphics.painter.Painter, contentDescription: String?, modifier: Modifier) {
    androidx.compose.foundation.Image(painter = painter, contentDescription = contentDescription, modifier = modifier)
}

