package com.hamid.data.model

import android.text.format.DateFormat
import com.hamid.domain.model.model.Response
import com.hamid.domain.model.model.Status
import com.hamid.domain.model.model.TransactionFormatted
import com.hamid.domain.model.repository.TransactionModelMapper
import java.util.*
import kotlin.collections.ArrayList

/**
 * Maps between Room database entity and model.
 */
class PresentationTransactionModelMapperImpl :
    TransactionModelMapper<List<TransactionDBModel>, Response> {
    override fun fromEntity(from: List<TransactionDBModel>) =
        convertResponseForUI(formatTransactions(from))

    fun formatTransactions(transactions: List<TransactionDBModel>): List<TransactionFormatted> {

        val formattedTransaction = ArrayList<TransactionFormatted>()

        for (transaction in transactions) {
            formattedTransaction.add(
                TransactionFormatted(
                    convertToBTC(transaction.result),
                    formatTime(transaction.time),
                    convertToBTC(transaction.fee),
                    transaction.result >= 0
                )
            )
        }

        return formattedTransaction
    }

    fun formatTime(time: Long): String {
        val cal = Calendar.getInstance(Locale.ENGLISH)
        cal.timeInMillis = time * 1000L
        return DateFormat.format("dd-MM-yyyy hh:mm:ss", cal).toString()
    }

    fun convertToBTC(amountInSatoshis: Int): String {
        return "%.8f BTC".format(amountInSatoshis / 1e8)
    }

    fun convertResponseForUI(transactions: List<TransactionFormatted>): Response {

        val response =
            Response(Status.SUCCESS, transactions)

        if (transactions.isEmpty()) {
            response.status = Status.LOADING
        }

        return response
    }
}