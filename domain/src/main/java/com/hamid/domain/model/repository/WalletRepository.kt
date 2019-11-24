package com.hamid.domain.model.repository

import com.hamid.domain.model.model.Response
import io.reactivex.Flowable

/**
 * Repository interface to be implemented by Data layer.
 */
interface WalletRepository {

    fun getTransactionsFromDb(): Flowable<Response>

    fun getTransactionsFromServer()

    fun nukeDB()

    fun getBalance(): String

    fun setBalance(balance: Int)

    fun clearDisposable()

}