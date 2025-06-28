package com.example.stylegenie

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.firebase.database.*

class HomeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var databaseRef: DatabaseReference
    private lateinit var progressBar: ProgressBar
    private lateinit var chipGroup: ChipGroup
    private lateinit var etSearch: EditText


    private val productList = mutableListOf<Product>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // 🔄 Initialize views
        progressBar = findViewById(R.id.progressBar)
        recyclerView = findViewById(R.id.rvProducts)
        bottomNavigationView = findViewById(R.id.bottomNav)
        chipGroup = findViewById(R.id.chipGroupCategories)
        etSearch = findViewById(R.id.etSearch)

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })


        // 🔄 Setup RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
        productAdapter = ProductAdapter(productList)
        recyclerView.adapter = productAdapter

        // 🔗 Firebase reference
        databaseRef = FirebaseDatabase.getInstance(
            "https://stylegenie-9c50a-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).reference

        // 🔧 Category filter logic
        setupCategoryFilters()

        // 🚀 Initial load
        fetchProductsFromFirebase()

        // 🔧 Bottom nav
        setupBottomNav()
    }
    private fun filterProducts(query: String) {
        val filteredList = productList.filter {
            it.category.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true)
        }
        productAdapter.updateList(filteredList)
    }


    private fun setupCategoryFilters() {
        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            animateAndLoad {
                when (checkedId) {
                    R.id.chipAll -> fetchProductsFromFirebase()
                    R.id.chipMens -> fetchProductsByGender("Men")
                    R.id.chipWomens -> fetchProductsByGender("Women")
                    R.id.chipShirt -> fetchProductsByCategory("Sweaters")
                    R.id.chipPant -> fetchProductsByCategory("Pants")
                    R.id.chipDress -> fetchProductsByCategory("Dresses")
                }
            }
        }
    }

    private fun animateAndLoad(loadAction: () -> Unit) {
        recyclerView.animate()
            .alpha(0f)
            .setDuration(150)
            .withEndAction {
                loadAction()
                recyclerView.animate().alpha(1f).setDuration(200).start()
            }.start()
    }

    private fun fetchProductsFromFirebase() {
        progressBar.visibility = View.VISIBLE

        databaseRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                productList.clear()

                if (!snapshot.exists()) {
                    Toast.makeText(this@HomeActivity, "No products found", Toast.LENGTH_SHORT).show()
                    progressBar.visibility = View.GONE
                    return
                }

                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let { productList.add(it) }
                }

                productList.shuffle() // Mix gender data
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
