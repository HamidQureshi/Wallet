package com.hamid.data.utils.helper

import com.hamid.data.model.TransactionDBModel


class MockDBResponse {
    companion object {

        private val transaction1 = TransactionDBModel(
            "4524ce25c3134b42970dd94c6d2096a81dc9fb7381b986fe5eb57d98ede7655d",
            559182,
            1542639840,
            17996,
            true
        )

        private val transaction2 = TransactionDBModel(
            "cbc06203f949804a512290ade05dcab35cf30c16b43bb0ede6f5074f1f8c3b9e",
            -612687,
            1547220743,
            36881,
            false
        )

        val transactionResponseList = listOf(transaction1, transaction2)

    }
}