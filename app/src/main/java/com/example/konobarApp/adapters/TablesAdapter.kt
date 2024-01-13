package com.example.konobarApp.adapters

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.konobarApp.R
import com.example.konobarApp.models.Order
import com.example.konobarApp.models.OrderItem

class TablesAdapter(private val tablesOrders: ArrayList<Order>,
                    private val brojStolova:Int, //n=0, acc=1, ready=2
                    private val onTableButtonClicked: ((order: Order)->Unit)): RecyclerView.Adapter<TablesAdapter.TablesAdapterViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TablesAdapterViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_table,parent, false )
        return TablesAdapterViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return tablesOrders.size
    }
    interface PopupCallback {
        fun onDismiss(isTableActivated: Boolean)
    }
    override fun onBindViewHolder(holder: TablesAdapterViewHolder, position: Int) {
        val currentOrder = tablesOrders[position]
        val emptyOrder = ArrayList<Order>()
        holder.btnTable.text = currentOrder.brStola.toString()
        holder.btnTable.isActivated = !currentOrder.sankOrders.isEmpty() || !currentOrder.readyMeals.isEmpty()
        holder.btnTable.setOnClickListener {
           // if(areArraysEmpty(currentOrder)){
            showPopup(holder.btnTable.context, holder.btnTable, currentOrder.brStola.toString(), currentOrder, object : PopupCallback {
                override fun onDismiss(isTableActivated: Boolean) {
                        holder.btnTable.isActivated = isTableActivated

                }
            })
        }
        onTableButtonClicked(Order(0,0,false,ArrayList<OrderItem>(),ArrayList<OrderItem>(),ArrayList<OrderItem>()))
    }

    class TablesAdapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val btnTable: Button = itemView.findViewById(R.id.btnTable)

    }

    private fun showPopup(context: Context, anchorView: View, number: String, order: Order,callback: PopupCallback) {
        val inflater = LayoutInflater.from(context)
        val popupView: View = inflater.inflate(R.layout.table_order_popup, null)

        // Popup Elements
        val btnX: Button = popupView.findViewById(R.id.btnX)
        val btnNaplati: Button = popupView.findViewById(R.id.btnNaplati)

        var recyclerViewOrder: RecyclerView = popupView.findViewById(R.id.recyclerViewOrder)
        var orderOfThisTable = ArrayList<OrderItem>()
        orderOfThisTable.addAll(order.sankOrders)
        orderOfThisTable.addAll(order.readyMeals)
        var ukupnaCijena = 0.0
        orderOfThisTable.forEach { ukupnaCijena = ukupnaCijena + it.price.toDouble() }
        var adapterPopup = OrderItemsAdapter(orderOfThisTable, 4, false) { itemClicked ->
            run {
                recyclerViewOrder.post(Runnable {
                    recyclerViewOrder.adapter?.notifyDataSetChanged()  //Kliknut item
                })
            }
        }
        recyclerViewOrder.adapter = adapterPopup
        recyclerViewOrder.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewOrder.setHasFixedSize(true)

        var txtOrderNumber: TextView = popupView.findViewById(R.id.txtOrderNumber)
        txtOrderNumber.text = number

        var txtUkupnaCijena: TextView = popupView.findViewById(R.id.txtUkupnaCijena)
        txtUkupnaCijena.text = "Za naplatiti: $ukupnaCijena KM"

        val alertDialogBuilder = AlertDialog.Builder(context)
        alertDialogBuilder.setView(popupView)

        val alertDialog = alertDialogBuilder.create()

        alertDialog.show()

        btnX.setOnClickListener {
            alertDialog.dismiss()
        }

        btnNaplati.setOnClickListener {
            if(!order.sankOrders.isEmpty() || !order.readyMeals.isEmpty())
                onTableButtonClicked(order)
            alertDialog.dismiss()
            callback.onDismiss(false)
        }

    }
                    }