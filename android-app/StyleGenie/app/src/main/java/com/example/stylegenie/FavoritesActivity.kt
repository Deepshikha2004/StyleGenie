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
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        recyclerView = findViewById(R.id.rvFavorites)
        progressBar = findViewById(R.id.progressBarFavorites)

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        productAdapter = ProductAdapter(favoriteList)
        recyclerView.adapter = productAdapter

        databaseRef = FirebaseDatabase.getInstance(
            "https://stylegenie-9c50a-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).getReference("products")  // ✅ not just .reference


        fetchFavoriteProducts()
    }

    private fun fetchFavoriteProducts() {
        progressBar.visibility = View.VISIBLE

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                favoriteList.clear()
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        it.id = productSnapshot.key ?: ""
                        // 👇 Add this log to see what's coming
                        android.util.Log.d("DEBUG_FAV", "ID: ${it.id}, Favorite: ${it.isFavorite}, Category: ${it.category}")
                        if (it.isFavorite) {
                            favoriteList.add(it)
                        }
                    }
                }

                productAdapter.updateList(favoriteList)
                progressBar.visibility = View.GONE

                if (favoriteList.isEmpty()) {
                    Toast.makeText(this@FavoritesActivity, "No liked products found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                progressBar.visibility = View.GONE
                Toast.makeText(this@FavoritesActivity, "Failed to load favorites", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
