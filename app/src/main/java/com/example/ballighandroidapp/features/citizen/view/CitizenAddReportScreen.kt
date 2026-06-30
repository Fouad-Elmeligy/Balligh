package com.example.ballighandroidapp.features.citizen.view

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.features.citizen.viewmodel.CitizenAddReportViewModel
import com.example.ballighandroidapp.ui.theme.Primary

// ─── Screen ──────────────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitizenAddReportScreen(
    onBackClick: () -> Unit,
    onReportSent: () -> Unit,
    viewModel: CitizenAddReportViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigate away on success
    LaunchedEffect(uiState.submitSuccess) {
        if (uiState.submitSuccess) onReportSent()
    }

    // Gallery launcher
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onPhotoSelected(it) }
    }

    // Error snackbar
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
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
                        text = stringResource(R.string.report_add_new),
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
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // ── Photo capture card ──────────────────────────────────────────
            SectionLabel(text = stringResource(R.string.prompt_photo_location))

            PhotoCaptureCard(
                photoUri = uiState.photoUri,
                onTap = { galleryLauncher.launch("image/*") }
            )

            // ── Geographic location card ────────────────────────────────────
            SectionLabel(text = stringResource(R.string.prompt_location_geographic))

            LocationCard(
                location = uiState.location,
                isEditing = uiState.isLocationEditing,
                onEditToggle = { viewModel.onLocationEditToggle() },
                onLocationChanged = { viewModel.onLocationChanged(it) }
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

            // ── Send button ─────────────────────────────────────────────────
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

            // ── Save as draft button ────────────────────────────────────────
            OutlinedButton(
                onClick = { viewModel.saveAsDraft() },
                enabled = !uiState.isSubmitting && !uiState.isGeneratingDraft,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(Primary)
                ),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary)
            ) {
                Text(
                    text = stringResource(R.string.action_save_draft),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ─── Sub-components ───────────────────────────────────────────────────────────

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

@Composable
private fun PhotoCaptureCard(
    photoUri: Uri?,
    onTap: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .clickable { onTap() },
        color = Color.White,
        tonalElevation = 2.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        if (photoUri != null) {
            AsyncImage(
                model = photoUri,
                contentDescription = stringResource(R.string.action_take_photo),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
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

@Composable
private fun LocationCard(
    location: String,
    isEditing: Boolean,
    onEditToggle: () -> Unit,
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
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Primary,
                    modifier = Modifier.size(22.dp)
                )
                Column {
                    if (isEditing) {
                        OutlinedTextField(
                            value = location,
                            onValueChange = onLocationChanged,
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF1E293B),
                                fontWeight = FontWeight.SemiBold
                            ),
                            modifier = Modifier.fillMaxWidth(0.85f),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            )
                        )
                    } else {
                        Text(
                            text = location,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = stringResource(R.string.prompt_location_auto),
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            TextButton(onClick = onEditToggle) {
                Text(
                    text = if (isEditing) stringResource(R.string.action_save)
                           else stringResource(R.string.action_edit),
                    color = Primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }
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
