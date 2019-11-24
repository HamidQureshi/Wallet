package com.example.hamid.wallet.presentation.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.hamid.wallet.presentation.factory.ViewModelFactory
import com.example.hamid.wallet.presentation.ui.viewmodel.TransactionViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap


@Module
abstract class ViewModelModule {

    @Binds
    abstract fun bindViewModelFactory(factory: ViewModelFactory): ViewModelProvider.Factory


    @Binds
    @IntoMap
    @ViewModelKey(TransactionViewModel::class)
    protected abstract fun viewModel(viewModel: TransactionViewModel): ViewModel

}
