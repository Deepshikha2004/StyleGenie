package com.example.stylegenie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
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
    private val productList = mutableListOf<Product>()

    // ✅ ProgressBar declaration
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // ✅ Initialize ProgressBar
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE

        // ✅ Setup RecyclerView
        recyclerView = findViewById(R.id.rvProducts)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)

        // ✅ Set Adapter
        productAdapter = ProductAdapter(productList)
        recyclerView.adapter = productAdapter

        // ✅ Firebase database reference
        databaseRef = FirebaseDatabase.getInstance().getReference("products")

        // ✅ Fetch data
        fetchProductsFromFirebase()

        // ✅ Setup Bottom Navigation
        setupBottomNav()
    }

    private fun fetchProductsFromFirebase() {
        progressBar.visibility = View.VISIBLE

        databaseRef.orderByKey().limitToFirst(20)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    productList.clear()

                    for (productSnapshot in snapshot.children) {
                        Log.d("HomeActivity", "Raw Firebase: ${productSnapshot.value}")

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

    private fun setupBottomNav() {
        bottomNavigationView = findViewById(R.id.bottomNav)

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
