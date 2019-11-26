package com.hamid.data.model

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hamid.data.utils.helper.MockApiRepoResponse
import com.hamid.data.utils.helper.MockDBResponse
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class DBModelMapperImplTest {

    private val mapper = DBTransactionModelMapperImpl()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun fromEntity() {
        val result = mapper.fromEntity(MockApiRepoResponse.transactionResponseList)

        assertEquals(MockDBResponse.transactionResponseList, result)
        assertEquals(MockDBResponse.transactionResponseList[0], result[0])
        assertEquals(MockDBResponse.transactionResponseList[1], result[1])
    }

}