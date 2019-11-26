package com.hamid.data

import com.hamid.data.local.db.TransactionDaoImpl
import com.hamid.data.local.sharedPref.WalletSharedPreference
import com.hamid.data.model.DBTransactionModelMapperImpl
import com.hamid.data.model.PresentationTransactionModelMapperImpl
import com.hamid.data.remote.APIService
import com.hamid.domain.model.model.RemoteApiResponse
import com.hamid.domain.model.model.Response
import com.hamid.domain.model.model.Transaction
import com.hamid.domain.model.repository.WalletRepository
import com.hamid.domain.model.utils.Constants
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WalletRepositoryImpl @Inject constructor(
    private val apiService: APIService,
    private val daoImpl: TransactionDaoImpl,
    private val sharedPreference: WalletSharedPreference,
    private val mapperDB: DBTransactionModelMapperImpl,
    private val mapperPresentation: PresentationTransactionModelMapperImpl
) : WalletRepository {

    override fun getTransactionsFromDb(): Flowable<Response> {
        return daoImpl.getAllTransactions()
            .map { mapperPresentation.fromEntity(it) }
    }

    override fun getTransactionsFromServer(): Single<RemoteApiResponse> {
        return apiService.fetchTransactions(Constants.address)
    }

    override fun insertTransactionsToDB(apiResponse: List<Transaction>) =
        daoImpl.insertAll(mapperDB.fromEntity(apiResponse))

    override fun nukeDB() = daoImpl.deleteAll()

    override fun getBalance(): String {
        return mapperPresentation.convertToBTC(sharedPreference.getBalance())
    }

    override fun setBalance(balance: Int) = sharedPreference.setBalance(balance)

}




