package com.example.ballighandroidapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.helpers.local.data.entities.ReportEntity

@Composable
fun CitizenReportCard(
    report: ReportEntity,
    onClick: (Int) -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(report.reportID) },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!report.photoUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = report.photoUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .size(70.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.metaphoto1),
                    error = painterResource(id = R.drawable.metaphoto1)
                )
            } else {
                Surface(
                    modifier = Modifier.size(70.dp),
                    shape = RoundedCornerShape(16.dp),
                    color = Color.Gray.copy(alpha = 0.05f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.metaphoto1),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "#BAL-${report.reportID}",
                        color = Color.Gray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
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
    val (labelRes, textColor, bgColor) = when (status) {
        1 -> Triple(R.string.status_under_review, Color(0xFFB45309), Color(0xFFFFFBEB))
        2 -> Triple(R.string.status_waiting, Color(0xFF1D4ED8), Color(0xFFEFF6FF))
        3 -> Triple(R.string.status_completed, Color(0xFF047857), Color(0xFFF0FDF4))
        4 -> Triple(R.string.status_closed, Color(0xFFB91C1C), Color(0xFFFEF2F2))
        else -> Triple(R.string.status_pending, Color.Gray, Color.LightGray.copy(alpha = 0.2f))
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = stringResource(id = labelRes),
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}