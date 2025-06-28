package com.example.stylegenie

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class FavoritesActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val favoriteList = mutableListOf<Product>()
    private lateinit var databaseRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        recyclerView = findViewById(R.id.rvFavorites)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        productAdapter = ProductAdapter(favoriteList)
        recyclerView.adapter = productAdapter

        databaseRef = FirebaseDatabase.getInstance(
            "https://stylegenie-9c50a-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).reference

        fetchFavoriteProducts()
    }

    private fun fetchFavoriteProducts() {
        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                favoriteList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        it.id = productSnapshot.key ?: ""  // ✅ Set ID
                        if (it.isFavorite) {               // ✅ Computed getter works
                            favoriteList.add(it)
                        }
                    }
                }

                productAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@FavoritesActivity,
                    "Failed to load favorites",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }
}
