package dk.soerensen.garbagev1.ui.features.recycling

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dk.soerensen.garbagev1.R

@Composable
fun RequestBackgroundLocationPermission(
    onPermissionGranted: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as Activity

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.enable_location_title)) },
        text = {
            Text(stringResource(R.string.enable_location_message))
        },
        confirmButton = {
            Button(onClick = {

                // STEP 1: Fine location først
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        1000
                    )
                }

                // STEP 2: Background (kun hvis Android 10+)
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        1001
                    )
                }

                // STEP 3: Alt OK
                else {
                    onPermissionGranted()
                }

            }) {
                Text(stringResource(R.string.allow_button))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text(stringResource(R.string.cancel_button))
            }
        }
    )
}