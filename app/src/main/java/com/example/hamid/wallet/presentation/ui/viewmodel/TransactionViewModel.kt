package com.example.hamid.wallet.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hamid.data.utils.EspressoIdlingResource
import com.hamid.domain.model.model.RemoteApiResponse
import com.hamid.domain.model.model.Response
import com.hamid.domain.model.model.Status
import com.hamid.domain.model.usecases.WalletUseCase
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TransactionViewModel @Inject
constructor(
    val walletUseCase: WalletUseCase
) : ViewModel() {

    companion object {
        private const val TAG = "viewModel"
    }

    val formattedList = MutableLiveData<Response>()
    val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun getData() {

        compositeDisposable.add(
            walletUseCase.getTransactionsFromDb()
                .subscribe({ response ->
                    Log.d(TAG, "On Next Called")
                    if (response.data.isEmpty()) {
                        getTransactionsFromServer()
                    }
                    formattedList.postValue(response)
                }, { error ->
                    Log.d(TAG, "On Error Called $error")
                    formattedList.postValue(Response(Status.ERROR, emptyList()))
                    getTransactionsFromServer()
                }, {
                    Log.d(TAG, "On Complete Called")
                })
        )

    }

    fun getTransactionsFromServer() {
        EspressoIdlingResource.increment()
        compositeDisposable.add(
            walletUseCase.getTransactionsFromServer()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .subscribeWith(object : DisposableSingleObserver<RemoteApiResponse>() {

                    override fun onError(e: Throwable) {
                        Log.e("error", e.message + "")
                        formattedList.postValue(Response(Status.ERROR, emptyList()))
                        EspressoIdlingResource.decrement()
                    }

                    override fun onSuccess(response: RemoteApiResponse) {
                        walletUseCase.setBalance(response.wallet.balance)
                        walletUseCase.insertTransactionsToDB(response.transactions)

                        EspressoIdlingResource.decrement()
                    }
                })
        )
    }

    fun getBalance() = walletUseCase.getBalance()

    public override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }

}
