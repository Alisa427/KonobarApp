package com.example.konobarApp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.konobarApp.adapters.OrdersAdapter
import com.example.konobarApp.adapters.OrdersDayActivityAdapter
import com.example.konobarApp.adapters.TablesAdapter
import com.example.konobarApp.models.Order
import com.example.konobarApp.models.OrderItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.sidesheet.SideSheetDialog
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Arrays.fill
import kotlin.reflect.typeOf

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

    var kuhinja_spremnoZaProslijediti: ArrayList<OrderItem> = ArrayList()
    var kuhinja_pripremaZaProslijediti: ArrayList<OrderItem> = ArrayList()

    var kuhar_poruka: String = "Poruke od kuhara: \n"
    var gost_poziv: String = "\n\nPozivi od gostiju: \n"
    var velGosti_notifikacije: Int = 1

    var brojacID: Int = 0
    fun createLists(number: Int, info: ArrayList<OrderItem>){
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

        /*if(!info.isEmpty()) {
            inPreparationMeals2.add(info[0])
        }*/

        this.currentOrderList = arrayListOf(Order(1, 1, false, sankOrders, readyMeals,inPreparationMeals),
            Order(2, 2, false,  sankOrders2, readyMeals2, inPreparationMeals2),
            Order(3, 0, true,  sankOrders, readyMeals, inPreparationMeals),
            Order(4, 0, true,  sankOrders, readyMeals, inPreparationMeals),
            Order(5, 5, false,  sankOrders, readyMeals, inPreparationMeals),
            Order(6, 6, false,  sankOrders, readyMeals, inPreparationMeals))
        
        for(i in 1..number){
            tablesOrders.add(Order(i, 0, false, ArrayList(), ArrayList(),ArrayList()))
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
    fun obavijestiBazu(order: Order){
        val baza = Firebase.firestore.collection("test_dodavanja_nadza")
        baza.whereEqualTo("brojStola", order.brStola)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val docRef = baza.document(document.id)
                    docRef.update("dostavljeno", true)
                        .addOnSuccessListener { Log.d("provjera", "Dokument uspješno ažuriran!") }
                        .addOnFailureListener { e -> Log.w("provjera", "Greška prilikom ažuriranja dokumenta", e) }
                }
            }
            .addOnFailureListener { exception ->
                Log.w("provjera", "Greška prilikom dohvaćanja dokumenta", exception)
            }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        var txtCook = findViewById<TextView>(R.id.txtCook)
        var brStolova = 15
        super.onCreate(savedInstanceState)
        setContentView(R.layout.konobar_activity)

        //  Buttons Orders, Accepted and Ready :
        val btnAccept = findViewById<Button>(R.id.btnAccept)
        val btnOrders = findViewById<Button>(R.id.btnOrders)
        val btnReady = findViewById<Button>(R.id.btnReady)

        btnOrders.isActivated = true
        btnAccept.isActivated = false

        //************ NOTIFIKACIJE ******************
        var drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout)
        //drawerLayout.closeDrawer(GravityCompat.END)
        drawerLayout.setScrimColor(Color.TRANSPARENT)
        val btnNotifikacije = findViewById<Button>(R.id.btnNotifikacije)
        drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                // Pomjeri dugme u skladu sa offsetom izbornika
                btnNotifikacije.translationX = - drawerView.width * slideOffset + 5
            }
            override fun onDrawerOpened(drawerView: View) {}
            override fun onDrawerClosed(drawerView: View) {}
            override fun onDrawerStateChanged(newState: Int) {}
        })

        btnNotifikacije.setOnClickListener {
            if(drawerLayout!=null) {
                drawerLayout.openDrawer(GravityCompat.END)
            }
            var txtNotifikacije = findViewById<TextView>(R.id.txtNotifikacije)
            txtNotifikacije.movementMethod = ScrollingMovementMethod()
            txtNotifikacije.text = this.kuhar_poruka + this.gost_poziv
            btnNotifikacije.text = "NOTIFIKACIJE"
            val baza = Firebase.firestore.collection("notifikacije")
            val docRef = baza.document("notifikacije")
            docRef.update("kuhar_notifikacija", false)
            val list = ArrayList<Boolean>()
            for(i in 0 until this.velGosti_notifikacije) {
                list.add(false)
            }
            docRef.update("gosti_notifikacije",list)

            btnNotifikacije.backgroundTintList = null
        }



        for(i in 1..15){
            tablesOrders.add(Order(i, i, false, ArrayList(), ArrayList(),ArrayList()))
        }


        //*********** Inicijalizacija Firestore database ************
        val baza = Firebase.firestore
        val notifikacijeKolekcija = baza.collection("notifikacije")
        notifikacijeKolekcija
            .addSnapshotListener { value, error ->
                notifikacijeKolekcija
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            var kuhar_notifikacija = document.get("kuhar_notifikacija") as Boolean
                            var kuhar_poruka = document.get("kuhar_poruka") as String

                            var gosti_notifikacije = document.get("gosti_notifikacije") as ArrayList<Boolean>
                            if(kuhar_notifikacija){
                                btnNotifikacije.text = "Zove KUHAR"
                                this.kuhar_poruka = this.kuhar_poruka + "\n ~ " + kuhar_poruka
                                btnNotifikacije.backgroundTintList = ColorStateList.valueOf(Color.RED)
                            }
                            if(gosti_notifikacije.any{it}){ //Ako je ijedan stol true
                                btnNotifikacije.text = "Zove GOST"
                                this.velGosti_notifikacije = gosti_notifikacije.size
                                gosti_notifikacije.forEachIndexed {index,it -> if(it){this.gost_poziv = this.gost_poziv + "\n ~ " + "Stol broj "+ (index+1).toString()} }
                                btnNotifikacije.backgroundTintList = ColorStateList.valueOf(Color.YELLOW)
                            }
                        }
                        // getUserData()
                    }
            }

        val narudzbeCollection = baza.collection("test_dodavanja_nadza")
        narudzbeCollection
            .addSnapshotListener { value, error ->
                Log.d("shotonja", value.toString())
                narudzbeCollection
                    .get()
                    .addOnSuccessListener { result ->
                        brojacID = 0 //bit ce ponovljenih id-ova u odnosu na one koji su otisli za stolove
                        currentOrderList.clear()
                        for (document in result) { //jelaaa sva moguca sa brojevima stolova
                            val brojStola = document.get("brojStola") as Number//za Order je za sad to id
                            val takeAway = document.get("zaPonijeti") as Boolean
                            val naziv = document.get("naziv") as String
                            val cijena = document.get("cijena") as Number
                            val kategorija = document.get("kategorija") as String
                            val spremljeno = document.get("spremljeno") as Boolean
                            val dostavljeno = document.get("dostavljeno") as Boolean
                            Log.d("hm1", naziv)

                            // dostavljeno viska i placeno

                            val item = OrderItem(naziv, cijena, false )
                            if(!dostavljeno) {
                                if (currentOrderList.indexOfFirst { it.brStola == brojStola } != -1) { //postoji vec taj stol
                                    if (kategorija == "Doručak" || kategorija == "Supe i čorbe") {
                                        if (spremljeno) {
                                            currentOrderList.find { it.brStola == brojStola }?.readyMeals?.add(
                                                item
                                            )
                                        } else {
                                            Log.d("hm2",  currentOrderList.size.toString())
                                            currentOrderList.find { it.brStola == brojStola }?.inPreparationMeals?.add(
                                                item
                                            )
                                        }
                                    } else if (kategorija == "pice") {
                                        currentOrderList.find { it.brStola == brojStola }?.sankOrders?.add(
                                            item
                                        )
                                    }
                                } else {
                                    val listaSank = ArrayList<OrderItem>()
                                    val listaReadyMeals = ArrayList<OrderItem>()
                                    val listaInPreparation = ArrayList<OrderItem>()
                                    if (kategorija == "pice") {
                                        listaSank.add(item)
                                    } else if (kategorija == "Doručak") {
                                        if (spremljeno) {
                                            listaReadyMeals.add(item)
                                        } else {
                                            listaInPreparation.add(item)
                                        }
                                    }
                                    Log.d("hm3", naziv)
                                    if(!listaInPreparation.isEmpty()) {
                                        Log.d("hm4 je li ", listaInPreparation.get(0).orderName)
                                    }
                                    currentOrderList.add(
                                        Order(
                                            brojacID,
                                            brojStola,
                                            takeAway,
                                            listaSank,
                                            listaReadyMeals,
                                            listaInPreparation
                                        )
                                    )
                                    brojacID = brojacID + 1
                                }
                            }
                        }
                        //Osigurati da se mijenja broj narudzbi u tabu i onda kada se ne nalazimo u istom:
                        var txtNumberOfOrderCards = findViewById<TextView>(R.id.txtNumberOfOrderCards)
                        txtNumberOfOrderCards.text = currentOrderList.size.toString()
                        if(btnOrders.isActivated) {
                            getUserData()
                        }
                    }
            }

        //createLists(brStolova,listaJela2)



        var dayActivityInfo = findViewById<TextView>(R.id.txtdayActivityInfo)
         dayActivityInfo.visibility = View.INVISIBLE

        // ********** PROFIL I IZMJENA BROJA STOLOVA **********
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
                                    tablesOrders.add(Order(i, i, false, ArrayList(), ArrayList(),ArrayList()))
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
            dayActivityInfo.visibility = View.INVISIBLE

        }

        btnAccept.setOnClickListener {
            btnAccept.isActivated = true
            btnOrders.isActivated = false
            btnReady.isActivated = false
            getUserDataAcc()
            ordersRecyclerView.visibility = View.INVISIBLE
            recycleViewTables.visibility = View.VISIBLE
            dayActivityInfo.visibility = View.INVISIBLE

        }

        btnReady.setOnClickListener {
            btnOrders.isActivated = false
            btnAccept.isActivated = false
            btnReady.isActivated = true
            var dnevniPazar = 0.0
           dayActivity.forEach {
                it.readyMeals.forEach { dnevniPazar= dnevniPazar + it.price.toDouble() }
                it.sankOrders.forEach { dnevniPazar= dnevniPazar + it.price.toDouble() }
            }
            dayActivityInfo.text = "Dnevni pazar: ${dnevniPazar} KM \nBroj dnevnih narudžbi: ${dayActivity.size.toString()}"
            dayActivityInfo.visibility = View.VISIBLE

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
        var adapter = OrdersAdapter(this, this.currentOrderList, 0) { order, item ->
            run {

                if ( item.orderName=="0") {
                    currentOrderList.remove(order)
                    obavijestiBazu(order)

                    Log.d(
                        "tableStae1",tablesOrders.any { it.brStola == order.brStola }.toString()

                    )
                    for(tbl in tablesOrders) {
                        Log.d(
                            "tableStae2",
                            (tbl.brStola.toString().equals(order.brStola.toString())).toString() + " order.brStola: " + tbl.brStola.toString()
                        )
                    }

                    if(order.status){
                        dayActivity.add(order)
                    }
                   //else if(tablesOrders.indexOfFirst { it.brStola == order.brStola }!=-1){
                    else if (tablesOrders.any { it.brStola.toString().equals(order.brStola.toString()) }) {
                        Log.d("tableStae3", "lodilo")
                       var tbl = tablesOrders.find { it.brStola.toString().equals(order.brStola.toString())}
                        tbl?.readyMeals?.addAll(order.readyMeals)
                        tbl?.sankOrders?.addAll(order.sankOrders)
                        tbl?.inPreparationMeals?.addAll(order.inPreparationMeals)
                    }
                    else{ tablesOrders.add(order)
                        Log.d("tableStae4",  tablesOrders.indexOfFirst { it.brStola == order.brStola }.toString())
                    }

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
            run {
                if(order.brStola != 0){ //proslijedjen je order koji je naplacen i ide u dayActivity
                    var order2 = order.copy()
                    order2.sankOrders = order.sankOrders.clone() as ArrayList<OrderItem>
                    order2.readyMeals = order.readyMeals.clone() as ArrayList<OrderItem>
                    order2.inPreparationMeals = order.inPreparationMeals.clone() as ArrayList<OrderItem>

                    dayActivity.add(order2)
                    tablesOrders.find { it.brStola == order.brStola }?.readyMeals?.clear()
                    tablesOrders.find { it.brStola == order.brStola }?.sankOrders?.clear()
                    tablesOrders.find { it.brStola == order.brStola }?.inPreparationMeals?.clear()
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
}


