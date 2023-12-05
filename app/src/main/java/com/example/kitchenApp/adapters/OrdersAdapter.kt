package com.example.kitchenApp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kitchenApp.R
import com.example.kitchenApp.models.Order
import com.example.kitchenApp.models.OrderItem

class OrdersAdapter(private val orderItemsList: ArrayList<Order>,
                    private val activated:Boolean,
                    private val onItemClicked: ((order:Order, item:OrderItem )->Unit)): RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.order, parent, false )
        return OrdersViewHolder(itemView)//, mListener
    }

    override fun onBindViewHolder(holder: OrdersAdapter.OrdersViewHolder, position: Int) {
        val currentOrder = orderItemsList[position]
        val currentClickedItem = OrderItem("0",0,false) //Nije kliknuto na item
        holder.orderNumber.text = currentOrder.id.toString()
        holder.orderItems.layoutManager = LinearLayoutManager(holder.orderNumber.context)
        holder.orderItems.setHasFixedSize(true)
        holder.btnAcceptAll.isActivated = activated
        if(activated)
            holder.btnAcceptAll.text = "SPREMNO SVE"

        holder.orderItems.adapter = OrderItemsAdapter(currentOrder.orderItems) { itemClicked ->
            onItemClicked(currentOrder,itemClicked)
            }

        holder.btnAcceptAll.setOnClickListener {
            onItemClicked(currentOrder,currentClickedItem)
        }
    }

    override fun getItemCount(): Int {
        return orderItemsList.size
    }

    class OrdersViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){ //, listener: onItemClickListener

        val orderNumber : TextView = itemView.findViewById(R.id.txtOrderNumber)
        val orderItems: RecyclerView = itemView.findViewById(R.id.recyclerViewOrder)
        val btnAcceptAll: Button = itemView.findViewById(R.id.btnAcceptAll)

    }

}