package dz.pharmstockdz.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dz.pharmstockdz.R
import dz.pharmstockdz.data.LotWithProduct
import dz.pharmstockdz.util.ExpiryStatus
import dz.pharmstockdz.util.classifyExpiry
import dz.pharmstockdz.util.epochDayToLocalDate

@Composable
fun StatusChip(status: ExpiryStatus, days: Long) {
    val label = when (status) {
        ExpiryStatus.EXPIRED -> stringResource(R.string.status_expired)
        ExpiryStatus.CRITICAL -> stringResource(R.string.status_critical, days)
        ExpiryStatus.WARNING -> stringResource(R.string.status_warning, days)
        ExpiryStatus.OK -> stringResource(R.string.status_ok, days)
    }
    // Non-clickable badge (not a button): status is text, never color alone.
    val container = when (status) {
        ExpiryStatus.EXPIRED -> MaterialTheme.colorScheme.errorContainer
        ExpiryStatus.CRITICAL -> MaterialTheme.colorScheme.tertiaryContainer
        else -> MaterialTheme.colorScheme.surfaceVariant
    }
    val content = when (status) {
        ExpiryStatus.EXPIRED -> MaterialTheme.colorScheme.onErrorContainer
        ExpiryStatus.CRITICAL -> MaterialTheme.colorScheme.onTertiaryContainer
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    Surface(color = container, contentColor = content, shape = RoundedCornerShape(8.dp)) {
        Text(label, style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
    }
}

@Composable
fun LotCard(entry: LotWithProduct, showProductName: Boolean = false, onClick: (() -> Unit)? = null) {
    val info = classifyExpiry(epochDayToLocalDate(entry.lot.expiryEpochDay))
    // Clickable only when an action exists; otherwise a plain card (TalkBack honest).
    val cardModifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
    @Composable
    fun body() {
        Column(Modifier.padding(16.dp)) {
            if (showProductName) {
                Text(entry.product.name, style = MaterialTheme.typography.titleMedium)
            }
            Row {
                Text(stringResource(R.string.lot_number, entry.lot.lotNumber))
                Spacer(Modifier.weight(1f))
                Text(stringResource(R.string.quantity, entry.lot.quantity))
            }
            Spacer(Modifier.height(4.dp))
            Row {
                Text(
                    stringResource(
                        R.string.expiry_date,
                        epochDayToLocalDate(entry.lot.expiryEpochDay).toString()
                    )
                )
                Spacer(Modifier.weight(1f))
                StatusChip(info.status, info.daysUntilExpiry)
            }
            if (entry.lot.supplierNote.isNotBlank()) {
                Text(
                    entry.lot.supplierNote,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
    if (onClick != null) {
        Card(modifier = cardModifier, onClick = onClick) { body() }
    } else {
        Card(modifier = cardModifier) { body() }
    }
}
