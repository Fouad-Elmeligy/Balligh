package com.example.ballighandroidapp.helpers

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageUtils {
    /**
     * Creates a temporary image Uri in cache for camera capture.
     */
    fun createImageUri(context: Context): Uri? {
        return try {
            val directory = File(context.cacheDir, "camera_photos")
            if (!directory.exists()) directory.mkdirs()

            val file = File(directory, "TEMP_${UUID.randomUUID()}.jpg")

            val authority = "${context.packageName}.fileprovider"
            FileProvider.getUriForFile(context, authority, file)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Copies an image from a Uri to the app's persistent internal storage.
     * Returns the absolute file path string.
     */
    fun saveUriToInternalStorage(context: Context, uri: Uri, subFolder: String = "report_images"): String? {
        return try {
            val contentResolver = context.contentResolver
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            
            val directory = File(context.filesDir, subFolder)
            if (!directory.exists()) directory.mkdirs()

            val file = File(directory, "IMG_${UUID.randomUUID()}.jpg")
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
