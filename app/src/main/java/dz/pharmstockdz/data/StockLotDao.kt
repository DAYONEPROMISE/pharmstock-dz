package dz.pharmstockdz.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface StockLotDao {
    @Insert
    suspend fun insert(lot: StockLot): Long

    @Update
    suspend fun update(lot: StockLot)

    @Delete
    suspend fun delete(lot: StockLot)

    @Query("SELECT * FROM stock_lots WHERE productId = :productId ORDER BY expiryEpochDay ASC")
    fun getLotsForProduct(productId: Long): Flow<List<StockLot>>

    @Transaction
    @Query("SELECT * FROM stock_lots ORDER BY expiryEpochDay ASC")
    fun getAllLotsWithProduct(): Flow<List<LotWithProduct>>

    @Transaction
    @Query("SELECT * FROM stock_lots WHERE expiryEpochDay <= :beforeEpochDay ORDER BY expiryEpochDay ASC")
    fun getLotsExpiringBefore(beforeEpochDay: Long): Flow<List<LotWithProduct>>

    @Transaction
    @Query("SELECT * FROM stock_lots WHERE expiryEpochDay <= :beforeEpochDay ORDER BY expiryEpochDay ASC")
    suspend fun getLotsExpiringBeforeOnce(beforeEpochDay: Long): List<LotWithProduct>

    @Query("SELECT COUNT(*) FROM stock_lots WHERE expiryEpochDay < :todayEpochDay")
    fun countExpired(todayEpochDay: Long): Flow<Int>
}
