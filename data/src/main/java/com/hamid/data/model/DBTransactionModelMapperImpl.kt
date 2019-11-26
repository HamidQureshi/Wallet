package com.hamid.data.model

import com.hamid.domain.model.model.Transaction
import com.hamid.domain.model.repository.TransactionModelMapper

/**
 * Maps between Room database entity and model.
 */
class DBTransactionModelMapperImpl :
    TransactionModelMapper<List<Transaction>, List<TransactionDBModel>> {
    override fun fromEntity(from: List<Transaction>) =
        convertToDBModel(from)

    private fun convertToDBModel(transactions: List<Transaction>): List<TransactionDBModel> {

        val transactionsDB = ArrayList<TransactionDBModel>()

        for (transaction in transactions) {
            transactionsDB.add(
                TransactionDBModel(
                    transaction.hash,
                    transaction.result,
                    transaction.time,
                    transaction.fee,
                    transaction.result >= 0
                )
            )
        }

        return transactionsDB
    }

}