package com.hamid.data.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hamid.data.utils.helper.MockApiRepoResponse
import com.hamid.data.utils.helper.MockDBResponse
import com.hamid.data.utils.helper.MockResponseForPresentation
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test

class PresentationTransactionModelMapperImplTest {

    private val mapper = PresentationTransactionModelMapperImpl()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun fromEntity() {

        val result = mapper.fromEntity(MockDBResponse.transactionResponseList)

        assertEquals(MockResponseForPresentation.responseSuccess.status, result.status)
        assertEquals(MockResponseForPresentation.responseSuccess.data, result.data)

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
            mapper.formatTransactions(MockDBResponse.transactionResponseList)

        assertEquals(MockResponseForPresentation.formattedList, transactionsFormatted)
        assert(transactionsFormatted[0].inflow == MockApiRepoResponse.transactionResponseList[0].result > 0)
        assert(transactionsFormatted[1].inflow == MockApiRepoResponse.transactionResponseList[1].result > 0)
    }

    @Test
    fun convertResponseForUI() {
        val transactionsFormatted = mapper.convertResponseForUI(MockResponseForPresentation.formattedList)

        assertEquals(MockResponseForPresentation.responseSuccess.status, transactionsFormatted.status)
        assertEquals(MockResponseForPresentation.responseSuccess.data, transactionsFormatted.data)

        val transactionsFormattedEmpty = mapper.convertResponseForUI(emptyList())

        assertEquals(MockResponseForPresentation.responseLoading.status, transactionsFormattedEmpty.status)
        assertEquals(MockResponseForPresentation.responseLoading.data, transactionsFormattedEmpty.data)
    }


}