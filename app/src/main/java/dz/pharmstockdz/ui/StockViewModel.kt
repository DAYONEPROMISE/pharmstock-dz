package dz.pharmstockdz.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import dz.pharmstockdz.data.DashboardStats
import dz.pharmstockdz.data.LotWithProduct
import dz.pharmstockdz.data.Product
import dz.pharmstockdz.data.StockLot
import dz.pharmstockdz.data.StockRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class StockViewModel(private val repo: StockRepository) : ViewModel() {
    private val query = MutableStateFlow("")

    val products: StateFlow<List<Product>> =
        query.flatMapLatest { repo.searchProducts(it) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val allLots: StateFlow<List<LotWithProduct>> =
        repo.allLots().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val stats: StateFlow<DashboardStats> =
        repo.dashboardStats().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardStats())

    fun setQuery(value: String) {
        query.value = value
    }

    fun addProduct(name: String, dosageForm: String, supplierNote: String, onDone: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repo.addProduct(name, dosageForm, supplierNote)
            onDone(id)
        }
    }

    fun updateProduct(product: Product) {
        viewModelScope.launch { repo.updateProduct(product) }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch { repo.deleteProduct(product) }
    }

    fun addLot(productId: Long, lotNumber: String, quantity: Int, expiryEpochDay: Long, supplierNote: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repo.addLot(productId, lotNumber, quantity, expiryEpochDay, supplierNote)
            onDone()
        }
    }

    fun updateLot(lot: StockLot) {
        viewModelScope.launch { repo.updateLot(lot) }
    }

    fun deleteLot(lot: StockLot) {
        viewModelScope.launch { repo.deleteLot(lot) }
    }

    fun productById(id: Long) = repo.productById(id)

    fun lotsForProduct(id: Long) = repo.lotsForProduct(id)

    class Factory(private val repo: StockRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return StockViewModel(repo) as T
        }
    }
}
