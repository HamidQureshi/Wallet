package com.hamid.domain.model.model

import com.google.gson.annotations.SerializedName

data class Wallet(
    @SerializedName("final_balance") val balance: Int
)