package com.example.konobarApp.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.konobarApp.R
import com.example.konobarApp.models.Order
import com.example.konobarApp.models.OrderItem

class TablesAdapter(private val orderItemsList: ArrayList<Order>,
                    private val brojStolova:Int, //n=0, acc=1, ready=2
                    private val onTableButtonClicked: ((order: Order)->Unit)): RecyclerView.Adapter<TablesAdapter.TablesAdapterViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TablesAdapterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_table,parent, false )
        return TablesAdapterViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return orderItemsList.size
    }

    override fun onBindViewHolder(holder: TablesAdapterViewHolder, position: Int) {
        val currentOrder = orderItemsList[position]
        holder.btnTable.text = currentOrder.id.toString()
        onTableButtonClicked(Order(0,false,ArrayList<OrderItem>()))
    }

    class TablesAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val btnTable: Button = itemView.findViewById(R.id.btnTable)

    }
                    }