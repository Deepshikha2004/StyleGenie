package com.example.stylegenie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.*

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var databaseRef: DatabaseReference
    private lateinit var progressBar: ProgressBar
    private lateinit var btnAll: Button
    private lateinit var btnMens: Button
    private lateinit var btnWomens: Button
    private lateinit var btnShirt: Button
    private lateinit var btnPant: Button
    private lateinit var btnDress: Button


    private val productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 🔄 Initialize views
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.rvProducts)
        bottomNavigationView = findViewById(R.id.bottomNav)
        btnAll = findViewById(R.id.btnAll)
        btnMens = findViewById(R.id.btnMens)
        btnWomens = findViewById(R.id.btnWomens)
        btnShirt = findViewById(R.id.btnShirt)
        btnPant = findViewById(R.id.btnPant)
        btnDress = findViewById(R.id.btnDress)


        // 🔄 Setup RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
        productAdapter = ProductAdapter(productList)
        recyclerView.adapter = productAdapter

        // 🔗 Firebase reference (Correct regional URL)
        databaseRef = FirebaseDatabase.getInstance(
            "https://stylegenie-9c50a-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).reference // <-- this points to the root where 0, 1, 2... are


        // 🚀 Fetch products
        fetchProductsFromFirebase()

        setupCategoryFilters()


        // 🔧 Setup bottom nav
        setupBottomNav()
    }

    private fun setupCategoryFilters() {
        btnAll.setOnClickListener {
            fetchProductsFromFirebase()  // Load all
        }

        btnMens.setOnClickListener {
            fetchProductsByGender("Men")
        }

        btnWomens.setOnClickListener {
            fetchProductsByGender("Women")
        }

        btnShirt.setOnClickListener {
            fetchProductsByCategory("Shirt")
        }

        btnPant.setOnClickListener {
            fetchProductsByCategory("Pant")
        }

        btnDress.setOnClickListener {
            fetchProductsByCategory("Dress")
        }
    }


    private fun fetchProductsFromFirebase() {
        progressBar.visibility = View.VISIBLE

        databaseRef.orderByKey().limitToFirst(20)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    productList.clear()

                    if (!snapshot.exists()) {
                        Toast.makeText(this@HomeActivity, "No products found", Toast.LENGTH_SHORT).show()
                        progressBar.visibility = View.GONE
                        return
                    }

                    for (productSnapshot in snapshot.children) {
                        Log.d("HomeActivity", "Snapshot Key: ${productSnapshot.key}")
                        Log.d("HomeActivity", "Snapshot Value: ${productSnapshot.value}")

                        val product = productSnapshot.getValue(Product::class.java)
                        product?.let { productList.add(it) }
                    }

                    productAdapter.notifyDataSetChanged()
                    progressBar.visibility = View.GONE

                    Log.d("HomeActivity", "Fetched ${productList.size} products")
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@HomeActivity, "Failed to fetch products", Toast.LENGTH_SHORT).show()
                    Log.e("HomeActivity", "Firebase error: ${error.message}")
                    progressBar.visibility = View.GONE
                }
            })
    }

    private fun fetchProductsByGender(gender: String) {
        progressBar.visibility = View.VISIBLE
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    if (product != null && product.gender.equals(gender, ignoreCase = true)) {
                        productList.add(product)
                    }
                }
                productAdapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun fetchProductsByCategory(category: String) {
        progressBar.visibility = View.VISIBLE
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    if (product != null && product.category.equals(category, ignoreCase = true)) {
                        productList.add(product)
                    }
                }
                productAdapter.notifyDataSetChanged()
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        })
    }


    private fun setupBottomNav() {
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true

                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    true
                }

                R.id.nav_cart -> {
                    val intent = Intent(this, ChatBotActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    }
                    startActivity(intent)
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
                    true
                }

                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }

                else -> false
            }
        }

        bottomNavigationView.selectedItemId = R.id.nav_home
    }
}
