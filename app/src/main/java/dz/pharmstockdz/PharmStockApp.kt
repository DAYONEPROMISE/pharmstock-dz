package dz.pharmstockdz

import android.app.Application
import dz.pharmstockdz.data.AppDatabase
import dz.pharmstockdz.data.StockRepository
import dz.pharmstockdz.notify.ExpiryScheduler

class PharmStockApp : Application() {
    lateinit var repository: StockRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = AppDatabase.get(this)
        repository = StockRepository(db.productDao(), db.stockLotDao())
        ExpiryScheduler.scheduleDaily(this)
    }
}
