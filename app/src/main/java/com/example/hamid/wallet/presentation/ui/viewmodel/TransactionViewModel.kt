package com.example.hamid.wallet.presentation.ui.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hamid.domain.model.model.Response
import com.hamid.domain.model.usecases.WalletUseCase
import io.reactivex.disposables.CompositeDisposable
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
                        walletUseCase.getTransactionsFromServer()
                    }
                    formattedList.postValue(response)
                }, { error ->
                    Log.d(TAG, "On Error Called $error")
                    walletUseCase.getTransactionsFromServer()
                }, {
                    Log.d(TAG, "On Complete Called")
                })
        )

    }

    fun getBalance() = walletUseCase.getBalance()

    public override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
        walletUseCase.clearDisposable()
    }

}
