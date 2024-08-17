package com.dhiwise.figma.utils

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.dhiwise.figma.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

private val TAG = "DexterPermissionUtils"

fun checkDexterPermissions(
    mContext: Context,
    permissions: List<String>,
    onAllPermissionGranted: () -> Unit,
    isMandatoryPer: Boolean = true,
    resultLauncher: ActivityResultLauncher<Intent>? = null
) {
    Dexter.withContext(mContext)
        .withPermissions(permissions)
        .withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                if (report.areAllPermissionsGranted()) {
                    onAllPermissionGranted()
                } else {
                    if (isMandatoryPer) {
                        showMaterialDialog(
                            mContext,
                            mContext.getString(R.string.permissions_required),
                            mContext.getString(R.string.please_allow_the_required_permissions),
                            mContext.getString(R.string.ok),
                            onPositiveClick = {
                                try {
                                    openAppSettings(mContext, resultLauncher)
                                } catch (e: Exception) {
                                    Log.e(TAG, "onPermissionsChecked: error:${e.message}")
                                }
                            },
                            if (resultLauncher == null) "Cancel" else null, {},
                            isCancelable = false, showAppIcon = true
                        )
                    }
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                p0: MutableList<PermissionRequest>?,
                token: PermissionToken?
            ) {
//                if (isMandatoryPer)
                token?.continuePermissionRequest()
            }
        })
        .check()
}

fun showMaterialDialog(
    context: Context,
    title: String,
    message: String,
    positiveBtnText: String,
    onPositiveClick: () -> Unit,
    negativeBtnText: String? = null,
    onNegativeClick: (() -> Unit)? = null,
    isCancelable: Boolean = false,
    showAppIcon: Boolean = false,
): MaterialAlertDialogBuilder {

    val materialDialog = MaterialAlertDialogBuilder(context, R.style.RoundAlertDialogShapeTheme)
        .setTitle(title)
        .setMessage(message)
        .setCancelable(isCancelable)
        .setPositiveButton(positiveBtnText) { dialog, which ->
            dialog.cancel()
            onPositiveClick.invoke()
        }
    if (negativeBtnText != null)
        materialDialog.setNegativeButton(
            negativeBtnText
        ) { dialog, which ->
            dialog.cancel()
            onNegativeClick?.invoke()
        }
    if (showAppIcon) materialDialog.setIcon(R.mipmap.ic_launcher)
    materialDialog.show()
    return materialDialog
}

fun openAppSettings(
    mContext: Context,
    resultLauncher: ActivityResultLauncher<Intent>?
) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    intent.data = Uri.fromParts("package", mContext.packageName, null)
    if (resultLauncher != null)
        resultLauncher.launch(intent)
    else
        ContextCompat.startActivity(mContext, intent, null)
}


enum class PermissionType(val id: Int, val value: List<String>) {
    StoragePermission(0, storagePermission),
    CameraPermission(1, cameraPermission),
    StorageAndCameraPermission(2, storageAndCameraPermission),

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    NotificationPermission(3, notificationPermission),

    AudioPermission(4, audioPermission),

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    AudioAndNotificationPermission(5, audioAndNotificationPermission),

}


val storagePermission = listOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE
)

val cameraPermission = listOf(
    Manifest.permission.CAMERA
)

val audioPermission = listOf(
    Manifest.permission.RECORD_AUDIO
)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
val audioAndNotificationPermission = listOf(
    Manifest.permission.RECORD_AUDIO,
    Manifest.permission.POST_NOTIFICATIONS,
    Manifest.permission.READ_PHONE_STATE,

    )


val storageAndCameraPermission = listOf(
    Manifest.permission.CAMERA,
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.WRITE_EXTERNAL_STORAGE,
)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
val notificationPermission = listOf(
    Manifest.permission.POST_NOTIFICATIONS
)
