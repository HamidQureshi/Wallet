package com.example.hamid.wallet.presentation.di.component

import android.app.Application
import com.example.hamid.wallet.presentation.di.AppController
import com.example.hamid.wallet.presentation.di.module.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.support.AndroidSupportInjectionModule

import javax.inject.Singleton

@Singleton
@Component(modules = [HttpClientModule::class, DBModule::class, ViewModelModule::class, ActivityModule::class, SharedPreferencesModule::class, AndroidSupportInjectionModule::class])
interface AppComponent {

    fun inject(appController: AppController)

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

}

