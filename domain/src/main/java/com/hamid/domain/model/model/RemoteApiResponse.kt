package com.hamid.domain.model.model

import com.google.gson.annotations.SerializedName

data class RemoteApiResponse(
    @SerializedName("wallet") val wallet: Wallet,
    @SerializedName("txs") val transactions: List<Transaction>
)