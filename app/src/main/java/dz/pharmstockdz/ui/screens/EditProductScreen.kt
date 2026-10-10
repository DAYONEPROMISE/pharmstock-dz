package dz.pharmstockdz.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import dz.pharmstockdz.ui.StockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(vm: StockViewModel, productId: Long?, onDone: () -> Unit) {
    val existing by vm.productById(productId ?: -1).collectAsStateWithLifecycle(initialValue = null)
    var name by remember(existing) { mutableStateOf(existing?.name ?: "") }
    var dosage by remember(existing) { mutableStateOf(existing?.dosageForm ?: "") }
    var note by remember(existing) { mutableStateOf(existing?.supplierNote ?: "") }
    var error by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (productId == null) stringResource(R.string.add_product)
                        else stringResource(R.string.edit_product)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { inner ->
        Column(
            Modifier.fillMaxSize().padding(inner).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text(stringResource(R.string.product_name_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = error && name.isBlank()
            )
            OutlinedTextField(
                value = dosage,
                onValueChange = { dosage = it },
                label = { Text(stringResource(R.string.dosage_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text(stringResource(R.string.supplier_note_hint)) },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (name.isBlank()) {
                        error = true
                    } else if (existing != null) {
                        vm.updateProduct(existing!!.copy(name = name.trim(), dosageForm = dosage.trim(), supplierNote = note.trim()))
                        onDone()
                    } else {
                        vm.addProduct(name.trim(), dosage.trim(), note.trim()) { onDone() }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }
}
