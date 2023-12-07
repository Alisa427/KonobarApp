package com.example.konobarApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.konobarApp.adapters.OrdersAdapter
import com.example.konobarApp.adapters.TablesAdapter
import com.example.konobarApp.models.Order
import com.example.konobarApp.models.OrderItem

class MainActivity : AppCompatActivity() {

    var nurl: String? = null
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var recycleViewTables: RecyclerView
    var currentOrderList: ArrayList<Order> = ArrayList()
    var ordersToBePayed: ArrayList<Order> = ArrayList()
    var readyOrderList: ArrayList<Order> = ArrayList()

    fun createLists(number: Int){
        var orderItems = arrayListOf<OrderItem>(OrderItem("Margarita", 0, true),
            OrderItem("Pileći sendvič", 0, true),
            OrderItem("Begova corba", 1, true),
            OrderItem("Pahuljice", 0, true),
            OrderItem("Pileća salata", 2, true),
            OrderItem("Tartufi", 10, true))
        this.currentOrderList = arrayListOf(Order(1, false, orderItems),
            Order(2, false, orderItems),
            Order(3, true, orderItems),
            Order(4, true, orderItems),
            Order(5, false, orderItems),
            Order(6, false, orderItems))
        this.ordersToBePayed = ArrayList()
        for(i in 1..number){
            ordersToBePayed.add(Order(i, false, orderItems))
        }
    }
    fun fixCookVisibility(checkList: ArrayList<Order>){
        val imgCook = findViewById<ImageView>(R.id.imgCook)
        val txtNoCards = findViewById<TextView>(R.id.txtNoCards)
        if (checkList.isEmpty()) {
            imgCook.visibility = View.VISIBLE
            txtNoCards.visibility = View.VISIBLE
        } else {
            imgCook.visibility = View.INVISIBLE
            txtNoCards.visibility = View.INVISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.kitchen_activity)
        var brStolova = 15
        createLists(brStolova)
        for(i in 0..5){
            Log.i("\nMyApp", currentOrderList[0].orderItems[i].orderName)
        }
        //  Buttons Orders, Accepted and Ready :
        val btnAccept = findViewById<Button>(R.id.btnAccept)
        val btnOrders = findViewById<Button>(R.id.btnOrders)
        val btnReady = findViewById<Button>(R.id.btnReady)

        btnOrders.isActivated = true
        btnAccept.isActivated = false
        var imgBtnAccount = findViewById<ImageButton>(R.id.imgBtnAccount)
        imgBtnAccount.setOnClickListener {view ->
            val popupMenu = PopupMenu(this, view)
            popupMenu.menuInflater.inflate(R.menu.account_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.lozinkaID -> {
                        // Ovdje obradite klik na prvu stavku menija
                        true
                    }
                    R.id.stoloviID -> {
                        // Kreirajte AlertDialog za unos broja
                        val builder = AlertDialog.Builder(this)
                        builder.setTitle("Unesite broj")

                        // Postavite EditText pogled za unos broja
                        val input = EditText(this)
                        input.inputType = InputType.TYPE_CLASS_NUMBER
                        builder.setView(input)

                        builder.setPositiveButton("OK") { dialog, _ ->
                            val number = input.text.toString().toIntOrNull()
                            if (number != null) {
                                brStolova = number
                                createLists(number)
                                btnOrders.performClick()
                                getUserData()
                            } else {
                                Toast.makeText(this, "Pogrešan unos! Molimo unesite ispravan broj stolova.", Toast.LENGTH_SHORT).show()                            }
                            dialog.dismiss()
                        }
                        builder.setNegativeButton("Odustani") { dialog, _ -> dialog.cancel() }
                        builder.show()
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }


        var txtAcceptedCards = findViewById<TextView>(R.id.txtAcceptedCards)
        var txtNumberOfOrderCards = findViewById<TextView>(R.id.txtNumberOfOrderCards)
        txtAcceptedCards.text = brStolova.toString()
        txtNumberOfOrderCards.text = currentOrderList.size.toString()



        btnOrders.setOnClickListener {
            btnOrders.isActivated = true
            btnAccept.isActivated = false
            btnReady.isActivated = false
            var adapterOrdr = OrdersAdapter(this.currentOrderList,0) { order, item ->
                run {
                    ordersRecyclerView.post(Runnable {
                        ordersRecyclerView.adapter?.notifyDataSetChanged()
                    })
                }
            }
            fixCookVisibility(currentOrderList)
            ordersRecyclerView.adapter = adapterOrdr
            txtAcceptedCards.text = brStolova.toString()

            //getUserData()
            ordersRecyclerView.visibility = View.VISIBLE
            recycleViewTables.visibility = View.INVISIBLE
        }

        btnAccept.setOnClickListener {
            btnAccept.isActivated = true
            btnOrders.isActivated = false
            btnReady.isActivated = false
            var adapterAcc = TablesAdapter(this.ordersToBePayed, 1) { order ->
                run {
                    recycleViewTables.post(Runnable {
                        recycleViewTables.adapter?.notifyDataSetChanged()
                    })
                }
            }
            fixCookVisibility(ordersToBePayed)
            recycleViewTables.adapter = adapterAcc
            ordersRecyclerView.visibility = View.INVISIBLE
            recycleViewTables.visibility = View.VISIBLE
            //getUserDataAcc()
        }
        btnReady.setOnClickListener {
            btnOrders.isActivated = false
            btnAccept.isActivated = false
            btnReady.isActivated = true
            var adapterReady = OrdersAdapter(this.readyOrderList,2) { order, item ->
                run {
                    ordersRecyclerView.post(Runnable {
                        ordersRecyclerView.adapter?.notifyDataSetChanged()
                    })
                }
            }
            fixCookVisibility(readyOrderList)
            ordersRecyclerView.adapter = adapterReady

            ordersRecyclerView.visibility = View.INVISIBLE
            recycleViewTables.visibility = View.INVISIBLE
        }

        ordersRecyclerView = findViewById(R.id.orderItemsRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        ordersRecyclerView.setHasFixedSize(true)
        recycleViewTables = findViewById(R.id.recycleViewTables)
        recycleViewTables.layoutManager = GridLayoutManager(this, 5)
        recycleViewTables.setHasFixedSize(true)
        getUserData()
    }


    //Funkcije za izmjenu nizova:
    private fun getUserData() { //Pokreće se odlaskom na window Narudžbe
        var txtAcceptedCards = findViewById<TextView>(R.id.txtAcceptedCards)
        var txtNumberOfOrderCards = findViewById<TextView>(R.id.txtNumberOfOrderCards)

        var adapter = OrdersAdapter(this.currentOrderList, 0) { order, item ->
            /*run {
                if ( item.orderName=="0") {//!accepted &&
                    if(ordersToBePayed.indexOfFirst { it.id == order.id }!=-1){
                        ordersToBePayed.find { it.id == order.id }?.orderItems?.addAll(order.orderItems)
                    }
                    else{ordersToBePayed.add(order)}
                    this.currentOrderList.remove(order)

                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                    txtAcceptedCards.text = ordersToBePayed.size.toString()
                    txtNumberOfOrderCards.text = currentOrderList.size.toString()
                    fixCookVisibility(currentOrderList)
                }
                else {
                    orderItemInteraction(currentOrderList, ordersToBePayed,order, item)
                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                }
            }*/
            run {
                ordersRecyclerView.post(Runnable {
                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                })
            }
        }
        ordersRecyclerView.adapter = adapter
    }

    /*private fun getUserDataAcc() { //Pokreće se odlaskom na window Prihvaćeno
        var txtAcceptedCards = findViewById<TextView>(R.id.txtAcceptedCards)
        var txtReadyCards = findViewById<TextView>(R.id.txtReadyCards)
        var adapter = TablesAdapter(this.ordersToBePayed,1) { order ->
            run {
                if (item.orderName=="0") {
                    this.ordersToBePayed.remove(order)
                    if(readyOrderList.indexOfFirst { it.id == order.id }!=-1){
                        readyOrderList.find { it.id == order.id }?.orderItems?.addAll(order.orderItems)
                    }
                    else{
                        this.readyOrderList.add(order)
                    }
                    recycleViewTables.adapter?.notifyDataSetChanged()
                    txtAcceptedCards.text = ordersToBePayed.size.toString()
                    txtReadyCards.text = readyOrderList.size.toString()
                    fixCookVisibility(ordersToBePayed)
                }
                else {
                    //acceptedOrderList.find { it.id == order.id }?.orderItems?.remove(item)
                    orderItemInteraction(ordersToBePayed, readyOrderList,order, item)
                    recycleViewTables.adapter?.notifyDataSetChanged()
                }
            }
        }
        recycleViewTables.adapter = adapter
        ordersRecyclerView.visibility = View.GONE
        recycleViewTables.visibility = View.VISIBLE
    }*/


    //Funkcija koja briše narudžbe(item-e) u karticama prvog niza
    //i prebacuje ih u drugi niz:
    private fun orderItemInteraction(orderedArray: ArrayList<Order>, acceptedArray: ArrayList<Order>,
                                     order: Order, item: OrderItem){

        var txtAcceptedCards = findViewById<TextView>(R.id.txtAcceptedCards)
        var txtNumberOfOrderCards = findViewById<TextView>(R.id.txtNumberOfOrderCards)
        var txtReadyCards = findViewById<TextView>(R.id.txtReadyCards)

        var orderItemListTemp = ArrayList<OrderItem>(order.orderItems.toList())
        var orderTemp = Order(order.id, order.status, orderItemListTemp)

        var arrayTemp : ArrayList<OrderItem> = ArrayList()
        arrayTemp.add(item)
        var orderTempAcc = Order(order.id, order.status, arrayTemp)

        if(acceptedArray.indexOfFirst { it.id == order.id }!=-1){
            acceptedArray.find { it.id == order.id }?.orderItems?.add(item)
            orderTemp.orderItems.remove(item)
            orderedArray.find { it.id == order.id }?.orderItems=orderTemp.orderItems
        }
        else{
            orderTemp.orderItems.remove(item)
            orderedArray.find { it.id == order.id }?.orderItems=orderTemp.orderItems
            acceptedArray.add(orderTempAcc)
        }
        txtAcceptedCards.text = ordersToBePayed.size.toString()
        txtNumberOfOrderCards.text = currentOrderList.size.toString()
        txtReadyCards.text = readyOrderList.size.toString()
    }

}


