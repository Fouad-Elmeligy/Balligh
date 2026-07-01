package com.example.ballighandroidapp.features.auth.login.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.components.BallighButton
import com.example.ballighandroidapp.components.BallighTextField
import com.example.ballighandroidapp.components.LoadingOverlay
import com.example.ballighandroidapp.features.auth.login.viewmodel.LoginViewModel
import com.example.ballighandroidapp.ui.theme.Primary

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onForgotPassword: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF004D40),
                            Primary
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Surface(
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(20.dp)),
                color = Color.White.copy(alpha = 0.2f)
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
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = stringResource(id = R.string.promo_login_subtitle),
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                ) {
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
                        value = viewModel.nationalId,
                        onValueChange = { viewModel.onNationalIdChange(it) },
                        label = stringResource(id = R.string.user_national_id),
                        placeholder = stringResource(id = R.string.prompt_enter_id),
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
                        placeholder = stringResource(id = R.string.prompt_enter_password),
                        leadingIcon = Icons.Default.Lock,
                        isPassword = true,
                        isError = viewModel.passwordErrorResId != null,
                        errorMessage = viewModel.passwordErrorResId?.let { stringResource(id = it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(
                                checked = viewModel.rememberMe,
                                onCheckedChange = { viewModel.onRememberMeChange(it) },
                                colors = CheckboxDefaults.colors(checkedColor = Primary)
                            )
                            Text(
                                text = stringResource(R.string.auth_remember_me),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Text(
                            text = stringResource(R.string.auth_forgot_password),
                            color = Primary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onForgotPassword() }
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    BallighButton(
                        text = stringResource(id = R.string.action_login),
                        onClick = { viewModel.login(onLoginSuccess) }
                    )

                    Spacer(modifier = Modifier.height(40.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = stringResource(R.string.auth_no_account),
                                color = Color.Gray,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.auth_create_account),
                                color = Primary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.clickable { onNavigateToRegister() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = stringResource(id = R.string.settings_privacy_policy),
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                        Text(text = "•", color = Color.LightGray)
                        Text(
                            text = stringResource(id = R.string.settings_terms_of_use),
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                        Text(text = "•", color = Color.LightGray)
                        Text(
                            text = stringResource(id = R.string.settings_support),
                            color = Color.LightGray,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }

    LoadingOverlay(isLoading = viewModel.isLoading)
}