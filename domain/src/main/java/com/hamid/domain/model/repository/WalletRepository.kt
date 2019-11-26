package com.hamid.domain.model.repository

import com.hamid.domain.model.model.RemoteApiResponse
import com.hamid.domain.model.model.Response
import com.hamid.domain.model.model.Transaction
import io.reactivex.Flowable
import io.reactivex.Single

/**
 * Repository interface to be implemented by Data layer.
 */
interface WalletRepository {

    fun getTransactionsFromDb(): Flowable<Response>

    fun getTransactionsFromServer(): Single<RemoteApiResponse>

    fun insertTransactionsToDB(transactions: List<Transaction>)

    fun nukeDB()

    fun getBalance(): String

    fun setBalance(balance: Int)

}