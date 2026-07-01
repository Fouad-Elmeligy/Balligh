package com.example.ballighandroidapp.features.auth.register.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.components.BallighButton
import com.example.ballighandroidapp.components.BallighTextField
import com.example.ballighandroidapp.components.LoadingOverlay
import com.example.ballighandroidapp.features.auth.register.viewmodel.RegisterViewModel
import com.example.ballighandroidapp.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onBackClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.auth_create_account),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                        Text(
                            text = stringResource(id = R.string.balligh_english),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(20.dp)),
                    color = Primary
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logolight),
                        contentDescription = null,
                        modifier = Modifier.padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(id = R.string.promo_welcome),
                    color = Primary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(id = R.string.promo_contribute),
                    color = Color.Gray,
                    fontSize = 15.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                viewModel.generalErrorResId?.let { errorId ->
                    Text(
                        text = stringResource(id = errorId),
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }

                BallighTextField(
                    value = viewModel.fullName,
                    onValueChange = { viewModel.onFullNameChange(it) },
                    label = stringResource(id = R.string.user_full_name),
                    placeholder = stringResource(id = R.string.user_enter_full_name),
                    leadingIcon = Icons.Default.Person,
                    isError = viewModel.fullNameErrorResId != null,
                    errorMessage = viewModel.fullNameErrorResId?.let { stringResource(id = it) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                BallighTextField(
                    value = viewModel.phone,
                    onValueChange = { viewModel.onPhoneChange(it) },
                    label = stringResource(id = R.string.user_phone),
                    placeholder = "05xxxxxxxx",
                    leadingIcon = Icons.Default.Phone,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    isError = viewModel.phoneErrorResId != null,
                    errorMessage = viewModel.phoneErrorResId?.let { stringResource(id = it) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                BallighTextField(
                    value = viewModel.nationalId,
                    onValueChange = { viewModel.onNationalIdChange(it) },
                    label = stringResource(id = R.string.user_national_id),
                    placeholder = "1xxxxxxxxx",
                    leadingIcon = Icons.Default.Badge,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = viewModel.nationalIdErrorResId != null,
                    errorMessage = viewModel.nationalIdErrorResId?.let { stringResource(id = it) }
                )

                Spacer(modifier = Modifier.height(20.dp))

                BallighTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = stringResource(id = R.string.user_password),
                    placeholder = "••••••••",
                    leadingIcon = Icons.Default.Lock,
                    isPassword = true,
                    isError = viewModel.passwordErrorResId != null,
                    errorMessage = viewModel.passwordErrorResId?.let { stringResource(id = it) }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = viewModel.agreedToTerms,
                        onCheckedChange = { viewModel.onAgreedToTermsChange(it) },
                        colors = CheckboxDefaults.colors(checkedColor = Primary)
                    )

                    val termsText = buildAnnotatedString {
                        append(stringResource(id = R.string.settings_agree_to) + " ")
                        withStyle(style = SpanStyle(color = Primary, fontWeight = FontWeight.Bold)) {
                            append(stringResource(id = R.string.settings_privacy_and_terms))
                        }
                    }

                    Text(
                        text = termsText,
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.clickable { viewModel.onAgreedToTermsChange(!viewModel.agreedToTerms) }
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                BallighButton(
                    text = stringResource(id = R.string.auth_create_account_short),
                    onClick = { viewModel.register(onRegisterSuccess) },
                    enabled = true
                )

                Spacer(modifier = Modifier.height(40.dp))

                val loginPrompt = buildAnnotatedString {
                    append(stringResource(id = R.string.auth_have_account) + " ")
                    withStyle(style = SpanStyle(color = Primary, fontWeight = FontWeight.Bold)) {
                        append(stringResource(id = R.string.action_login))
                    }
                }

                Text(
                    text = loginPrompt,
                    fontSize = 16.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(bottom = 32.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onNavigateToLogin() }
                )
            }

            LoadingOverlay(isLoading = viewModel.isLoading)
        }
    }
}