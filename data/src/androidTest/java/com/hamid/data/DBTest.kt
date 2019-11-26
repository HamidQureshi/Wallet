package com.hamid.data

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.hamid.data.local.db.TransactionDaoImpl
import com.hamid.data.local.db.WalletRoomDatabase
import com.hamid.data.utils.helper.MockDBResponse
import org.junit.*

class DBTest {

    private lateinit var db: WalletRoomDatabase
    private lateinit var transactionDaoImpl: TransactionDaoImpl
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private val transactionList = MockDBResponse.transactionResponseList

    @Before
    fun setUp() {
        db = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(), WalletRoomDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        transactionDaoImpl = db.transactionDao()
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        db.close()
    }


    @Test
    fun getDataWhenNoDataInserted() {
        val list = transactionDaoImpl.getAllTransactions()
            .test()
            .values()[0]

        Assert.assertEquals(0, list.size)
    }

    @Test
    fun insertAndGetData() {
        transactionDaoImpl.insertAll(transactionList)

        transactionDaoImpl.getAllTransactions()
            .test()
            .assertValue { it.size == transactionList.size && it == transactionList }
    }

    @Test
    fun deleteAndGetData() {
        transactionDaoImpl.insertAll(transactionList)

        transactionDaoImpl.deleteAll()
        val list = transactionDaoImpl.getAllTransactions()
            .test()
            .values()[0]

        Assert.assertEquals(0, list.size)

    }

}


