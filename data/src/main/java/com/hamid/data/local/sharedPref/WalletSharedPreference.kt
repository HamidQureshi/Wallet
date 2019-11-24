package com.hamid.data.local.sharedPref

import android.content.SharedPreferences
import com.hamid.domain.model.utils.Constants

class WalletSharedPreference
constructor(private val sharedPreferences: SharedPreferences) {

    fun setBalance(balance: Int) {
        with(sharedPreferences.edit()) {
            putInt(Constants.balanceKey, balance)
            commit()
        }
    }

    fun getBalance(): Int {
        return sharedPreferences.getInt(Constants.balanceKey, 0)
    }

}