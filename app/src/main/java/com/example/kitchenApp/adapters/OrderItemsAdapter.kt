package com.example.kitchenApp.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kitchenApp.R
import com.example.kitchenApp.models.Order
import com.example.kitchenApp.models.OrderItem

class OrderItemsAdapter(private val orderItemsList: ArrayList<OrderItem>, private val onItemButtonClicked: ((itemClicked: OrderItem)->Unit)): RecyclerView.Adapter<OrderItemsAdapter.OrdersItemsViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersItemsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list,parent, false )
        return OrdersItemsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return orderItemsList.size
    }

    override fun onBindViewHolder(holder: OrdersItemsViewHolder, position: Int) {
        val currentItem = orderItemsList[position]
        holder.orderName.text = currentItem.orderName
        holder.btnAcceptOrder.setOnClickListener {
            holder.btnAcceptOrder.text ="prihvaćeno"
            onItemButtonClicked(currentItem)
        }
    }

    class OrdersItemsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val orderName: TextView = itemView.findViewById(R.id.txtVOrder)
        val btnAcceptOrder: Button = itemView.findViewById(R.id.btnAcceptOrder)

    }

}