package com.hamid.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "transaction_table")
data class Transaction(
    @PrimaryKey
    @SerializedName("hash") val hash: String,
    @SerializedName("result") val result: Int,
    @SerializedName("time") val time: Long,
    @SerializedName("fee") val fee: Int
//    @SerializedName("out") val outs: List<TransactionOutput>

)