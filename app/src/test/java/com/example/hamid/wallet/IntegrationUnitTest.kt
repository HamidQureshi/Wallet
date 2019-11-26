package com.example.hamid.wallet

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.hamid.wallet.presentation.ui.viewmodel.TransactionViewModel
import com.hamid.data.WalletRepositoryImpl
import com.hamid.data.local.db.TransactionDaoImpl
import com.hamid.data.local.sharedPref.WalletSharedPreference
import com.hamid.data.model.DBTransactionModelMapperImpl
import com.hamid.data.model.PresentationTransactionModelMapperImpl
import com.hamid.data.remote.APIService
import com.hamid.data.utils.helper.MockApiRepoResponse
import com.hamid.data.utils.helper.MockDBResponse
import com.hamid.domain.model.usecases.WalletUseCase
import com.hamid.domain.model.utils.Constants
import com.hamid.data.utils.helper.MockResponseForPresentation
import com.nhaarman.mockitokotlin2.mock
import io.reactivex.Flowable
import io.reactivex.Single
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.`when`

class IntegrationUnitTest {

    @get:Rule
    val rxSchedulerRule = ViewModelTest.RxSchedulerRule()
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private var apiService: APIService = mock()
    private var transactionDAOImpl: TransactionDaoImpl = mock()
    private var sharedPreference: WalletSharedPreference = mock()
    private var mapperDB: DBTransactionModelMapperImpl = mock()
    private var mapperPresentation: PresentationTransactionModelMapperImpl = mock()

    private lateinit var walletRepoImpl: WalletRepositoryImpl
    private lateinit var walletUseCase: WalletUseCase
    private lateinit var viewModel: TransactionViewModel

    @Before
    @Throws(Exception::class)
    fun setUp() {

        `when`(
            apiService.fetchTransactions(Constants.address)
        ).thenReturn(Single.just(MockApiRepoResponse.response))

        `when`(
            transactionDAOImpl.getAllTransactions()
        ).thenReturn(Flowable.just(MockDBResponse.transactionResponseList))

        `when`(
            mapperDB.fromEntity(MockApiRepoResponse.transactionResponseList)
        ).thenReturn(MockDBResponse.transactionResponseList)

        `when`(
            mapperPresentation.fromEntity(MockDBResponse.transactionResponseList)
        ).thenReturn(MockResponseForPresentation.responseSuccess)

        walletRepoImpl =
            WalletRepositoryImpl(apiService, transactionDAOImpl, sharedPreference, mapperDB, mapperPresentation)
        walletUseCase = WalletUseCase(walletRepoImpl)
        viewModel = TransactionViewModel(walletUseCase)

        viewModel.getData()

    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
    }

    @Test
    fun verifyApiResponseAndLDSameSize() {

        val apiData = apiService.fetchTransactions(Constants.address)
            .test()
            .values()

        viewModel.formattedList.observeForTesting {
            assertEquals(viewModel.formattedList.value!!.data.size, apiData[0].transactions.size)
            assertEquals(
                viewModel.formattedList.value!!.data,
                mapperPresentation.fromEntity(mapperDB.fromEntity(apiData[0].transactions)).data
            )
        }
    }

    @Test
    fun verifyDBAndLDSameSize() {

        val dbData = transactionDAOImpl.getAllTransactions()
            .test()
            .values()

        viewModel.formattedList.observeForTesting {
            assertEquals(viewModel.formattedList.value!!.data.size, dbData[0].size)
            assertEquals(
                viewModel.formattedList.value!!.data,
                mapperPresentation.fromEntity(dbData[0]).data
            )
        }
    }

    private fun <T> LiveData<T>.observeForTesting(block: () -> Unit) {
        val observer = Observer<T> { }
        try {
            observeForever(observer)
            block()
        } finally {
            removeObserver(observer)
        }
    }

}