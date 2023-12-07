package com.example.konobarApp.adapters
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.konobarApp.R
import com.example.konobarApp.models.OrderItem

class OrderItemsWithoutButtonsAdapter(private val orderItemsList: ArrayList<OrderItem>,
                                      private val activated:Int, //n=0, acc=1, ready=2
                                      private val activated2:Boolean,
                                      private val onItemButtonClicked: ((itemClicked: OrderItem)->Unit)): RecyclerView.Adapter<OrderItemsWithoutButtonsAdapter.OrderItemsWithoutButtonsAdapter>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderItemsWithoutButtonsAdapter {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_without_buttons,parent, false )
        return OrderItemsWithoutButtonsAdapter(itemView)
    }

    override fun getItemCount(): Int {
        return orderItemsList.size
    }

    override fun onBindViewHolder(holder: OrderItemsWithoutButtonsAdapter, position: Int) {
        val currentItem = orderItemsList[position]
        holder.orderName.text = currentItem.orderName

    }

    class OrderItemsWithoutButtonsAdapter(itemView: View): RecyclerView.ViewHolder(itemView){

        val orderName: TextView = itemView.findViewById(R.id.txtVOrder)
    }

}