package com.example.hamid.wallet.presentation.di.module

import android.app.Application
import androidx.room.Room
import androidx.test.espresso.idling.CountingIdlingResource
import com.hamid.data.WalletRepositoryImpl
import com.hamid.data.local.db.TransactionDaoImpl
import com.hamid.data.local.db.WalletRoomDatabase
import com.hamid.data.local.sharedPref.WalletSharedPreference
import com.hamid.data.model.DBTransactionModelMapperImpl
import com.hamid.data.model.PresentationTransactionModelMapperImpl
import com.hamid.data.remote.APIService
import com.hamid.domain.model.repository.WalletRepository
import com.hamid.domain.model.usecases.WalletUseCase
import com.hamid.domain.model.utils.Constants
import dagger.Module
import dagger.Provides
import io.reactivex.annotations.NonNull

import javax.inject.Singleton

@Module
class DBModule {


    @Provides
    @Singleton
    fun provideDatabase(@NonNull application: Application): WalletRoomDatabase {
        return Room.databaseBuilder(
            application,
            WalletRoomDatabase::class.java,
            Constants.dbName
        )
            .allowMainThreadQueries().build()
    }


    @Provides
    @Singleton
    fun provideItemDao(@NonNull appDatabase: WalletRoomDatabase): TransactionDaoImpl {
        return appDatabase.transactionDao()
    }

    @Singleton
    @Provides
    fun provideDBModelMapper() = DBTransactionModelMapperImpl()

    @Singleton
    @Provides
    fun providePresentationModelMapper() = PresentationTransactionModelMapperImpl()

    @Provides
    @Singleton
    fun provideRepository(@NonNull apiService: APIService, @NonNull transactionDaoImpl: TransactionDaoImpl, @NonNull sharedPreference: WalletSharedPreference, @NonNull mapperDB: DBTransactionModelMapperImpl, @NonNull mapperPresentation: PresentationTransactionModelMapperImpl): WalletRepository {
        return WalletRepositoryImpl(
            apiService,
            transactionDaoImpl,
            sharedPreference,
            mapperDB,
            mapperPresentation
        )
    }

    @Provides
    @Singleton
    fun provideIdlingResource(): CountingIdlingResource {
        return CountingIdlingResource(Constants.idlingResourceName)
    }


    @Provides
    @Singleton
    fun provideUseCase(@NonNull walletRepository: WalletRepository) =
        WalletUseCase(walletRepository)

}
