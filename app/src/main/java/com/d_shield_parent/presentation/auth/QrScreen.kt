package com.d_shield_parent.presentation.auth

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONObject

@Composable
fun QrScreen(
    navController: NavController,
    deviceId: Int
) {
    val qrData = remember {
        JSONObject().apply {

            put("android.app.extra.PROVISIONING_MODE", 1)

            put(
                "android.app.extra.PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME",
                "com.bmdu.d_shieldchild/.receivers.DShieldAdminReceiver"
            )

            put(
                "android.app.extra.PROVISIONING_DEVICE_ADMIN_SIGNATURE_CHECKSUM",
                "E4RLLqKG03ud7dKst7TQduwZrsCISDE_M-J2x5giLUg"
            )

            put(
                "android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION",
                "https://dshieldpro.com/storage/apk/app-release.apk"
            )

            put("android.app.extra.PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED", true)
            put("android.app.extra.PROVISIONING_SKIP_ENCRYPTION", false)

            put(
                "android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE",
                JSONObject().apply {
                    put("device_id", deviceId)
                    put("auto_provision", true)
                }
            )

        }.toString()
    }


    val qrBitmap = remember(qrData) {
        generateQRCode(qrData, 600)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ComposeColor.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Scan QR Code to Provision Device",
                fontSize = 22.sp,
                color = ComposeColor(0xFFff5722),
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Android 15 Compatible Setup\nEnsure device is factory reset",
                textAlign = TextAlign.Center,
                color = ComposeColor.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // QR Code Image
            Image(
                bitmap = qrBitmap.asImageBitmap(),
                contentDescription = "D-Shield Provisioning QR Code",
                modifier = Modifier.size(280.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Device ID: $deviceId",
                color = ComposeColor(0xFFff5722),
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "⚠️ If stuck on 'Getting ready...':\n• Check internet connection\n• Ensure factory reset done\n• Try manual setup via ADB",
                color = ComposeColor.Red,
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { navController.navigateUp() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ComposeColor(0xFFff5722)
                ),
                modifier = Modifier.padding(horizontal = 40.dp)
            ) {
                Text(
                    "Done",
                    color = ComposeColor.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@SuppressLint("UseKtx")
fun generateQRCode(text: String, size: Int = 600): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, size, size)

    val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565)

    for (x in 0 until size) {
        for (y in 0 until size) {
            bitmap.setPixel(
                x,
                y,
                if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            )
        }
    }
    return bitmap
}