package dk.soerensen.garbagev1.data.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import dk.soerensen.garbagev1.data.worker.SmartGeofenceWorker

class GeofenceReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        val geofencingEvent = GeofencingEvent.fromIntent(intent)

        if (geofencingEvent?.hasError() == true) {
            Log.e("GEOFENCE", "Error receiving geofence event")
            return
        }

        val transitionType = geofencingEvent?.geofenceTransition

        if (transitionType == Geofence.GEOFENCE_TRANSITION_ENTER) {

            Log.d("GEOFENCE", "Entered geofence!")

            val workRequest =
                OneTimeWorkRequestBuilder<SmartGeofenceWorker>().build()

            WorkManager.getInstance(context).enqueue(workRequest)
        }
    }
}