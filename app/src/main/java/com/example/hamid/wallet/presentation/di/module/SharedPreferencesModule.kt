package com.example.hamid.wallet.presentation.di.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.hamid.data.local.sharedPref.WalletSharedPreference
import com.hamid.domain.model.utils.Constants
import dagger.Module
import dagger.Provides
import io.reactivex.annotations.NonNull
import javax.inject.Singleton

@Module
class SharedPreferencesModule {

    @Provides
    fun provideSharedPreferences(@NonNull context: Application): SharedPreferences {
        return context.getSharedPreferences(Constants.sharedPrefName, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideAppSharedPreferences(sharedPreference: SharedPreferences): WalletSharedPreference {
        return WalletSharedPreference(sharedPreference)
    }
}