package com.hamid.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transaction_table")
data class TransactionDBModel(
    @PrimaryKey
    val hash: String,
    val result: Int,
    val time: Long,
    val fee: Int,
    val inflow: Boolean
)