package dz.pharmstockdz.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dz.pharmstockdz.R
import dz.pharmstockdz.ui.StockViewModel
import dz.pharmstockdz.ui.components.LotCard
import dz.pharmstockdz.util.ExpiryStatus
import dz.pharmstockdz.util.classifyExpiry
import dz.pharmstockdz.util.epochDayToLocalDate

@Composable
fun DashboardScreen(vm: StockViewModel, onOpenProduct: (Long) -> Unit) {
    val stats by vm.stats.collectAsStateWithLifecycle()
    val lots by vm.allLots.collectAsStateWithLifecycle()

    val attention = lots.filter {
        val s = classifyExpiry(epochDayToLocalDate(it.lot.expiryEpochDay)).status
        s == ExpiryStatus.EXPIRED || s == ExpiryStatus.CRITICAL
    }.sortedBy { it.lot.expiryEpochDay }

    LazyColumn(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
        item {
            Row(
                Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(stringResource(R.string.stat_products), stats.productCount.toString(), Modifier.weight(1f))
                StatCard(stringResource(R.string.stat_expired), stats.expiredLots.toString(), Modifier.weight(1f))
            }
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatCard(stringResource(R.string.stat_critical), stats.criticalLots.toString(), Modifier.weight(1f))
                StatCard(stringResource(R.string.stat_warning), stats.warningLots.toString(), Modifier.weight(1f))
            }
        }
        item {
            Text(
                stringResource(R.string.attention_needed, attention.size),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(12.dp)
            )
        }
        items(attention, key = { it.lot.id }) { entry ->
            LotCard(entry, showProductName = true, onClick = { onOpenProduct(entry.product.id) })
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(value, style = MaterialTheme.typography.headlineMedium)
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}
