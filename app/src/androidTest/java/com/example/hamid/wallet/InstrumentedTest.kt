package com.example.hamid.wallet

import android.app.Application
import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.espresso.IdlingRegistry
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.example.hamid.wallet.presentation.di.module.HttpClientModule
import com.example.hamid.wallet.presentation.ui.activity.TransactionActivity
import com.example.hamid.wallet.presentation.ui.viewmodel.TransactionViewModel
import com.hamid.data.WalletRepositoryImpl
import com.hamid.data.local.db.TransactionDaoImpl
import com.hamid.data.local.db.WalletRoomDatabase
import com.hamid.data.local.sharedPref.WalletSharedPreference
import com.hamid.data.model.PresentationTransactionModelMapperImpl
import com.hamid.data.remote.APIService
import com.hamid.data.utils.EspressoIdlingResource
import com.hamid.domain.model.model.Status
import com.hamid.domain.model.usecases.WalletUseCase
import com.hamid.domain.model.utils.Constants
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.*
import org.junit.Assert.assertFalse
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class InstrumentedTest {

    private lateinit var apiService: APIService
    private lateinit var db: WalletRoomDatabase
    private lateinit var sharedPreference: WalletSharedPreference
    private lateinit var transactionDaoImpl: TransactionDaoImpl
    private val mapper = PresentationTransactionModelMapperImpl()
    private lateinit var repositoryImpl: WalletRepositoryImpl
    private lateinit var walletUseCase: WalletUseCase
    private lateinit var viewModel: TransactionViewModel


    @get:Rule
    var activityRule: ActivityTestRule<TransactionActivity>? = ActivityTestRule(
        TransactionActivity::class.java
    )
    @get:Rule
    val rxSchedulerRule = RxSchedulerRule()
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    lateinit var context: Context

    @Before
    @Throws(Exception::class)
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().targetContext
        db = Room.inMemoryDatabaseBuilder(
            context, WalletRoomDatabase::class.java
        ).build()

        transactionDaoImpl = db.transactionDao()

        val httpModule = HttpClientModule()
        val cache = httpModule.provideCache(context.applicationContext as Application)
        val httpClient = httpModule.provideOkhttpClient(cache)
        val retrofit = httpModule.provideRetrofit(httpClient)

        apiService = httpModule.provideApiService(retrofit)

        sharedPreference = WalletSharedPreference(
            context.getSharedPreferences(
                Constants.sharedPrefName,
                Context.MODE_PRIVATE
            )
        )

        repositoryImpl =
            WalletRepositoryImpl(apiService, transactionDaoImpl, sharedPreference, mapper)

        walletUseCase = WalletUseCase(repositoryImpl)


        IdlingRegistry.getInstance().register(EspressoIdlingResource.idlingResource)
        viewModel = TransactionViewModel(walletUseCase)
        repositoryImpl.getTransactionsFromServer()
        viewModel.getData()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        db.close()
        activityRule = null
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.idlingResource)
    }


    @Test
    fun verifyLiveDataHasSomeValue() {
        viewModel.formattedList.observeForTesting {
            Assert.assertNotNull(viewModel.formattedList.value)
        }
    }

    @Test
    fun verifyLiveDataHasSuccessResponse() {
        viewModel.formattedList.observeForTesting {
            Assert.assertTrue(viewModel.formattedList.value!!.status == Status.SUCCESS)
        }
    }

    @Test
    fun verifyApiResponseAndDbSameSize() {

        viewModel.formattedList.observeForTesting {
            Assert.assertTrue(viewModel.formattedList.value!!.data.size == transactionDaoImpl.getAllTransactions().test().values()[0].size)
        }
    }

    @Test
    fun verifyApiResponseAndDbSameData() {

        viewModel.formattedList.observeForTesting {
            Assert.assertTrue(
                viewModel.formattedList.value!!.data == mapper.fromEntity(
                    transactionDaoImpl.getAllTransactions().test().values()[0]
                ).data
            )
        }
    }

    @Test
    fun verifyGetBalanceHasBalance() {
        assertFalse(viewModel.getBalance().isNullOrEmpty())
        assertFalse(viewModel.getBalance() == "")
    }

    //should be accessed from a common class
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

