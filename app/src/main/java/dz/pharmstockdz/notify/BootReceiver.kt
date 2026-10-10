package dz.pharmstockdz.notify

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/** Re-enqueues the daily offline expiry check after a reboot. */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            ExpiryScheduler.scheduleDaily(context)
        }
    }
}
