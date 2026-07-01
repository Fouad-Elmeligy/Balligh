package com.example.ballighandroidapp.helpers

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageUtils {
    /**
     * Copies an image from a given Uri to the app's internal storage.
     * Returns the absolute path of the saved file, or null if it fails.
     */
    fun saveUriToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            
            // Create a dedicated directory for profile images
            val directory = File(context.filesDir, "profile_images")
            if (!directory.exists()) directory.mkdirs()

            // Generate a unique file name to avoid overwriting and caching issues
            val file = File(directory, "profile_${UUID.randomUUID()}.jpg")
            val outputStream = FileOutputStream(file)

            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
