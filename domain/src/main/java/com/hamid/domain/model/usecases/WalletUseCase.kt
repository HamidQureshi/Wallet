package com.hamid.domain.model.usecases

import com.hamid.domain.model.model.Response
import com.hamid.domain.model.repository.WalletRepository
import io.reactivex.Flowable

class WalletUseCase(private val repository: WalletRepository) {

    fun getTransactionsFromDb(): Flowable<Response> = repository.getTransactionsFromDb()

    fun getTransactionsFromServer() = repository.getTransactionsFromServer()

    fun nukeDB() = repository.nukeDB()

    fun getBalance(): String = repository.getBalance()

    fun clearDisposable() = repository.clearDisposable()

}