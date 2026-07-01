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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.components.CitizenReportCard
import com.example.ballighandroidapp.features.citizen.viewmodel.CitizenMainViewModel
import com.example.ballighandroidapp.ui.theme.Primary
import androidx.compose.foundation.BorderStroke

@Composable
fun CitizenHomeScreen(
    viewModel: CitizenMainViewModel,
    onReportClick: () -> Unit,
    onViewAllReports: () -> Unit
) {
    val state by viewModel.homeState.collectAsState()
    
    val displayName = state.userName?.trim()?.split(" ")?.firstOrNull() ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
    ) {
        // Welcome Section
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

        // Primary Action Card
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
                        fontSize = 28.sp, 
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
                Text(
                    text = stringResource(id = R.string.action_view_all),
                    color = Primary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
