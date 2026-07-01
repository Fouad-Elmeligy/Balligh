package com.example.ballighandroidapp.features.citizen.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.components.BallighButton
import com.example.ballighandroidapp.components.BallighTextField
import com.example.ballighandroidapp.features.citizen.viewmodel.CitizenAccountViewModel
import com.example.ballighandroidapp.helpers.ImageUtils
import com.example.ballighandroidapp.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenEditProfileScreen(
    viewModel: CitizenAccountViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val internalPath = ImageUtils.saveUriToInternalStorage(context, it)
            if (internalPath != null) {
                viewModel.editPhotoPath = internalPath
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.prepareEdit()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.user_edit_profile), fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF8FAFC)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(16.dp))

            // Profile Photo Edit - Professional Stacked Layout
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(136.dp) // Slightly larger container for dynamic badges
            ) {
                // The Main Avatar Container
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    border = BorderStroke(2.dp, Primary.copy(alpha = 0.8f)),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    if (viewModel.editPhotoPath != null) {
                        Image(
                            painter = rememberAsyncImagePainter(viewModel.editPhotoPath),
                            contentDescription = null,
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(28.dp),
                            tint = Primary.copy(alpha = 0.4f)
                        )
                    }
                }

                // Top-Right Badge: Delete Action (Clean Minimalist Circle)
                if (viewModel.editPhotoPath != null) {
                    IconButton(
                        onClick = { viewModel.editPhotoPath = null },
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(Color(0xFFFEE2E2)) // Light Soft Red Background
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Photo",
                            tint = Color(0xFFEF4444), // Crimson Red
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Bottom-End Badge: Camera/Edit Action (Floating Action Circle)
                IconButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier
                        .size(36.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(color = Primary)
                        .shadow(elevation = 2.dp, shape = CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Pick Photo",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Editable Fields
            BallighTextField(
                value = viewModel.editName,
                onValueChange = { viewModel.editName = it },
                label = stringResource(id = R.string.user_full_name),
                placeholder = stringResource(id = R.string.user_enter_full_name),
                isError = viewModel.editNameError != null,
                errorMessage = viewModel.editNameError?.let { stringResource(id = it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            BallighTextField(
                value = viewModel.editPhone,
                onValueChange = { viewModel.editPhone = it },
                label = stringResource(id = R.string.user_phone),
                placeholder = stringResource(id = R.string.user_phone),
                isError = viewModel.editPhoneError != null,
                errorMessage = viewModel.editPhoneError?.let { stringResource(id = it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Professional Look for Non-Editable Fields (Clean Disabled Style)
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.user_national_id),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64748B), // Slate Grey for subtitle
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = viewModel.user.collectAsState().value?.nationalID ?: "",
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledContainerColor = Color(0xFFF1F5F9), // Soft grey background for non-editable
                        disabledBorderColor = Color(0xFFE2E8F0),
                        disabledTextColor = Color(0xFF94A3B8)
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            BallighTextField(
                value = viewModel.editPassword,
                onValueChange = { viewModel.editPassword = it },
                label = stringResource(id = R.string.user_password),
                placeholder = "••••••••",
                isPassword = true,
                isError = viewModel.editPasswordError != null,
                errorMessage = viewModel.editPasswordError?.let { stringResource(id = it) }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Save Action Layout
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                BallighButton(
                    text = stringResource(id = R.string.action_save),
                    onClick = {
                        viewModel.updateProfile {
                            onBack()
                        }
                    },
                    enabled = !viewModel.isSaving
                )

                if (viewModel.isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp) // Fits inside or perfectly overlays without layout shifting
                    )
                }
            }
        }
    }
}