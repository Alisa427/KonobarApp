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
    var accepted = false
    var removed: Boolean = false

    fun createLists(){
        var orderItems = arrayListOf<OrderItem>(OrderItem("Margarita", 0, true),
            OrderItem("Pileći sendvič", 0, true),
            OrderItem("Begova corba", 1, true),
            OrderItem("Pahuljice", 0, true),
            OrderItem("Pileća salata", 2, true),
            OrderItem("Tartufi", 10, true))
        this.currentOrderList = arrayListOf(Order(1, true, orderItems),
            Order(2, true, orderItems),
            Order(3, true, orderItems),
            Order(4, true, orderItems),
            Order(5, true, orderItems),
            Order(6, true, orderItems))
    }
    fun fixCookVisibility(){
        val imgCook = findViewById<ImageView>(R.id.imgCook)
        val txtNoCards = findViewById<TextView>(R.id.txtNoCards)
        if(accepted){ //Accepted
            if (acceptedOrderList.isEmpty()) {
                imgCook.visibility = View.VISIBLE
                txtNoCards.visibility = View.VISIBLE
            } else {
                imgCook.visibility = View.INVISIBLE
                txtNoCards.visibility = View.INVISIBLE
            }
        }
        else{ //Orders
            if (currentOrderList.isEmpty()) {
                imgCook.visibility = View.VISIBLE
                txtNoCards.visibility = View.VISIBLE
            } else{
                imgCook.visibility = View.INVISIBLE
                txtNoCards.visibility = View.INVISIBLE
            }
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

        //  Buttons Orders and Accepted :
        val btnAccept = findViewById<Button>(R.id.btnAccept)
        val btnOrders = findViewById<Button>(R.id.btnOrders)
        val textView = findViewById<TextView>(R.id.textView)

        btnOrders.isActivated = true
        btnAccept.isActivated = false

        btnAccept.setOnClickListener {
            accepted = true
            btnAccept.isActivated = accepted
            btnOrders.isActivated = false
            var adapterAcc = OrdersAdapter(this.acceptedOrderList, false) { order, item ->
                run {
                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
            fixCookVisibility()
            ordersRecyclerView.adapter = adapterAcc
            textView.text = "PRIHVAĆENO"
            getUserDataAcc()
        }

        btnOrders.setOnClickListener {
            accepted = false
            btnOrders.isActivated = true
            btnAccept.isActivated = false
            var adapterOrdr = OrdersAdapter(this.currentOrderList,true) { order, item ->
                run {
                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
            fixCookVisibility()
            ordersRecyclerView.adapter = adapterOrdr
            textView.text = "NARUDŽBA"
            getUserData()
        }

        ordersRecyclerView = findViewById(R.id.orderItemsRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        ordersRecyclerView.setHasFixedSize(true)

        getUserData()
    }

    private fun getUserData() {
        var txtAcceptedCards = findViewById<TextView>(R.id.txtAcceptedCards)
        var txtNumberOfOrderCards = findViewById<TextView>(R.id.txtNumberOfOrderCards)
        var adapter = OrdersAdapter(this.currentOrderList, false) { order, item ->
            run {
                if (!accepted && item.orderName=="0") {
                    this.currentOrderList.remove(order)
                    this.acceptedOrderList.add(order)
                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                    txtAcceptedCards.text = acceptedOrderList.size.toString()
                    txtNumberOfOrderCards.text = currentOrderList.size.toString()
                    fixCookVisibility()
                }
                else {
                    orderItemInteraction(order, item)
                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                }
            }
        }
        ordersRecyclerView.adapter = adapter
    }

    private fun getUserDataAcc() {
        var txtAcceptedCards = findViewById<TextView>(R.id.txtAcceptedCards)
        var adapter = OrdersAdapter(this.acceptedOrderList,true) { order, item ->
            run {
                if (accepted && item.orderName=="0") {
                    this.acceptedOrderList.remove(order)
                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                    txtAcceptedCards.text = acceptedOrderList.size.toString()
                    fixCookVisibility()
                }
                else orderItemInteraction(order, item)
            }
        }
        ordersRecyclerView.adapter = adapter
    }
    private fun orderItemInteraction(order: Order, item: OrderItem){
        val textView = findViewById<TextView>(R.id.textView)
        val textView2 = findViewById<TextView>(R.id.textView2)

        var orderTemp = order
        if(order in acceptedOrderList){

        }
        else{

            var index = currentOrderList.indexOf(order)
            var indexItema = currentOrderList[index].orderItems.indexOf(item)
            textView.text = currentOrderList[0].orderItems[0].orderName
            textView2.text = currentOrderList[1].orderItems[0].orderName

            orderTemp.orderItems.remove(item)

            //var index = order.id
            //textView.text = index.toString()
            //currentOrderList.find { it.id == order.id }?.orderItems=orderTemp.orderItems
            val condition: (OrderItem) -> Boolean = { orderItem -> orderItem.orderName==item.orderName }
            currentOrderList[index].orderItems.removeIf(condition)
          // currentOrderList.filter { it.id == order.id }.forEach{it.orderItems = orderTemp.orderItems}

        }
    }

}


