package com.example.hamid.wallet.presentation.ui.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.hamid.wallet.R
import com.example.hamid.wallet.presentation.factory.ViewModelFactory
import com.example.hamid.wallet.presentation.ui.adaptar.TransactionListAdapter
import com.example.hamid.wallet.presentation.ui.viewmodel.TransactionViewModel
import com.hamid.domain.model.model.Status
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_transaction.*
import javax.inject.Inject


class TransactionActivity : AppCompatActivity() {

    lateinit var viewModel: TransactionViewModel
    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private lateinit var itemListAdapter: TransactionListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_transaction)

        setSupportActionBar(toolbar)

        viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(TransactionViewModel::class.java)

        itemListAdapter =
            TransactionListAdapter()

        val mLayoutManager = LinearLayoutManager(this)
        rv_list.layoutManager = mLayoutManager
        rv_list.itemAnimator = DefaultItemAnimator()
        rv_list.adapter = itemListAdapter

        viewModel.getData()

        viewModel.formattedList.observe(this, Observer { transaction ->


            progress_bar.visibility = View.GONE

            when {
                transaction.status == Status.SUCCESS -> {
                    tv_balance.text = viewModel.getBalance()
                    itemListAdapter.setAdapterList(transaction!!.data)
                    progress_bar.visibility = View.GONE
                    tv_lbl_error.visibility = View.GONE
                }
                transaction.status == Status.LOADING -> {
                    progress_bar.visibility = View.VISIBLE
                    tv_lbl_error.visibility = View.GONE
                }
                else -> {
                    progress_bar.visibility = View.GONE
                    tv_lbl_error.visibility = View.VISIBLE
                }
            }

        })

    }


    override fun onDestroy() {
        super.onDestroy()
        viewModel.compositeDisposable.clear()
    }
}
