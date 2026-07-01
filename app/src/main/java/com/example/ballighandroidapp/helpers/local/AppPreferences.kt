package com.example.ballighandroidapp.helpers.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreferences @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("balligh_prefs", Context.MODE_PRIVATE)

    var isFirstTimeLaunch: Boolean
        get() = prefs.getBoolean(KEY_FIRST_TIME_LAUNCH, true)
        set(value) = prefs.edit().putBoolean(KEY_FIRST_TIME_LAUNCH, value).apply()

    var isUserLoggedIn: Boolean
        get() = prefs.getBoolean(KEY_IS_USER_LOGGED_IN, false)
        set(value) = prefs.edit().putBoolean(KEY_IS_USER_LOGGED_IN, value).apply()

    var loggedInNationalId: String?
        get() = prefs.getString(KEY_LOGGED_IN_NATIONAL_ID, null)
        set(value) = prefs.edit().putString(KEY_LOGGED_IN_NATIONAL_ID, value).apply()

    var isNotificationsEnabled: Boolean
        get() = prefs.getBoolean(KEY_NOTIFICATIONS_ENABLED, true)
        set(value) = prefs.edit().putBoolean(KEY_NOTIFICATIONS_ENABLED, value).apply()

    var currentUserRole: Int
        get() = prefs.getInt(KEY_CURRENT_USER_ROLE, 1)
        set(value) = prefs.edit().putInt(KEY_CURRENT_USER_ROLE, value).apply()

    fun logout() {
        prefs.edit()
            .putBoolean(KEY_IS_USER_LOGGED_IN, false)
            .putString(KEY_LOGGED_IN_NATIONAL_ID, null)
            .putInt(KEY_CURRENT_USER_ROLE, 1)
            .apply()
    }

    companion object {
        private const val KEY_FIRST_TIME_LAUNCH = "is_first_time_launch"
        private const val KEY_IS_USER_LOGGED_IN = "is_user_logged_in"
        private const val KEY_LOGGED_IN_NATIONAL_ID = "logged_in_national_id"
        private const val KEY_NOTIFICATIONS_ENABLED = "is_notifications_enabled"
        private const val KEY_CURRENT_USER_ROLE = "current_user_role"
    }
}