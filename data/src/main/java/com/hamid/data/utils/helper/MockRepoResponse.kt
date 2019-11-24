package com.hamid.data.utils.helper

import com.hamid.data.model.RemoteApiResponse
import com.hamid.data.model.Transaction
import com.hamid.data.model.Wallet

class MockRepoResponse {
    companion object {

        private val transaction1 = Transaction(
            "4524ce25c3134b42970dd94c6d2096a81dc9fb7381b986fe5eb57d98ede7655d",
            559182,
            1542639840,
            17996
        )

        private val transaction2 = Transaction(
            "cbc06203f949804a512290ade05dcab35cf30c16b43bb0ede6f5074f1f8c3b9e",
            -612687,
            1547220743,
            36881
        )

        private val wallet = Wallet(8549)

        val transactionResponseList = listOf(transaction1, transaction2)

        val response = RemoteApiResponse(wallet, transactionResponseList)
    }
}