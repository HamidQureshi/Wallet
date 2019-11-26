package com.hamid.domain.model.model

import com.google.gson.annotations.SerializedName

data class Transaction(
    @SerializedName("hash") val hash: String,
    @SerializedName("result") val result: Int,
    @SerializedName("time") val time: Long,
    @SerializedName("fee") val fee: Int
//    @SerializedName("out") val outs: List<TransactionOutput>

)