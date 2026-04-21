package dk.soerensen.garbagev1.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dk.soerensen.garbagev1.R
import dk.soerensen.garbagev1.data.notification.NotificationHelper
import dk.soerensen.garbagev1.domain.BinRepository
import kotlinx.coroutines.flow.first

@HiltWorker
class PeriodicRecyclingReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val binRepository: BinRepository
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val bins = binRepository.getBins().first()
        val now = System.currentTimeMillis()
        val weekMs = 7L * 24 * 60 * 60 * 1000

        val overdueBins = bins.filter { bin -> now - bin.lastPickupTime > weekMs }

        if (overdueBins.isNotEmpty()) {
            val message = if (overdueBins.size == 1) {
                applicationContext.getString(R.string.notification_overdue_single, overdueBins.first().title)
            } else {
                applicationContext.getString(R.string.notification_overdue_multiple, overdueBins.size)
            }
            NotificationHelper.showNotification(applicationContext, message)
        }

        return Result.success()
    }
}
