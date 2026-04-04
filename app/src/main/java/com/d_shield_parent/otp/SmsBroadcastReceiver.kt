package com.d_shield_parent.otp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.http.UrlRequest
import android.os.Build
import androidx.annotation.RequiresExtension
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes

class SmsBroadcastReceiver : BroadcastReceiver() {

    var otpReceiveListener: OtpReceiveListener? = null

    interface OtpReceiveListener {
        fun onOtpReceived(otp: String)
        fun onOtpTimeout()
    }

    @RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as? com.google.android.gms.common.api.Status

            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras?.getString(SmsRetriever.EXTRA_SMS_MESSAGE) ?: ""
                    val otp = extractOtp(message)
                    if (otp != null) otpReceiveListener?.onOtpReceived(otp)
                }
                CommonStatusCodes.TIMEOUT -> {
                    otpReceiveListener?.onOtpTimeout()
                }
            }
        }
    }

    // Extracts 6-digit OTP from DShield Pro SMS
    private fun extractOtp(message: String): String? {
        val pattern = Regex("\\b\\d{6}\\b")
        return pattern.find(message)?.value
    }
}