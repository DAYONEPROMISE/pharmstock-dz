package dz.pharmstockdz.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val dosageForm: String = "",
    val supplierNote: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
