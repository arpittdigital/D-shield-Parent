package com.d_shield_parent.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import java.io.File
import java.io.FileOutputStream


enum class NetworkStatus {
    Available,
    Unavailable
}

class DocumentViewModel : ViewModel() {
    private val TAG = "DocumentViewModel"

    var photo by mutableStateOf<Uri?>(null)
    var photoUri by mutableStateOf<Uri?>(null)
        private set

    var aadhaarFront by mutableStateOf<Uri?>(null)
    var aadhaarFrontUri by mutableStateOf<Uri?>(null)
        private set
    var aadhaarBack by mutableStateOf<Uri?>(null)
    var aadhaarBackUri by mutableStateOf<Uri?>(null)
        private set

    // PAN Card
    var panCard by mutableStateOf<Uri?>(null)
    var panCardUri by mutableStateOf<Uri?>(null)
        private set
    var signaturePath by mutableStateOf<String?>(null)
        private set

    // Initialize Uri for camera captures
    fun initializeUris(context: Context) {
        photoUri = createImageUri(context, "photo")
        aadhaarFrontUri = createImageUri(context, "aadhaar_front")
        aadhaarBackUri = createImageUri(context, "aadhaar_back")
        panCardUri = createImageUri(context, "pan_card")
    }

    private fun createImageUri(context: Context, name: String): Uri {
        val file = File(context.cacheDir, "${name}_${System.currentTimeMillis()}.jpg")
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    // ✅ FIXED: Proper signature saving to file
    fun updateSignature(path: Path, context: Context) {
        try {
            Log.d(TAG, "Starting signature conversion to file...")

            // Convert Compose Path to Bitmap
            val bitmap = Bitmap.createBitmap(800, 400, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            // White background
            canvas.drawColor(android.graphics.Color.WHITE)

            // Draw the path
            val paint = Paint().apply {
                color = android.graphics.Color.BLACK
                strokeWidth = 5f
                style = Paint.Style.STROKE
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                isAntiAlias = true
            }

            canvas.drawPath(path.asAndroidPath(), paint)

            // Save bitmap to file
            val file = File(context.cacheDir, "signature_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                out.flush()
            }

            signaturePath = file.absolutePath
            Log.d(TAG, "✅ Signature saved successfully at: ${file.absolutePath}")
            Log.d(TAG, "   File size: ${file.length()} bytes")
            Log.d(TAG, "   File exists: ${file.exists()}")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error saving signature", e)
            signaturePath = null
        }
    }

    fun clearAllDocuments() {
        photo = null
        aadhaarFront = null
        aadhaarBack = null
        panCard = null
        signaturePath = null
        Log.d(TAG, "All documents cleared")
    }
}