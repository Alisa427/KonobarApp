package com.example.konobarApp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.konobarApp.R
import com.example.konobarApp.models.Order
import com.example.konobarApp.models.OrderItem

class OrdersAdapter(private val orderItemsList: ArrayList<Order>,
                    private val activated:Int, //n=0, acc=1, ready=2 bilo prije sad bi to trebala biti info za takeaway
                    private val onItemClicked: ((order:Order, item:OrderItem )->Unit)): RecyclerView.Adapter<OrdersAdapter.OrdersViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.order_for_waiter, parent, false )
        return OrdersViewHolder(itemView)//, mListener
    }

    override fun onBindViewHolder(holder: OrdersAdapter.OrdersViewHolder, position: Int) {
        var activated2 = false
        val currentOrder = orderItemsList[position]
        val currentClickedItem = OrderItem("0",0,false) //Nije kliknuto na item
        holder.orderNumber.text = currentOrder.id.toString()

        holder.orderItemsDrink.layoutManager = LinearLayoutManager(holder.orderNumber.context)
        holder.orderItemsDrink.setHasFixedSize(true)

        holder.orderItemsFoodReady.layoutManager = LinearLayoutManager(holder.orderNumber.context)
        holder.orderItemsFoodReady.setHasFixedSize(true)
        holder.orderItemsFoodMaking.layoutManager = LinearLayoutManager(holder.orderNumber.context)
        holder.orderItemsFoodMaking.setHasFixedSize(true)

        holder.btnAcceptAll.visibility = View.VISIBLE
        holder.imgTakeAway.visibility = View.INVISIBLE
        if(currentOrder.status){
            holder.imgTakeAway.visibility = View.VISIBLE
        }
        if(activated == 1) {
            holder.btnAcceptAll.isActivated = true
            holder.btnAcceptAll.text = "SPREMNO SVE"
        }
        else if(activated == 2){
            holder.btnAcceptAll.isActivated = false
            holder.btnAcceptAll.visibility = View.INVISIBLE
            holder.txtOrderText.text = "\n  Narudžba br.\n\nSpremljena jela:"
        }

        holder.orderItemsDrink.adapter = OrderItemsAdapter(currentOrder.orderItems, activated, activated2) { itemClicked ->
            onItemClicked(currentOrder,itemClicked)
            }
        holder.orderItemsFoodReady.adapter = OrderItemsAdapter(currentOrder.orderItems, activated, activated2) { itemClicked ->
            onItemClicked(currentOrder,itemClicked)
        }
        holder.orderItemsFoodMaking.adapter = OrderItemsWithoutButtonsAdapter(currentOrder.orderItems, activated, activated2) { itemClicked ->
            onItemClicked(currentOrder,itemClicked)
        }

        holder.btnAcceptAll.setOnClickListener {
           // onItemClicked(currentOrder,currentClickedItem)
        }
    }

    override fun getItemCount(): Int {
        return orderItemsList.size
    }

    class OrdersViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){ //, listener: onItemClickListener

        val orderNumber : TextView = itemView.findViewById(R.id.txtOrderNumber)
        val txtOrderText : TextView = itemView.findViewById(R.id.txtViewOrderNumber)

        val orderItemsDrink: RecyclerView = itemView.findViewById(R.id.recyclerViewDrinks)
        val orderItemsFoodReady: RecyclerView = itemView.findViewById(R.id.recyclerViewFoodReady)
        val orderItemsFoodMaking: RecyclerView = itemView.findViewById(R.id.recyclerViewFoodMaking)

        val btnAcceptAll: Button = itemView.findViewById(R.id.btnAcceptAll)
        val imgTakeAway: ImageView = itemView.findViewById(R.id.imgTakeAway)

    }

}