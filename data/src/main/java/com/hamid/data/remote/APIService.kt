package com.hamid.data.remote

import com.hamid.data.model.RemoteApiResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {

    @GET("/multiaddr")
    fun fetchTransactions(
        @Query("active") address: String
    ): Single<RemoteApiResponse>

}
