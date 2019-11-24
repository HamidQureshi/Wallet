package com.hamid.domain.model.model

data class TransactionFormatted(
    val result: String,
    val time: String,
    val fee: String,
    val inflow: Boolean
)