package com.example.kitchenApp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kitchenApp.adapters.OrdersAdapter
import com.example.kitchenApp.models.Order
import com.example.kitchenApp.models.OrderItem

class MainActivity : AppCompatActivity() {

    var nurl: String? = null
    private lateinit var ordersRecyclerView: RecyclerView
    var currentOrderList: ArrayList<Order> = ArrayList()
    var acceptedOrderList: ArrayList<Order> = ArrayList()
    var readyOrderList: ArrayList<Order> = ArrayList()

    fun createLists(){
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
        setContentView(R.layout.kitchen_activity) //android.R.
        createLists()
        var txtAcceptedCards = findViewById<TextView>(R.id.txtAcceptedCards)
        var txtNumberOfOrderCards = findViewById<TextView>(R.id.txtNumberOfOrderCards)
        txtAcceptedCards.text = acceptedOrderList.size.toString()
        txtNumberOfOrderCards.text = currentOrderList.size.toString()

        //  Buttons Orders, Accepted and Ready :
        val btnAccept = findViewById<Button>(R.id.btnAccept)
        val btnOrders = findViewById<Button>(R.id.btnOrders)
        val btnReady = findViewById<Button>(R.id.btnReady)

        btnOrders.isActivated = true
        btnAccept.isActivated = false

        btnOrders.setOnClickListener {
            btnOrders.isActivated = true
            btnAccept.isActivated = false
            btnReady.isActivated = false
            var adapterOrdr = OrdersAdapter(this.currentOrderList,0) { order, item ->
                run {
                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
            fixCookVisibility(currentOrderList)
            ordersRecyclerView.adapter = adapterOrdr
            getUserData()
        }

        btnAccept.setOnClickListener {
            btnAccept.isActivated = true
            btnOrders.isActivated = false
            btnReady.isActivated = false
            var adapterAcc = OrdersAdapter(this.acceptedOrderList, 1) { order, item ->
                run {
                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
            fixCookVisibility(acceptedOrderList)
            ordersRecyclerView.adapter = adapterAcc
            getUserDataAcc()
        }
        btnReady.setOnClickListener {
            btnOrders.isActivated = false
            btnAccept.isActivated = false
            btnReady.isActivated = true
            var adapterReady = OrdersAdapter(this.readyOrderList,2) { order, item ->
                run {
                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
            fixCookVisibility(readyOrderList)
            ordersRecyclerView.adapter = adapterReady
        }

        ordersRecyclerView = findViewById(R.id.orderItemsRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        ordersRecyclerView.setHasFixedSize(true)

        getUserData()
    }


    //Funkcije za izmjenu nizova:
    private fun getUserData() { //Pokreće se odlaskom na window Narudžbe
        var txtAcceptedCards = findViewById<TextView>(R.id.txtAcceptedCards)
        var txtNumberOfOrderCards = findViewById<TextView>(R.id.txtNumberOfOrderCards)
        var adapter = OrdersAdapter(this.currentOrderList, 0) { order, item ->
            run {
                if ( item.orderName=="0") {//!accepted &&
                    if(acceptedOrderList.indexOfFirst { it.id == order.id }!=-1){
                        acceptedOrderList.find { it.id == order.id }?.orderItems?.addAll(order.orderItems)
                    }
                    else{acceptedOrderList.add(order)}
                    this.currentOrderList.remove(order)

                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                    txtAcceptedCards.text = acceptedOrderList.size.toString()
                    txtNumberOfOrderCards.text = currentOrderList.size.toString()
                    fixCookVisibility(currentOrderList)
                }
                else {
                    orderItemInteraction(currentOrderList, acceptedOrderList,order, item)
                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
        }
        ordersRecyclerView.adapter = adapter
    }

    private fun getUserDataAcc() { //Pokreće se odlaskom na window Prihvaćeno
        var txtAcceptedCards = findViewById<TextView>(R.id.txtAcceptedCards)
        var txtReadyCards = findViewById<TextView>(R.id.txtReadyCards)
        var adapter = OrdersAdapter(this.acceptedOrderList,1) { order, item ->
            run {
                if (item.orderName=="0") {//accepted &&
                    this.acceptedOrderList.remove(order)
                    if(readyOrderList.indexOfFirst { it.id == order.id }!=-1){
                        readyOrderList.find { it.id == order.id }?.orderItems?.addAll(order.orderItems)
                    }
                    else{
                        this.readyOrderList.add(order)
                    }
                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                    txtAcceptedCards.text = acceptedOrderList.size.toString()
                    txtReadyCards.text = readyOrderList.size.toString()
                    fixCookVisibility(acceptedOrderList)
                }
                else {
                    //acceptedOrderList.find { it.id == order.id }?.orderItems?.remove(item)
                    orderItemInteraction(acceptedOrderList, readyOrderList,order, item)
                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
        }
        ordersRecyclerView.adapter = adapter
    }


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
        txtAcceptedCards.text = acceptedOrderList.size.toString()
        txtNumberOfOrderCards.text = currentOrderList.size.toString()
        txtReadyCards.text = readyOrderList.size.toString()
    }

}


