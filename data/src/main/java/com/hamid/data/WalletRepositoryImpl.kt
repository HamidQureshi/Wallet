package com.hamid.data

import android.util.Log
import com.hamid.data.local.db.TransactionDaoImpl
import com.hamid.data.local.sharedPref.WalletSharedPreference
import com.hamid.data.model.RemoteApiResponse
import com.hamid.data.model.Transaction
import com.hamid.data.model.TransactionModelMapperImpl
import com.hamid.data.remote.APIService
import com.hamid.data.utils.EspressoIdlingResource
import com.hamid.domain.model.model.Response
import com.hamid.domain.model.repository.WalletRepository
import com.hamid.domain.model.utils.Constants
import io.reactivex.Flowable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepositoryImpl @Inject constructor(
    private val apiService: APIService,
    private val daoImpl: TransactionDaoImpl,
    private val sharedPreference: WalletSharedPreference,
    private val mapper: TransactionModelMapperImpl
) : WalletRepository {

    private var disposable = CompositeDisposable()

    override fun getTransactionsFromDb(): Flowable<Response> {
        return daoImpl.getAllTransactions()
            .map { mapper.fromEntity(it) }
    }

    override fun getTransactionsFromServer() {
        EspressoIdlingResource.increment()

        disposable.add(
            apiService.fetchTransactions(Constants.address)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(object : DisposableSingleObserver<RemoteApiResponse>() {

                    override fun onError(e: Throwable) {
                        Log.e("error", e.message + "")
                        EspressoIdlingResource.decrement()
                    }

                    override fun onSuccess(response: RemoteApiResponse) {

                        setBalance(response.wallet.balance)
                        insertTransactionsToDB(response.transactions)

                        EspressoIdlingResource.decrement()
                    }
                })
        )

    }

    fun insertTransactionsToDB(transactions: List<Transaction>) = daoImpl.insertAll(transactions)

    override fun nukeDB() = daoImpl.deleteAll()

    override fun getBalance(): String {
        return mapper.convertToBTC(sharedPreference.getBalance())
    }

    override fun setBalance(balance: Int) = sharedPreference.setBalance(balance)

    fun getDisposable() = disposable

    override fun clearDisposable() =
        disposable.clear()

}




