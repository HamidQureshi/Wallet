package com.example.hamid.wallet.presentation.di.module

import com.example.hamid.wallet.presentation.ui.activity.TransactionActivity
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityModule {

    @ContributesAndroidInjector
    abstract fun contributeActivity(): TransactionActivity

}
