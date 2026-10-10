package dz.pharmstockdz.data

import dz.pharmstockdz.util.ExpiryStatus
import dz.pharmstockdz.util.classifyExpiry
import dz.pharmstockdz.util.epochDayToLocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.LocalDate

data class DashboardStats(
    val productCount: Int = 0,
    val expiredLots: Int = 0,
    val criticalLots: Int = 0,
    val warningLots: Int = 0,
    val okLots: Int = 0
)

class StockRepository(
    private val productDao: ProductDao,
    private val lotDao: StockLotDao
) {
    fun products(): Flow<List<Product>> = productDao.getAll()

    fun searchProducts(query: String): Flow<List<Product>> =
        if (query.isBlank()) productDao.getAll() else productDao.search(query.trim())

    fun productById(id: Long): Flow<Product?> = productDao.getById(id)

    fun lotsForProduct(productId: Long): Flow<List<StockLot>> =
        lotDao.getLotsForProduct(productId)

    fun allLots(): Flow<List<LotWithProduct>> = lotDao.getAllLotsWithProduct()

    fun dashboardStats(
        today: LocalDate = LocalDate.now(),
        criticalDays: Long = 30,
        warningDays: Long = 90
    ): Flow<DashboardStats> = combine(productDao.getAll(), lotDao.getAllLotsWithProduct()) { products, lots ->
        var expired = 0
        var critical = 0
        var warning = 0
        var ok = 0
        for (entry in lots) {
            when (classifyExpiry(epochDayToLocalDate(entry.lot.expiryEpochDay), today, criticalDays, warningDays).status) {
                ExpiryStatus.EXPIRED -> expired++
                ExpiryStatus.CRITICAL -> critical++
                ExpiryStatus.WARNING -> warning++
                ExpiryStatus.OK -> ok++
            }
        }
        DashboardStats(products.size, expired, critical, warning, ok)
    }

    suspend fun addProduct(name: String, dosageForm: String, supplierNote: String): Long =
        productDao.insert(Product(name = name.trim(), dosageForm = dosageForm.trim(), supplierNote = supplierNote.trim()))

    suspend fun updateProduct(product: Product) = productDao.update(product)

    suspend fun deleteProduct(product: Product) = productDao.delete(product)

    suspend fun addLot(productId: Long, lotNumber: String, quantity: Int, expiryEpochDay: Long, supplierNote: String): Long =
        lotDao.insert(
            StockLot(
                productId = productId,
                lotNumber = lotNumber.trim(),
                quantity = quantity.coerceAtLeast(0),
                expiryEpochDay = expiryEpochDay,
                supplierNote = supplierNote.trim()
            )
        )

    suspend fun updateLot(lot: StockLot) = lotDao.update(lot)

    suspend fun deleteLot(lot: StockLot) = lotDao.delete(lot)
}
