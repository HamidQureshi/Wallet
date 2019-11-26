package com.hamid.domain.model.usecases

import com.hamid.domain.model.model.Response
import com.hamid.domain.model.model.Transaction
import com.hamid.domain.model.repository.WalletRepository
import io.reactivex.Flowable

class WalletUseCase(private val repository: WalletRepository) {

    fun getTransactionsFromDb(): Flowable<Response> = repository.getTransactionsFromDb()

    fun getTransactionsFromServer() = repository.getTransactionsFromServer()

    fun insertTransactionsToDB(apiResponse: List<Transaction>) =
        repository.insertTransactionsToDB(apiResponse)

    fun nukeDB() = repository.nukeDB()

    fun getBalance(): String = repository.getBalance()

    fun setBalance(balance: Int) = repository.setBalance(balance)
}