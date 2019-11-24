package com.hamid.domain.model.utils.helper

import com.hamid.domain.model.model.Response
import com.hamid.domain.model.model.Status
import com.hamid.domain.model.model.TransactionFormatted

class MockResponse {
    companion object {

        private val formattedTransaction1 =
            TransactionFormatted("0.00559182 BTC", "19-11-2018 03:04:00", "0.00017996 BTC", true)
        private val formattedTransaction2 =
            TransactionFormatted("-0.00612687 BTC", "11-01-2019 03:32:23", "0.00036881 BTC", false)

        val formattedList = listOf(formattedTransaction1, formattedTransaction2)

        val responseSuccess = Response(Status.SUCCESS, formattedList)
        val responseFailure = Response(Status.ERROR, emptyList())
    }
}