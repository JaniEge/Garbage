package dk.soerensen.garbagev1.data.geofence

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.qualifiers.ApplicationContext
import dk.soerensen.garbagev1.domain.RecyclingStation
import javax.inject.Inject

class GeofenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val geofencingClient =
        LocationServices.getGeofencingClient(context)

    fun registerGeofences(stations: List<RecyclingStation>) {

        val geofenceList = stations.take(10).map { station ->
            Geofence.Builder()
                .setRequestId(station.id)
                .setCircularRegion(
                    station.latitude,
                    station.longitude,
                    100f // radius i meter
                )
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        }

        val geofencingRequest = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            .addGeofences(geofenceList)
            .build()

        val intent = Intent(context, GeofenceReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
        )

        // ⚠️ vigtigt: check permission
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        geofencingClient.addGeofences(geofencingRequest, pendingIntent)
    }
}