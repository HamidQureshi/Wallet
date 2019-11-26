package com.hamid.data.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.hamid.data.model.TransactionDBModel
import com.hamid.domain.model.repository.TransactionDao
import io.reactivex.Flowable

@Dao
interface TransactionDaoImpl : TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(items: List<TransactionDBModel>)

    @Query("SELECT * from transaction_table ORDER BY time ")
    fun getAllTransactions(): Flowable<List<TransactionDBModel>>

    @Query("DELETE FROM transaction_table")
    fun deleteAll()

}