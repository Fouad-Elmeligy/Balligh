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
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    
    // 1. Define the Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            // 2. Intercept URI, copy to internal storage, and get absolute path
            val internalPath = ImageUtils.saveUriToInternalStorage(context, it)
            if (internalPath != null) {
                // 3. Update ViewModel state
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
            // Profile Photo Edit
            Box(contentAlignment = Alignment.BottomEnd) {
                Surface(
                    modifier = Modifier.size(120.dp),
                    shape = CircleShape,
                    border = BorderStroke(2.dp, Primary),
                    color = Color.White
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
                            modifier = Modifier.padding(24.dp),
                            tint = Primary.copy(alpha = 0.5f)
                        )
                    }
                }
                Surface(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Primary)
                        .clickable { 
                            // Trigger the launcher
                            imagePickerLauncher.launch("image/*") 
                        },
                    color = Primary
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(8.dp)
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

            // NON-Editable National ID
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.user_national_id),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = viewModel.user.collectAsState().value?.nationalID ?: "",
                    onValueChange = {},
                    enabled = false,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledBorderColor = Color.LightGray,
                        disabledTextColor = Color.Gray,
                        disabledLabelColor = Color.Gray
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

            // Save Button
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
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator(color = Primary)
            }
        }
    }
}
