package dz.pharmstockdz.notify

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dz.pharmstockdz.data.AppDatabase
import java.time.LocalDate
import java.util.concurrent.TimeUnit

/** Runs once a day, fully offline, and posts a local notification if lots expired/are critical. */
class ExpiryCheckWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return try {
            val db = AppDatabase.get(applicationContext)
            val cutoff = LocalDate.now().plusDays(30).toEpochDay()
            val lots = db.stockLotDao().getLotsExpiringBeforeOnce(cutoff)
            val (expired, critical) = NotificationHelper.bucket(lots)
            NotificationHelper.showExpirySummary(applicationContext, expired, critical)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}

object ExpiryScheduler {
    private const val WORK_NAME = "expiry-daily-check"

    fun scheduleDaily(context: Context) {
        val request = PeriodicWorkRequestBuilder<ExpiryCheckWorker>(24, TimeUnit.HOURS)
            .addTag(WORK_NAME)
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
