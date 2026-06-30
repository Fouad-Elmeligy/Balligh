package com.example.ballighandroidapp.features.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.components.BallighButton
import com.example.ballighandroidapp.components.RoleSelectionCard
import com.example.ballighandroidapp.ui.theme.Primary

@Composable
fun RoleSelectionScreen(
    onRoleSelected: (Int) -> Unit
) {
    var selectedRoleId by remember { mutableStateOf(1) } // Default to Citizen
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))
            
            Surface(
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                color = Primary.copy(alpha = 0.1f),
                modifier = Modifier.size(56.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = stringResource(id = R.string.auth_choose_account_type),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0F172A),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = stringResource(id = R.string.promo_select_role),
                fontSize = 16.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            RoleSelectionCard(
                title = stringResource(id = R.string.role_citizen),
                description = stringResource(id =R.string.role_citizen_desc),
                icon = Icons.Default.Person,
                selected = selectedRoleId == 1,
                onClick = { selectedRoleId = 1 }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RoleSelectionCard(
                title = stringResource(id = R.string.role_employee),
                description = stringResource(id = R.string.role_employee_desc),
                icon = Icons.Default.Badge,
                selected = selectedRoleId == 2,
                onClick = { selectedRoleId = 2 }
            )

            Spacer(modifier = Modifier.height(16.dp))

            RoleSelectionCard(
                title = stringResource(id = R.string.role_system_manager),
                description = stringResource(id = R.string.role_system_manager_desc),
                icon = Icons.Default.AdminPanelSettings,
                selected = selectedRoleId == 3,
                onClick = { selectedRoleId = 3 }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Pinned Button at the bottom
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 8.dp
        ) {
            Box(modifier = Modifier.padding(24.dp)) {
                BallighButton(
                    text = stringResource(id = R.string.action_continue),
                    onClick = { onRoleSelected(selectedRoleId) }
                )
            }
        }
    }
}
