package com.example.ballighandroidapp.features.citizen.view

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.features.citizen.viewmodel.CitizenAddReportViewModel
import com.example.ballighandroidapp.helpers.ImageUtils
import com.example.ballighandroidapp.ui.theme.Primary

// ─── Screen ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenAddReportScreen(
    reportId: Int? = null, // null or -1 => CREATE MODE; valid id => EDIT/VIEW MODE
    onBackClick: () -> Unit,
    onReportSent: () -> Unit,
    onReportDeleted: () -> Unit = onBackClick,
    viewModel: CitizenAddReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Load existing report once, if we're opened in edit mode
    LaunchedEffect(reportId) {
        if (reportId != null && reportId != -1) {
            viewModel.loadReportForEditing(reportId)
        }
    }

    // Navigate away on create/update success
    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess) onReportSent()
    }

    // Navigate away on delete success
    LaunchedEffect(uiState.deleteSuccess) {
        if (uiState.deleteSuccess) onReportDeleted()
    }

    // ── Photo source: gallery ──────────────────────────────────────────────
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val path = ImageUtils.saveUriToInternalStorage(context, it)
            if (path != null) {
                viewModel.onPhotoSelected(path)
            }
        }
    }

    // ── Photo source: camera ───────────────────────────────────────────────
    var pendingCameraUriString by rememberSaveable { mutableStateOf<String?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && pendingCameraUriString != null) {
            val uri = Uri.parse(pendingCameraUriString)
            val path = ImageUtils.saveUriToInternalStorage(context, uri)
            if (path != null) {
                viewModel.onPhotoSelected(path)
            }
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            val uri = ImageUtils.createImageUri(context)
            if (uri != null) {
                pendingCameraUriString = uri.toString()
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(context, "Error: Could not create image file", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    fun launchCamera() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            val uri = ImageUtils.createImageUri(context)
            if (uri != null) {
                pendingCameraUriString = uri.toString()
                cameraLauncher.launch(uri)
            } else {
                Toast.makeText(context, "Error: Could not create image file", Toast.LENGTH_LONG).show()
            }
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // ── Bottom sheet: choose photo source ──────────────────────────────────
    var showPhotoSourceSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()

    // ── Delete confirmation dialog ───────────────────────────────────────
    var showDeleteConfirmation by remember { mutableStateOf(false) }

    // Error snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.errorMessageResId) {
        uiState.errorMessageResId?.let {
            snackbarHostState.showSnackbar(message = context.getString(it))
            viewModel.onErrorDismissed()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF1F4F9),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (uiState.isEditMode) "Report Details" else stringResource(R.string.report_add_new),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.action_close),
                            tint = Color(0xFF1E293B)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFF1F4F9)
                )
            )
        }
    ) { innerPadding ->

        if (uiState.isLoadingReport) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Primary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Status badge (edit mode only) ────────────────────────────
            if (uiState.isEditMode) {
                ReportStatusBadge(status = uiState.reportStatus)
            }

            // ── Photo capture card ──────────────────────────────────────────
            SectionLabel(text = stringResource(R.string.prompt_photo_location))

            PhotoCaptureCard(
                photoPath = uiState.attachedPhotoPath,
                isSaving = uiState.isSubmitting,
                onTap = { showPhotoSourceSheet = true },
                onRemove = { viewModel.onPhotoRemoved() }
            )

            // ── Geographic location card ────────────────────────────────────
            SectionLabel(text = stringResource(R.string.prompt_location_geographic))

            LocationCard(
                location = uiState.location,
                onLocationChanged = { viewModel.onLocationChanged(it) }
            )

            // ── Problem Type ComboBox ───────────────────────────────────────
            SectionLabel(text = "Problem Type")

            ProblemTypeCard(
                selectedType = uiState.problemType,
                onTypeChanged = { viewModel.onProblemTypeChanged(it) }
            )

            // ── Title card ───────────────────────────────────────────────────
            SectionLabel(text = stringResource(R.string.prompt_report_title))

            TitleCard(
                title = uiState.title,
                onTitleChanged = { viewModel.onTitleChanged(it) }
            )

            // ── Draft content card ──────────────────────────────────────────
            SectionLabel(
                text = stringResource(R.string.report_drafting),
                isAiBadge = true
            )

            DraftContentCard(
                content = uiState.draftContent,
                isGenerating = uiState.isGeneratingDraft,
                onContentChanged = { viewModel.onDraftContentChanged(it) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isEditMode) {
                // ── EDIT MODE: Save Changes + Delete Report ─────────────────

                OutlinedButton(
                    onClick = { viewModel.updateReport() },
                    enabled = !uiState.isSubmitting && !uiState.isDeleting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(
                        brush = androidx.compose.ui.graphics.SolidColor(Primary)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            color = Primary,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Save Changes",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                }

                Button(
                    onClick = { showDeleteConfirmation = true },
                    enabled = !uiState.isSubmitting && !uiState.isDeleting,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    if (uiState.isDeleting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Delete Report",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            } else {
                // ── CREATE MODE: Send Report ─────────────────────────────────

                Button(
                    onClick = { viewModel.sendReport() },
                    enabled = !uiState.isSubmitting && !uiState.isGeneratingDraft,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    if (uiState.isSubmitting) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "▶  ${stringResource(R.string.report_send)}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // ── Photo source bottom sheet ───────────────────────────────────────────
    if (showPhotoSourceSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoSourceSheet = false },
            sheetState = sheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.prompt_choose_photo_source),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                PhotoSourceOption(
                    icon = Icons.Default.CameraAlt,
                    label = stringResource(R.string.action_take_photo_camera),
                    onClick = {
                        showPhotoSourceSheet = false
                        launchCamera()
                    }
                )

                PhotoSourceOption(
                    icon = Icons.Default.PhotoLibrary,
                    label = stringResource(R.string.action_choose_gallery),
                    onClick = {
                        showPhotoSourceSheet = false
                        galleryLauncher.launch("image/*")
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    // ── Delete confirmation dialog ───────────────────────────────────────
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Report?", fontWeight = FontWeight.Bold) },
            text = { Text("This action cannot be undone. Are you sure you want to delete this report?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirmation = false
                        viewModel.deleteReport()
                    }
                ) {
                    Text("Delete", color = Color(0xFFDC2626), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            }
        )
    }
}

// ─── Sub-components ───────────────────────────────────────────────────────────

@Composable
private fun ReportStatusBadge(status: Int) {
    val (label, textColor, bgColor) = when (status) {
        1 -> Triple(R.string.status_under_review, Color(0xFFB45309), Color(0xFFFFFBEB))
        2 -> Triple(R.string.status_waiting, Color(0xFF1D4ED8), Color(0xFFEFF6FF))
        3 -> Triple(R.string.status_completed, Color(0xFF047857), Color(0xFFF0FDF4))
        4 -> Triple(R.string.status_closed, Color(0xFFB91C1C), Color(0xFFFEF2F2))
        else -> Triple(R.string.status_pending, Color.Gray, Color.LightGray.copy(alpha = 0.2f))
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = bgColor,
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(R.string.report_status),
                color = textColor.copy(alpha = 0.75f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = stringResource(id = label),
                color = textColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String, isAiBadge: Boolean = false) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isAiBadge) {
            Surface(
                color = Primary.copy(alpha = 0.12f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "✦",
                    color = Primary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProblemTypeCard(
    selectedType: String,
    onTypeChanged: (String) -> Unit
) {
    val options = listOf(
        stringResource(R.string.category_general),
        stringResource(R.string.category_street_lighting),
        stringResource(R.string.category_road_damage),
        stringResource(R.string.category_water_leak),
        stringResource(R.string.category_sanitation)
    )
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it }
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = onTypeChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF1E293B),
                        fontWeight = FontWeight.SemiBold
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    placeholder = {
                        Text("Select or enter problem type", color = Color(0xFFCBD5E1))
                    },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    options.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                onTypeChanged(option)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoSourceOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 14.dp, horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = Primary.copy(alpha = 0.1f),
            modifier = Modifier.size(40.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1E293B)
        )
    }
}

@Composable
private fun PhotoCaptureCard(
    photoPath: String?,
    isSaving: Boolean,
    onTap: () -> Unit,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(16.dp))
                .clickable(enabled = photoPath == null && !isSaving) { onTap() },
            color = Color.White,
            tonalElevation = 2.dp,
            shape = RoundedCornerShape(16.dp)
        ) {
            when {
                isSaving -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Primary, strokeWidth = 2.dp)
                    }
                }
                photoPath != null -> {
                    AsyncImage(
                        model = photoPath,
                        contentDescription = stringResource(R.string.action_take_photo),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(
                                width = 1.5.dp,
                                color = Primary.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .background(Primary.copy(alpha = 0.04f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = null,
                                tint = Primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(40.dp)
                            )
                            Text(
                                text = stringResource(R.string.prompt_take_or_upload),
                                style = MaterialTheme.typography.bodySmall,
                                color = Primary.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }

        if (photoPath != null && !isSaving) {
            Surface(
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.55f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(28.dp)
                    .clickable { onRemove() }
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "delete icon",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun LocationCard(
    location: String,
    onLocationChanged: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(22.dp)
            )
            OutlinedTextField(
                value = location,
                onValueChange = onLocationChanged,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF1E293B),
                    fontWeight = FontWeight.SemiBold
                ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.prompt_location_geographic),
                        color = Color(0xFFCBD5E1)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun TitleCard(
    title: String,
    onTitleChanged: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Title,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.size(22.dp)
            )
            OutlinedTextField(
                value = title,
                onValueChange = onTitleChanged,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFF1E293B),
                    fontWeight = FontWeight.SemiBold
                ),
                placeholder = {
                    Text(
                        text = stringResource(R.string.prompt_report_title),
                        color = Color(0xFFCBD5E1)
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Primary,
                    unfocusedBorderColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                )
            )
        }
    }
}

@Composable
private fun DraftContentCard(
    content: String,
    isGenerating: Boolean,
    onContentChanged: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color.White,
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isGenerating) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        color = Primary,
                        strokeWidth = 2.dp
                    )
                    Text(
                        text = "Generating AI draft...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF64748B)
                    )
                }
            } else {
                OutlinedTextField(
                    value = content,
                    onValueChange = onContentChanged,
                    modifier = Modifier
                        .fillMaxWidth()
                        .defaultMinSize(minHeight = 160.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF1E293B),
                        lineHeight = 22.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    placeholder = {
                        Text(
                            text = stringResource(R.string.report_description),
                            color = Color(0xFFCBD5E1)
                        )
                    }
                )

                HorizontalDivider(color = Color(0xFFE2E8F0))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { /* TODO: open text editor */ }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = null,
                            tint = Primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(R.string.action_edit) + " " +
                                    stringResource(R.string.report_description),
                            color = Primary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}