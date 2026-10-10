package dz.pharmstockdz.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dz.pharmstockdz.R
import dz.pharmstockdz.ui.StockViewModel

@Composable
fun StockScreen(vm: StockViewModel, onOpenProduct: (Long) -> Unit, onAddProduct: () -> Unit) {
    val products by vm.products.collectAsStateWithLifecycle()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = onAddProduct) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(R.string.add_product))
            }
        }
    ) { inner ->
        Column(Modifier.fillMaxSize().padding(inner)) {
            SearchField(vm)
            LazyColumn(Modifier.fillMaxSize()) {
                items(products, key = { it.id }) { product ->
                    ListItem(
                        headlineContent = { Text(product.name) },
                        supportingContent = {
                            val sub = listOf(product.dosageForm, product.supplierNote)
                                .filter { it.isNotBlank() }.joinToString(" · ")
                            if (sub.isNotBlank()) Text(sub)
                        },
                        modifier = Modifier.clickable { onOpenProduct(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchField(vm: StockViewModel) {
    var text = remember { mutableStateOf("") }
    OutlinedTextField(
        value = text.value,
        onValueChange = {
            text.value = it
            vm.setQuery(it)
        },
        label = { Text(stringResource(R.string.search_hint)) },
        modifier = Modifier.fillMaxWidth().padding(12.dp),
        singleLine = true
    )
}
