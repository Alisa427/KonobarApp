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
        var usluzenoSank = false
        var usluzenoKuhinja = false

        val currentOrder = orderItemsList[position]
        val currentClickedItem = OrderItem("0",0,false) //Nije kliknuto na item
        holder.orderNumber.text = currentOrder.brStola.toString()

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
            holder.txtOrderText.text = "\nNarudžba za ponijeti:"
        }
        if(usluzenoSank && usluzenoKuhinja) {
            holder.btnAcceptAll.performClick()
        }

        holder.orderItemsDrink.adapter = OrderItemsAdapter(currentOrder.sankOrders, activated, activated2) { itemClicked ->
            onItemClicked(currentOrder,itemClicked)
            if(!itemClicked.status){
                usluzenoSank = false
            }
            }
        holder.orderItemsFoodReady.adapter = OrderItemsAdapter(currentOrder.readyMeals, activated, activated2) { itemClicked ->
            onItemClicked(currentOrder,itemClicked)
            if(!itemClicked.status){
                usluzenoKuhinja = false
            }
        }
        holder.orderItemsFoodMaking.adapter = OrderItemsWithoutButtonsAdapter(currentOrder.inPreparationMeals, activated, activated2) { itemClicked ->
            onItemClicked(currentOrder,itemClicked)
        }

        holder.btnAcceptAll.setOnClickListener {
            currentOrder.sankOrders.forEach{item -> item.status=false} // Kada ode u Table da ne ostanu prethodno checkirana polja
            currentOrder.readyMeals.forEach{item -> item.status=false}
            onItemClicked(currentOrder,currentClickedItem)
        }

        holder.btnAcceptfromSank.setOnClickListener {
            usluzenoSank = true
            var adapterReady = OrderItemsAdapter(currentOrder.sankOrders,activated, true) { itemClicked ->
                run {
                    holder.orderItemsDrink.post(Runnable {
                        holder.orderItemsDrink.adapter?.notifyDataSetChanged()
                    })
                }
            }
            holder.orderItemsDrink.adapter = adapterReady
        }

        holder.btnAcceptAllFood.setOnClickListener {
            usluzenoKuhinja = true
            var adapterReady = OrderItemsAdapter(currentOrder.readyMeals,activated, true) { itemClicked ->
                run {
                    holder.orderItemsFoodReady.post(Runnable {
                        holder.orderItemsDrink.adapter?.notifyDataSetChanged()
                    })
                }
            }
            holder.orderItemsFoodReady.adapter = adapterReady
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

        val btnAcceptAll: Button = itemView.findViewById(R.id.btnX)
        val imgTakeAway: ImageView = itemView.findViewById(R.id.imgTakeAway)
        val btnAcceptfromSank: Button = itemView.findViewById(R.id.btnAcceptfromSank)

        val txtJelaUPripremi : TextView = itemView.findViewById(R.id.txtJelaUPripremi)
        val btnAcceptAllFood : Button = itemView.findViewById(R.id.btnAcceptAllFood)

    }

}