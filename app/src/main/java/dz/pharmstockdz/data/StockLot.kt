package dz.pharmstockdz.data

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(
    tableName = "stock_lots",
    foreignKeys = [
        ForeignKey(
            entity = Product::class,
            parentColumns = ["id"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("productId"), Index("expiryEpochDay")]
)
data class StockLot(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val productId: Long,
    val lotNumber: String,
    val quantity: Int,
    /** Expiry date as epoch day (LocalDate.toEpochDay). Timezone-free. */
    val expiryEpochDay: Long,
    val supplierNote: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class LotWithProduct(
    @Embedded val lot: StockLot,
    @Relation(parentColumn = "productId", entityColumn = "id")
    val product: Product
)
