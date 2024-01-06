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
import com.example.konobarApp.adapters.OrdersDayActivityAdapter
import com.example.konobarApp.adapters.TablesAdapter
import com.example.konobarApp.models.Order
import com.example.konobarApp.models.OrderItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

//import serverStuff.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    var nurl: String? = null
    private lateinit var ordersRecyclerView: RecyclerView
    private lateinit var recycleViewTables: RecyclerView
    
    var currentOrderList: ArrayList<Order> = ArrayList()
    var tablesOrders: ArrayList<Order> = ArrayList()
    var dayActivity: ArrayList<Order> = ArrayList()

    var sankOrders: ArrayList<OrderItem> = ArrayList()
    var readyMeals: ArrayList<OrderItem> = ArrayList()
    var inPreparationMeals: ArrayList<OrderItem> = ArrayList()

    fun createLists(number: Int){
        this.sankOrders = arrayListOf<OrderItem>(OrderItem("Sky cola", 0, false),
            OrderItem("Ness vanilija", 1, false),
            OrderItem("Espresso", 0, false),
            OrderItem("Nutela torta", 2, false),
            OrderItem("Mineralna voda", 10, false))
        var  sankOrders2 = arrayListOf<OrderItem>(OrderItem("Sky cola", 0, false),
            OrderItem("Ness vanilija", 1, false),
            OrderItem("Espresso", 0, false),
            OrderItem("Nutela torta", 2, false),
            OrderItem("Mineralna voda", 10, false))
        this.readyMeals = arrayListOf<OrderItem>(OrderItem("Margarita", 0, false),
            OrderItem("Pileći sendvič", 0, false),
            OrderItem("Begova corba", 1, false),
            OrderItem("Pahuljice", 0, false))
        var readyMeals2 = arrayListOf<OrderItem>(OrderItem("Margarita", 0, false),
            OrderItem("Pileći sendvič", 0, false),
            OrderItem("Begova corba", 1, false),
            OrderItem("Trotilja", 0, false))
        this.inPreparationMeals = arrayListOf<OrderItem>(OrderItem("Mexikana", 0, false),
            OrderItem("Tuna pizza", 0, false),
            OrderItem("Pileća salata", 2, false),
            OrderItem("Tartufi", 10, false))
        var inPreparationMeals2 = arrayListOf<OrderItem>(OrderItem("Mexikana", 0, false),
            OrderItem("Tuna pizza", 0, false),
            OrderItem("Pileća salata", 2, false))

        this.currentOrderList = arrayListOf(Order(1, false, sankOrders, readyMeals,inPreparationMeals),
            Order(2, false, sankOrders2, readyMeals2, inPreparationMeals2),
            Order(3, true, sankOrders, readyMeals, inPreparationMeals),
            Order(4, true, sankOrders, readyMeals, inPreparationMeals),
            Order(5, false, sankOrders, readyMeals, inPreparationMeals),
            Order(6, false, sankOrders, readyMeals, inPreparationMeals))
        
        for(i in 1..number){
            tablesOrders.add(Order(i, false, ArrayList(), ArrayList(),ArrayList()))
        }
    }
    fun fixCookVisibility(checkList: ArrayList<Order>){

        var txtAcceptedCards = findViewById<TextView>(R.id.txtAcceptedCards)
        var txtNumberOfOrderCards = findViewById<TextView>(R.id.txtNumberOfOrderCards)
        var txtReadyCards = findViewById<TextView>(R.id.txtReadyCards)

        val imgCook = findViewById<ImageView>(R.id.imgCook)
        val txtNoCards = findViewById<TextView>(R.id.txtNoCards)
        if (checkList.isEmpty()) {
            imgCook.visibility = View.VISIBLE
            txtNoCards.visibility = View.VISIBLE
        } else {
            imgCook.visibility = View.INVISIBLE
            txtNoCards.visibility = View.INVISIBLE
        }
        txtAcceptedCards.text = tablesOrders.size.toString()
        txtNumberOfOrderCards.text = currentOrderList.size.toString()
        txtReadyCards.text = dayActivity.size.toString()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
      /*  val viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.getPost()
        viewModel.myResponse.observe(this, Observer {
            Log.d(TAG, it.body)
            Log.d(TAG, it.title)
            Log.d(TAG, it.id.toString())
            Log.d(TAG, it.userId.toString())
        })*/

        //Inicijalizacija Firestore database
        val baza = Firebase.firestore
        baza.collection("komentari")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    Log.d("provjera", "Greška prilikom osluškivanja promjena", error)
                    return@addSnapshotListener
                }

                if (value != null) {
                    for (document in value.documents) {
                        Log.d("provjera", "${document.id} => ${document.data}")
                    }
                } else {
                    Log.d("provjera", "Nema podataka u kolekciji")
                }
            }

        var txtCook = findViewById<TextView>(R.id.txtCook)
        var brStolova = 15
        super.onCreate(savedInstanceState)
        setContentView(R.layout.konobar_activity)
        createLists(brStolova)

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

                            if(!tablesOrders.all { it.sankOrders.isEmpty() && it.readyMeals.isEmpty() }){
                            Toast.makeText(this, "Nisu sve narudžbe naplaćene!", Toast.LENGTH_SHORT).show()
                            }
                            else if (number != null ) {
                                //PROVJERITI DA LI JE OVAJ PRISTUP OKEJ sve se narudžbe brišu pri promjeni broja stolova
                               tablesOrders.clear()
                                for(i in 1..number){
                                    tablesOrders.add(Order(i, false, ArrayList(), ArrayList(),ArrayList()))
                                }
                                getUserDataAcc()

                                brStolova = number
                                //createLists(number)
                            }
                            else {
                                Toast.makeText(this, "Pogrešan unos! Molimo unesite ispravan broj stolova.", Toast.LENGTH_SHORT).show()
                            }
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

        fixCookVisibility(currentOrderList)

        btnOrders.setOnClickListener {
            btnOrders.isActivated = true
            btnAccept.isActivated = false
            btnReady.isActivated = false

            getUserData()
            ordersRecyclerView.visibility = View.VISIBLE
            recycleViewTables.visibility = View.INVISIBLE
        }

        btnAccept.setOnClickListener {
            btnAccept.isActivated = true
            btnOrders.isActivated = false
            btnReady.isActivated = false
            getUserDataAcc()
            ordersRecyclerView.visibility = View.INVISIBLE
            recycleViewTables.visibility = View.VISIBLE
        }

        btnReady.setOnClickListener {
            btnOrders.isActivated = false
            btnAccept.isActivated = false
            btnReady.isActivated = true
            var adapterReady = OrdersDayActivityAdapter(this.dayActivity,2) { order, item ->
                run {
                    ordersRecyclerView.post(Runnable {
                        ordersRecyclerView.adapter?.notifyDataSetChanged()
                    })
                }
            }
            fixCookVisibility(dayActivity)
            ordersRecyclerView.adapter = adapterReady
            ordersRecyclerView.visibility = View.VISIBLE
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
                if ( item.orderName=="0") {
                    currentOrderList.remove(order)
                    if(order.status){
                        dayActivity.add(order)
                    }
                   else if(tablesOrders.indexOfFirst { it.id == order.id }!=-1){
                        tablesOrders.find { it.id == order.id }?.readyMeals?.addAll(order.readyMeals)
                        tablesOrders.find { it.id == order.id }?.sankOrders?.addAll(order.sankOrders)
                        tablesOrders.find { it.id == order.id }?.inPreparationMeals?.addAll(order.inPreparationMeals)
                    }
                    else{ tablesOrders.add(order)}

                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                }
                fixCookVisibility(currentOrderList)

            }
        }
        fixCookVisibility(currentOrderList)
        ordersRecyclerView.adapter = adapter
    }

    private fun getUserDataAcc() { //Pokreće se odlaskom na window Prihvaćeno
        var adapter = TablesAdapter(this.tablesOrders, 1) { order ->
           /* run {
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
        }*/
            run {
                if(order.id != 0){
                    var order2 = order.copy()
                    order2.sankOrders = order.sankOrders.clone() as ArrayList<OrderItem>
                    order2.readyMeals = order.readyMeals.clone() as ArrayList<OrderItem>
                    order2.inPreparationMeals = order.inPreparationMeals.clone() as ArrayList<OrderItem>

                    dayActivity.add(order2)
                    tablesOrders.find { it.id == order.id }?.readyMeals?.clear()
                    tablesOrders.find { it.id == order.id }?.sankOrders?.clear()
                    tablesOrders.find { it.id == order.id }?.inPreparationMeals?.clear()
                    fixCookVisibility(tablesOrders)

                }
                ordersRecyclerView.adapter?.notifyDataSetChanged()
            }
        }
        fixCookVisibility(tablesOrders)
        recycleViewTables.adapter = adapter
        ordersRecyclerView.visibility = View.GONE
        recycleViewTables.visibility = View.VISIBLE
    }

    //Funkcija koja briše narudžbe(item-e) u karticama prvog niza
    //i prebacuje ih u drugi niz:
    /*private fun orderItemInteraction(orderedArray: ArrayList<Order>, acceptedArray: ArrayList<Order>,
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
    }*/

}


