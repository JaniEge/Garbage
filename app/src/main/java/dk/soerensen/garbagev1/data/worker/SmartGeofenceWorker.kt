package dk.soerensen.garbagev1.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.hilt.android.qualifiers.ApplicationContext
import dk.soerensen.garbagev1.domain.BinRepository
import javax.inject.Inject
import androidx.hilt.work.HiltWorker
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dk.soerensen.garbagev1.data.notification.NotificationHelper

@HiltWorker
class SmartGeofenceWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val binRepository: BinRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {

        val bins = binRepository.getBins() // Flow

        val now = System.currentTimeMillis()
        val week = 7 * 24 * 60 * 60 * 1000L

        bins.collect { list ->
            list.forEach { bin ->
                if (now - bin.lastPickupTime > week) {

                    NotificationHelper.showNotification(
                        applicationContext,
                        "Time to recycle ${bin.title} ♻️"
                    )
                }
            }
        }

        return Result.success()
    }
}