package com.hamid.data.model

import com.google.gson.annotations.SerializedName

data class TransactionOutput(
    @SerializedName("type") val type: Int,
    @SerializedName("spent") val spent: Boolean,
    @SerializedName("value") val value: Int,
    @SerializedName("tx_index") val txIndex: Int,
    @SerializedName("script") val script: String,
    @SerializedName("addr") val address: String,
    @SerializedName("n") val tx_index: Int

)