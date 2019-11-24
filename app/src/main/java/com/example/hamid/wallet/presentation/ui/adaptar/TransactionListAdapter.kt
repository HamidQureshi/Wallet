package com.example.hamid.wallet.presentation.ui.adaptar

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hamid.wallet.R
import com.hamid.domain.model.model.TransactionFormatted
import kotlinx.android.synthetic.main.transaction_item.view.*

class TransactionListAdapter : RecyclerView.Adapter<TransactionListAdapter.ViewHolder>() {

    private var transactionList: List<TransactionFormatted>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.transaction_item, parent, false)

        return ViewHolder(
            view,
            object :
                ViewHolder.IClickListener {

                override fun onClick(caller: View, position: Int) {

                }

            })
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactionList!![position]
        holder.tvFee.text = transaction.fee
        holder.tvAmount.text = transaction.result
        if (transaction.inflow) {
            holder.tvAmount.setTextColor(Color.GREEN)
        } else {
            holder.tvAmount.setTextColor(Color.RED)
        }
        holder.tvTime.text = transaction.time
    }

    override fun getItemCount(): Int {
        return if (transactionList != null)
            transactionList!!.size
        else
            0
    }

    fun setAdapterList(transactionList: List<TransactionFormatted>) {
        this.transactionList = transactionList
        notifyDataSetChanged()
    }

    class ViewHolder(view: View, private val clickListener: IClickListener) :
        RecyclerView.ViewHolder(view),
        View.OnClickListener {

        var tvTime: TextView = view.tv_time
        var tvFee: TextView = view.tv_fee
        var tvAmount: TextView = view.tv_amount


        override fun onClick(view: View) {
            clickListener.onClick(view, layoutPosition)
        }

        interface IClickListener {
            fun onClick(caller: View, position: Int)
        }

    }

}
