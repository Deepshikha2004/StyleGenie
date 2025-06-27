package com.example.stylegenie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        recyclerView = findViewById(R.id.rvProducts)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)

        val products = listOf(
            Product(R.drawable.fashion_image2, "Floral Dress", "₹999"),
            Product(R.drawable.ic_launcher_background, "Summer Skirt", "₹599"),
            Product(R.drawable.fashion_image, "Cotton Kurta", "₹749"),
            Product(R.drawable.fashion_image2, "Blue Shirt", "₹899"),
            Product(R.drawable.ic_launcher_background, "Kids Set", "₹499")
        )

        productAdapter = ProductAdapter(products)
        recyclerView.adapter = productAdapter
        Log.d("HomeActivity", "Adapter set with ${products.size} products")

        // ✅ Bottom Navigation setup
        bottomNavigationView = findViewById(R.id.bottomNav)

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    true
                }
                R.id.nav_favorites -> {
                    startActivity(Intent(this, FavoritesActivity::class.java))
                    true
                }
                R.id.nav_cart -> {
                    val intent = Intent(this, ChatBotActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
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


        // Optionally set the selected item
        bottomNavigationView.selectedItemId = R.id.nav_home
    }
}
