//package com.d_shield_parent.Dashboard.model
//
//import com.google.gson.annotations.SerializedName
//
//data class ProvisioningData(
//    @SerializedName("android.app.extra.PROVISIONING_DEVICE_ADMIN_COMPONENT_NAME")
//    val deviceAdminComponentName: String,
//
////    @SerializedName("android.app.extra.PROVISIONING_DEVICE_ADMIN_SIGNATURE_CHECKSUM")
////    val deviceAdminSignatureChecksum: String,
//
//    @SerializedName("android.app.extra.PROVISIONING_DEVICE_ADMIN_PACKAGE_DOWNLOAD_LOCATION")
//    val deviceAdminPackageDownloadLocation: String,
//
//    @SerializedName("android.app.extra.PROVISIONING_LEAVE_ALL_SYSTEM_APPS_ENABLED")
//    val leaveAllSystemAppsEnabled: Boolean,
//
//    @SerializedName("android.app.extra.PROVISIONING_SKIP_ENCRYPTION")
//    val skipEncryption: Boolean,
//
//    @SerializedName("android.app.extra.PROVISIONING_ADMIN_EXTRAS_BUNDLE")
//    val adminExtrasBundle: AdminExtrasBundle
//)
//
//data class AdminExtrasBundle(
//    @SerializedName("auto_provision")
//    val autoProvision: Boolean,
//
//    @SerializedName("device_id")
//    val deviceId: String,
//
//    @SerializedName("pair_token")
//    val pairToken: String
//)