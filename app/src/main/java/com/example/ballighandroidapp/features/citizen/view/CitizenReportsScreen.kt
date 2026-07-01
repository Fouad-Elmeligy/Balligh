package com.example.ballighandroidapp.features.citizen.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.components.CitizenReportCard
import com.example.ballighandroidapp.features.citizen.viewmodel.CitizenMainViewModel
import com.example.ballighandroidapp.ui.theme.Primary

@Composable
fun CitizenReportsScreen(
    viewModel: CitizenMainViewModel
) {
    val state by viewModel.reportsState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .padding(top = 24.dp)
    ) {
        // Header
        Column(modifier = Modifier.padding(horizontal = 24.dp)) {
            Text(
                text = stringResource(id = R.string.nav_my_reports),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A)
            )
            Text(
                text = stringResource(id = R.string.promo_track_reports),
                fontSize = 15.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters = listOf(
                R.string.status_all,
                R.string.status_pending,
                R.string.status_in_progress,
                R.string.status_completed
            )
            items(filters.size) { index ->
                val isSelected = state.selectedFilter == index
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.setReportsFilter(index) },
                    label = { Text(text = stringResource(id = filters[index])) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Primary,
                        selectedLabelColor = Color.White,
                        containerColor = Color.White,
                        labelColor = Color.Gray
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (isSelected) Primary else Color(0xFFE2E8F0),
                        borderWidth = 1.dp,
                        enabled = true,
                        selected = isSelected
                    ),
                    shape = RoundedCornerShape(24.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Reports List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(state.reports) { report ->
                CitizenReportCard(report = report)
            }
        }
    }
}
