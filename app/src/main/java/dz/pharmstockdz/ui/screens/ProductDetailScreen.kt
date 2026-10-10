package dz.pharmstockdz.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dz.pharmstockdz.R
import dz.pharmstockdz.data.StockLot
import dz.pharmstockdz.ui.StockViewModel
import dz.pharmstockdz.ui.components.LotCard
import dz.pharmstockdz.util.epochDayToLocalDate
import dz.pharmstockdz.util.localDateToEpochDay
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(vm: StockViewModel, productId: Long, onBack: () -> Unit) {
    val product by vm.productById(productId).collectAsStateWithLifecycle(initialValue = null)
    val lots by vm.lotsForProduct(productId).collectAsStateWithLifecycle(initialValue = emptyList())
    var showAddLot by remember { mutableStateOf(false) }
    var editingLot by remember { mutableStateOf<StockLot?>(null) }
    var confirmDeleteProduct by remember { mutableStateOf(false) }
    var lotToDelete by remember { mutableStateOf<StockLot?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product?.name ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                actions = {
                    IconButton(onClick = { confirmDeleteProduct = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete_product))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddLot = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_lot))
            }
        }
    ) { inner ->
        LazyColumn(Modifier.fillMaxSize().padding(inner)) {
            item {
                product?.let { p ->
                    Card(Modifier.fillMaxWidth().padding(12.dp)) {
                        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            if (p.dosageForm.isNotBlank()) Text(p.dosageForm)
                            if (p.supplierNote.isNotBlank()) {
                                Text(
                                    stringResource(R.string.supplier_note_label, p.supplierNote),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Text(
                                stringResource(R.string.lot_count, lots.size),
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
            items(lots, key = { it.id }) { lot ->
                LotCard(
                    dz.pharmstockdz.data.LotWithProduct(lot, product ?: return@items),
                    onClick = { editingLot = lot }
                )
            }
        }
    }

    if (showAddLot) {
        LotDialog(
            title = stringResource(R.string.add_lot),
            onDismiss = { showAddLot = false },
            onConfirm = { number, qty, expiry, note ->
                vm.addLot(productId, number, qty, expiry, note) { showAddLot = false }
            }
        )
    }
    editingLot?.let { lot ->
        LotDialog(
            title = stringResource(R.string.edit_lot),
            initial = lot,
            showDelete = true,
            onDismiss = { editingLot = null },
            onDelete = { lotToDelete = lot },
            onConfirm = { number, qty, expiry, note ->
                vm.updateLot(lot.copy(lotNumber = number, quantity = qty, expiryEpochDay = expiry, supplierNote = note))
                editingLot = null
            }
        )
    }
    lotToDelete?.let { lot ->
        AlertDialog(
            onDismissRequest = { lotToDelete = null },
            confirmButton = {
                TextButton(onClick = {
                    vm.deleteLot(lot)
                    lotToDelete = null
                    editingLot = null
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { lotToDelete = null }) { Text(stringResource(R.string.cancel)) }
            },
            title = { Text(stringResource(R.string.delete_lot_title)) },
            text = { Text(stringResource(R.string.delete_lot_message, lot.lotNumber)) }
        )
    }
    if (confirmDeleteProduct) {
        AlertDialog(
            onDismissRequest = { confirmDeleteProduct = false },
            confirmButton = {
                TextButton(onClick = {
                    product?.let { vm.deleteProduct(it) }
                    confirmDeleteProduct = false
                    onBack()
                }) { Text(stringResource(R.string.delete)) }
            },
            dismissButton = {
                TextButton(onClick = { confirmDeleteProduct = false }) { Text(stringResource(R.string.cancel)) }
            },
            title = { Text(stringResource(R.string.delete_product_title)) },
            text = { Text(stringResource(R.string.delete_product_message)) }
        )
    }
}

@Composable
private fun LotDialog(
    title: String,
    initial: StockLot? = null,
    showDelete: Boolean = false,
    onDismiss: () -> Unit,
    onDelete: () -> Unit = {},
    onConfirm: (lotNumber: String, quantity: Int, expiryEpochDay: Long, note: String) -> Unit
) {
    var number by remember { mutableStateOf(initial?.lotNumber ?: "") }
    var qtyText by remember { mutableStateOf(initial?.quantity?.toString() ?: "") }
    var expiryText by remember {
        mutableStateOf(
            initial?.let { epochDayToLocalDate(it.expiryEpochDay).toString() }
                ?: LocalDate.now().plusYears(1).toString()
        )
    }
    var note by remember { mutableStateOf(initial?.supplierNote ?: "") }
    var error by remember { mutableStateOf<String?>(null) }
    val invalidDate = stringResource(R.string.invalid_date)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(number, { number = it }, label = { Text(stringResource(R.string.lot_number_hint)) }, singleLine = true)
                OutlinedTextField(qtyText, { qtyText = it.filter(Char::isDigit).take(6) }, label = { Text(stringResource(R.string.quantity_hint)) }, singleLine = true)
                OutlinedTextField(expiryText, { expiryText = it }, label = { Text(stringResource(R.string.expiry_hint)) }, singleLine = true, placeholder = { Text("AAAA-MM-JJ") })
                OutlinedTextField(note, { note = it }, label = { Text(stringResource(R.string.supplier_note_hint)) })
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
            }
        },
        confirmButton = {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (showDelete) {
                    OutlinedButton(onClick = onDelete) { Text(stringResource(R.string.delete)) }
                }
                Button(onClick = {
                    val qty = qtyText.toIntOrNull()
                    val expiry = runCatching { LocalDate.parse(expiryText.trim()) }.getOrNull()
                    if (number.isBlank() || qty == null) {
                        error = invalidDate
                    } else if (expiry == null) {
                        error = invalidDate
                    } else {
                        onConfirm(number.trim(), qty, localDateToEpochDay(expiry), note.trim())
                    }
                }) { Text(stringResource(R.string.save)) }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel)) }
        }
    )
}
