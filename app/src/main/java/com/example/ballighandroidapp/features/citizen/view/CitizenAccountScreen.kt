package com.example.ballighandroidapp.features.citizen.view

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.ballighandroidapp.R
import com.example.ballighandroidapp.features.citizen.viewmodel.CitizenAccountViewModel
import com.example.ballighandroidapp.ui.theme.Error
import com.example.ballighandroidapp.ui.theme.Primary
import android.app.Activity
import com.example.ballighandroidapp.helpers.LocaleHelper
@Composable
fun CitizenAccountScreen(
    viewModel: CitizenAccountViewModel,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    val user by viewModel.user.collectAsState()
    val context = LocalContext.current
    val comingSoonMessage = stringResource(id = R.string.feature_coming_soon)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Profile Header
        Box(contentAlignment = Alignment.BottomEnd) {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                border = BorderStroke(2.dp, Primary),
                color = Color.White
            ) {
                if (user?.profilePhotoPath != null) {
                    Image(
                        painter = rememberAsyncImagePainter(user?.profilePhotoPath),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
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
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = user?.fullName ?: "",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = onEditProfile,
            colors = ButtonDefaults.buttonColors(containerColor = Primary),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.height(44.dp)
        ) {
            Text(text = stringResource(id = R.string.user_edit_profile))
        }

        Spacer(modifier = Modifier.height(40.dp))

        // Settings Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                SettingsRow(
                    icon = Icons.Default.NotificationsNone,
                    title = stringResource(id = R.string.settings_notifications),
                    showToggle = true,
                    isToggled = viewModel.isNotificationsEnabled,
                    onToggleChange = { 
                        val resId = viewModel.toggleNotifications()
                        Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
                    },
                    onClick = { 
                        val resId = viewModel.toggleNotifications()
                        Toast.makeText(context, context.getString(resId), Toast.LENGTH_SHORT).show()
                    }
                )
                
                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = Color(0xFFF1F5F9))

                SettingsRow(
                    icon = Icons.Default.Language,
                    title = stringResource(id = R.string.settings_app_language),
                    showLanguageToggle = true,
                    currentLanguageCode = LocaleHelper.getPersistedLanguage(context),
                    onLanguageChange = { newLangCode ->
                        viewModel.changeLanguage(newLangCode)

                        LocaleHelper.setLanguage(context, newLangCode)

                        val activity = context as? Activity
                        activity?.let {
                            val intent = it.intent
                            it.finish()
                            it.startActivity(intent)

                            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                it.overrideActivityTransition(
                                    Activity.OVERRIDE_TRANSITION_OPEN,
                                    android.R.anim.fade_in,
                                    android.R.anim.fade_out
                                )
                            } else {
                                @Suppress("DEPRECATION")
                                it.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                            }
                        }
                    },
                    onClick = {}
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = Color(0xFFF1F5F9))

                SettingsRow(
                    icon = Icons.Default.HelpOutline,
                    title = stringResource(id = R.string.settings_help_support),
                    onClick = {
                        Toast.makeText(context, comingSoonMessage, Toast.LENGTH_SHORT).show()
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp), thickness = 0.5.dp, color = Color(0xFFF1F5F9))

                SettingsRow(
                    icon = Icons.Default.PrivacyTip,
                    title = stringResource(id = R.string.settings_privacy_policy),
                    onClick = {
                        Toast.makeText(context, comingSoonMessage, Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Logout Button
        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Error),
            border = BorderStroke(1.dp, Error.copy(alpha = 0.2f))
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(text = stringResource(id = R.string.action_logout), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun SettingsRow(
    icon: ImageVector,
    title: String,
    showToggle: Boolean = false,
    isToggled: Boolean = false,
    onToggleChange: (Boolean) -> Unit = {},
    showLanguageToggle: Boolean = false,
    currentLanguageCode: String = "en",
    onLanguageChange: (String) -> Unit = {},
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(44.dp),
            shape = CircleShape,
            color = Primary.copy(alpha = 0.08f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier.padding(10.dp)
            )
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF0F172A)
        )

        if (showToggle) {
            Switch(
                checked = isToggled,
                onCheckedChange = onToggleChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Primary,
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color.LightGray
                )
            )
        } else if (showLanguageToggle) {
            Surface(
                modifier = Modifier.height(32.dp),
                shape = RoundedCornerShape(16.dp),
                color = Primary.copy(alpha = 0.08f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "العربية",
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (currentLanguageCode == "ar") Primary else Color.Transparent)
                            .clickable { onLanguageChange("ar") }
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        color = if (currentLanguageCode == "ar") Color.White else Color.Gray
                    )
                    Text(
                        text = "English",
                        fontSize = 12.sp,
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (currentLanguageCode == "en") Primary else Color.Transparent)
                            .clickable { onLanguageChange("en") }
                            .padding(horizontal = 12.dp, vertical = 4.dp),
                        color = if (currentLanguageCode == "en") Color.White else Color.Gray
                    )
                }
            }
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.LightGray
            )
        }
    }
}
