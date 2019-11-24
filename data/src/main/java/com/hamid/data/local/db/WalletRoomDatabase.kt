package com.hamid.data.local.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hamid.data.model.Transaction

@Database(entities = [Transaction::class], version = 1, exportSchema = false)
abstract class WalletRoomDatabase : RoomDatabase() {

    abstract fun transactionDao(): TransactionDaoImpl

}
