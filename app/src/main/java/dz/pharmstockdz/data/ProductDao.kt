package dz.pharmstockdz.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert
    suspend fun insert(product: Product): Long

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

    @Query("SELECT * FROM products ORDER BY name COLLATE NOCASE ASC")
    fun getAll(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getById(id: Long): Flow<Product?>

    @Query("SELECT * FROM products WHERE name LIKE '%' || :query || '%' ORDER BY name COLLATE NOCASE ASC")
    fun search(query: String): Flow<List<Product>>
}
