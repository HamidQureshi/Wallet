package com.hamid.data.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hamid.data.utils.helper.MockRepoResponse
import com.hamid.domain.model.utils.helper.MockResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test

class TransactionModelMapperImplTest {

    private val mapper = TransactionModelMapperImpl()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun fromEntity() {

        val result = mapper.fromEntity(MockRepoResponse.transactionResponseList)

        assertEquals(MockResponse.responseSuccess.status, result.status)
        assertEquals(MockResponse.responseSuccess.data, result.data)

    }

    @Test
    fun formatTime() {

        val timeFormatted = mapper.formatTime(1542639840)
        assertEquals("19-11-2018 03:04:00", timeFormatted)

    }

    @Test
    fun convertToBTC() {

        val amountInBTC = mapper.convertToBTC(8549)
        assertEquals("0.00008549 BTC", amountInBTC)
        assertNotEquals("0.00008549", amountInBTC)
        assertNotEquals("8549", amountInBTC)
        assertNotEquals("", amountInBTC)

    }

    @Test
    fun formatTransactions() {
        val transactionsFormatted =
            mapper.formatTransactions(MockRepoResponse.transactionResponseList)

        assertEquals(MockResponse.formattedList, transactionsFormatted)
        assert(transactionsFormatted[0].inflow == MockRepoResponse.transactionResponseList[0].result > 0)
        assert(transactionsFormatted[1].inflow == MockRepoResponse.transactionResponseList[1].result > 0)
    }

    @Test
    fun convertResponseForUI() {
        val transactionsFormatted = mapper.convertResponseForUI(MockResponse.formattedList)

        assertEquals(MockResponse.responseSuccess.status, transactionsFormatted.status)
        assertEquals(MockResponse.responseSuccess.data, transactionsFormatted.data)

        val transactionsFormattedEmpty = mapper.convertResponseForUI(emptyList())

        assertEquals(MockResponse.responseFailure.status, transactionsFormattedEmpty.status)
        assertEquals(MockResponse.responseFailure.data, transactionsFormattedEmpty.data)
    }


}