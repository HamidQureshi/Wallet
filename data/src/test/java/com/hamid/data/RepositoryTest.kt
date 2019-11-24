package com.hamid.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.hamid.data.local.db.TransactionDaoImpl
import com.hamid.data.local.sharedPref.WalletSharedPreference
import com.hamid.data.model.Transaction
import com.hamid.data.model.TransactionModelMapperImpl
import com.hamid.data.remote.APIService
import com.hamid.data.utils.helper.MockRepoResponse
import com.hamid.domain.model.utils.Constants
import com.hamid.domain.model.utils.helper.MockResponse
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.mockito.Mockito.`when`


class RepositoryTest {

    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private var apiService: APIService = mock()
    private var transactionDAOImpl: TransactionDaoImpl =
        mock()
    private var sharedPreference: WalletSharedPreference =
        mock()
    private var mapper: TransactionModelMapperImpl = mock()

    private lateinit var walletRepoImpl: WalletRepositoryImpl

    private val balanceInSatoshi = 8549
    private val balanceInBTC = "0.00008549 BTC"

    @Before
    fun setUp() {

        `when`(
            apiService.fetchTransactions(Constants.address)
        )
            .thenReturn(Single.just(MockRepoResponse.response))

        `when`(
            transactionDAOImpl.getAllTransactions()
        )
            .thenReturn(Flowable.just(MockRepoResponse.transactionResponseList))

        `when`(
            sharedPreference.getBalance()
        ).thenReturn(balanceInSatoshi)

        `when`(
            mapper.fromEntity(MockRepoResponse.transactionResponseList)
        ).thenReturn(MockResponse.responseSuccess)

        `when`(
            mapper.convertToBTC(balanceInSatoshi)
        ).thenReturn(balanceInBTC)

        walletRepoImpl =
            WalletRepositoryImpl(apiService, transactionDAOImpl, sharedPreference, mapper)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getDataFromServer_apiCalled() {

        walletRepoImpl.getTransactionsFromServer()

        verify(apiService, times(1))
            .fetchTransactions(any())

        verify(sharedPreference, times(1))
            .setBalance(any())

        verify(transactionDAOImpl, times(1))
            .insertAll(MockRepoResponse.transactionResponseList)
    }

    @Test
    fun insertTransactionListToDB_insertToDbCall() {

        walletRepoImpl.insertTransactionsToDB(MockRepoResponse.transactionResponseList)

        verify(transactionDAOImpl, only())
            .insertAll(MockRepoResponse.transactionResponseList)
    }

    @Test
    fun nukeDB_deleteDbCall() {

        walletRepoImpl.nukeDB()

        verify(transactionDAOImpl, only())
            .deleteAll()
    }

    @Test
    fun getBalance_returnsBalanceFromPref() {

        val balance = walletRepoImpl.getBalance()

        verify(sharedPreference, only())
            .getBalance()

        verify(mapper, only())
            .convertToBTC(any())

        assertFalse(balance.isNullOrEmpty())
    }

    @Test
    fun getBalance_convertsToBTC() {

        val balance = walletRepoImpl.getBalance()

        assertNotEquals(balance, balanceInSatoshi)
        assertEquals(balance, balanceInBTC)
    }

    @Test
    fun setBalance_prefCall() {

        walletRepoImpl.setBalance(any())

        verify(sharedPreference, only())
            .setBalance(any())
    }

    @Test
    fun clearDisposable_clearsDisposable() {

        val disposableSize = walletRepoImpl.getDisposable().size()

        assertTrue(disposableSize == 0)

        walletRepoImpl.getTransactionsFromServer()

        assertTrue(disposableSize + 1 == walletRepoImpl.getDisposable().size())

        walletRepoImpl.clearDisposable()

        assertTrue(0 == walletRepoImpl.getDisposable().size())

        verify(sharedPreference, only())
            .setBalance(any())


    }

    @Test
    fun getDataFromServer_returnsTransactions() {
        val expectedValue = MockRepoResponse.response

        val actualValue = apiService.fetchTransactions(Constants.address)
            .test()
            .values()

        assertEquals(expectedValue, actualValue[0])
    }

    @Test
    fun getAllTransactions_containsList() {
        val expectedValue = MockRepoResponse.transactionResponseList

        val actualValue = walletRepoImpl.getTransactionsFromDb()
            .test()
            .values()

        assertEquals(mapper.fromEntity(expectedValue).data, actualValue[0].data)

    }

    @Test
    fun getAllTransactions_containsEmptyList() {
        val expectedValue = emptyList<Transaction>()

        `when`(
            mapper.fromEntity(emptyList())
        ).thenReturn(MockResponse.responseFailure)

        `when`(transactionDAOImpl.getAllTransactions())
            .thenReturn(Flowable.just(expectedValue))

        walletRepoImpl =
            WalletRepositoryImpl(apiService, transactionDAOImpl, sharedPreference, mapper)

        val actualValue = walletRepoImpl.getTransactionsFromDb()
            .test()
            .values()

        assertEquals(expectedValue.size, actualValue[0].data.size)
    }

    @Test
    fun verifySizeOfApiResponseAndDbSame() {

        val apiData = apiService.fetchTransactions(Constants.address)
            .test()
            .values()

        val dbData = transactionDAOImpl.getAllTransactions()
            .test()
            .values()

        assertEquals(apiData[0].transactions.size, dbData[0].size)
    }

    @Test
    fun verifyApiResponseAndDbSame() {

        val apiData = apiService.fetchTransactions(Constants.address)
            .test()
            .values()

        val dbData = transactionDAOImpl.getAllTransactions()
            .test()
            .values()

        assertEquals(apiData[0].transactions, dbData[0])
    }

    @Test
    fun getDataFromServerAndDBDataSame() {

        val actualValue = walletRepoImpl.getTransactionsFromDb()
            .test()
            .values()

        assertEquals(
            mapper.fromEntity(MockRepoResponse.transactionResponseList).data,
            actualValue[0].data
        )

    }

    class RxSchedulerRule : TestRule {

        override fun apply(base: Statement, description: Description) =
            object : Statement() {
                override fun evaluate() {
                    RxAndroidPlugins.reset()
                    RxAndroidPlugins.setInitMainThreadSchedulerHandler { SCHEDULER_INSTANCE }

                    RxJavaPlugins.reset()
                    RxJavaPlugins.setIoSchedulerHandler { SCHEDULER_INSTANCE }
                    RxJavaPlugins.setNewThreadSchedulerHandler { SCHEDULER_INSTANCE }
                    RxJavaPlugins.setComputationSchedulerHandler { SCHEDULER_INSTANCE }

                    base.evaluate()
                }
            }

        companion object {
            private val SCHEDULER_INSTANCE = Schedulers.trampoline()
        }
    }

}