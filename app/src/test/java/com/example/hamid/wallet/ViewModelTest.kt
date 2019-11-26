package com.example.hamid.wallet

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.example.hamid.wallet.presentation.ui.viewmodel.TransactionViewModel
import com.hamid.data.WalletRepositoryImpl
import com.hamid.data.utils.helper.MockApiResponse
import com.hamid.domain.model.model.Status
import com.hamid.domain.model.usecases.WalletUseCase
import com.hamid.data.utils.helper.MockResponseForPresentation
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.only
import com.nhaarman.mockitokotlin2.verify
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.*
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement
import org.mockito.Mockito.`when`
import retrofit2.HttpException


class ViewModelTest {

    // RxSchedulerRule rule for making the RxJava to run synchronously in unit test
    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val repo: WalletRepositoryImpl = mock()

    private lateinit var walletUseCase: WalletUseCase
    private lateinit var viewModel: TransactionViewModel


    @Before
    @Throws(Exception::class)
    fun setUp() {

        `when`(
            repo.getTransactionsFromDb()
        ).thenReturn(Flowable.just(MockResponseForPresentation.responseSuccess))

        `when`(
            repo.getTransactionsFromServer()
        ).thenReturn(Single.just(MockApiResponse.response))

        walletUseCase = WalletUseCase(repo)
        viewModel = TransactionViewModel(walletUseCase)

    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
    }


    @Test
    fun getData_getDataFromDomainCalled() {
        viewModel.getData()
        verify(repo, only()).getTransactionsFromDb()
    }

    @Test
    fun getBalance_getBalanceFromDomainCalled() {
        viewModel.getBalance()
        verify(repo, only()).getBalance()
    }

    @Test
    fun getTransactionsFromServer_callsMethods() {
        viewModel.getTransactionsFromServer()
        verify(repo, only()).getBalance()
    }

    @Test
    fun getTransactionsFromServer_returnsSuccessResponse() {
        viewModel.getTransactionsFromServer()

        val value = walletUseCase.getTransactionsFromServer()
            .test()
            .values()

        assert(value.size > 0)
    }

    @Test
    fun verifyLiveDataNotNull() {

        viewModel.formattedList.observeForTesting {
            Assert.assertNotNull(viewModel.formattedList)
        }
    }

    @Test
    fun verifyLiveData_StatusSuccess() {
        viewModel.getData()

        viewModel.formattedList.observeForTesting {
            Assert.assertTrue(viewModel.formattedList.value!!.status == Status.SUCCESS)
        }
    }

    @Test
    fun verifyLiveData_StatusError() {

        `when`(
            repo.getTransactionsFromDb()
        ).thenReturn(Flowable.just(MockResponseForPresentation.responseLoading))

        viewModel.getData()

        viewModel.formattedList.observeForTesting {
            Assert.assertTrue(viewModel.formattedList.value!!.status == Status.ERROR)
        }
    }

    @Test
    fun disposableTest() {

        assert(viewModel.compositeDisposable.size() == 0)

        viewModel.getData()

        assert(viewModel.compositeDisposable.size() == 1)

        viewModel.onCleared()

        assert(viewModel.compositeDisposable.size() == 0)

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


