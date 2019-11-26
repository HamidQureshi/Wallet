package com.hamid.domain.model.usecases

import com.hamid.domain.model.repository.WalletRepository
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.only
import com.nhaarman.mockitokotlin2.verify
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class WalletUseCaseTest {

    private val repo: WalletRepository = mock()

    private lateinit var useCase: WalletUseCase

    @Before
    fun setUp() {

        Mockito.`when`(
            repo.getBalance()
        )
            .thenReturn("0.0 BTC")

        useCase = WalletUseCase(repo)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun getTransactionsFromDb() {

        useCase.getTransactionsFromDb()

        verify(repo, only())
            .getTransactionsFromDb()
    }

    @Test
    fun getTransactionsFromServer() {
        useCase.getTransactionsFromServer()

        verify(repo, only())
            .getTransactionsFromServer()
    }

    @Test
    fun nukeDB() {
        useCase.nukeDB()

        verify(repo, only())
            .nukeDB()
    }

    @Test
    fun getBalance() {
        val balance = useCase.getBalance()

        verify(repo, only())
            .getBalance()

        assertFalse(balance.isNullOrEmpty())
    }

}