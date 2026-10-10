package dz.pharmstockdz.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import dz.pharmstockdz.R
import dz.pharmstockdz.data.LotWithProduct
import dz.pharmstockdz.util.ExpiryStatus
import dz.pharmstockdz.util.classifyExpiry
import dz.pharmstockdz.util.epochDayToLocalDate
import java.time.LocalDate

object NotificationHelper {
    const val CHANNEL_ID = "expiry_alerts"

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= 26) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            if (manager.getNotificationChannel(CHANNEL_ID) == null) {
                manager.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID,
                        context.getString(R.string.notif_channel_name),
                        NotificationManager.IMPORTANCE_DEFAULT
                    ).apply { description = context.getString(R.string.notif_channel_desc) }
                )
            }
        }
    }

    fun showExpirySummary(context: Context, expired: List<LotWithProduct>, critical: List<LotWithProduct>) {
        ensureChannel(context)
        if (expired.isEmpty() && critical.isEmpty()) return
        val text = context.getString(
            R.string.notif_summary,
            expired.size,
            critical.size
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(context.getString(R.string.notif_title))
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1001, notification)
    }

    /** Splits lots into expired / critical buckets using the shared classifier. */
    fun bucket(
        lots: List<LotWithProduct>,
        today: LocalDate = LocalDate.now(),
        criticalDays: Long = 30
    ): Pair<List<LotWithProduct>, List<LotWithProduct>> {
        val expired = mutableListOf<LotWithProduct>()
        val critical = mutableListOf<LotWithProduct>()
        for (entry in lots) {
            when (classifyExpiry(epochDayToLocalDate(entry.lot.expiryEpochDay), today, criticalDays, Long.MAX_VALUE).status) {
                ExpiryStatus.EXPIRED -> expired.add(entry)
                ExpiryStatus.CRITICAL -> critical.add(entry)
                else -> Unit
            }
        }
        return expired to critical
    }
}
