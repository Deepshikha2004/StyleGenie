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

        // 📝 Listen to search input
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterProducts(s.toString())
            }
        })

        // 🔄 Setup RecyclerView
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.setHasFixedSize(true)
        productAdapter = ProductAdapter(productList.toMutableList())
        recyclerView.adapter = productAdapter

        // 🔗 Firebase reference (root)
        databaseRef = FirebaseDatabase.getInstance(
            "https://stylegenie-9c50a-default-rtdb.asia-southeast1.firebasedatabase.app"
        ).reference

        // ⏳ Load all products initially
        fetchProductsFromFirebase()

        // 🟣 Chip click filters
        setupCategoryFilters()

        // ⬇️ Bottom navigation
        setupBottomNav()
    }

    private fun setupCategoryFilters() {
        chipGroup.setOnCheckedChangeListener { _, checkedId ->
            animateAndLoad {
                etSearch.setText("") // clear search when chip is changed
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
                for (productSnapshot in snapshot.children) {
                    val product = productSnapshot.getValue(Product::class.java)
                    product?.let {
                        it.id = productSnapshot.key ?: ""  // ✅ Set product ID from Firebase key
                        productList.add(it)
                    }
                }
                productList.shuffle()
                productAdapter.updateList(productList)
                progressBar.visibility = View.GONE
                Log.d("HomeActivity", "Fetched ${productList.size} products")
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Failed to fetch products", Toast.LENGTH_SHORT).show()
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
                        product.id = productSnapshot.key ?: ""
                        productList.add(product)
                    }
                }
                productAdapter.updateList(productList)
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
                productAdapter.updateList(productList)
                progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@HomeActivity, "Failed to fetch data", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        })
    }

    private fun filterProducts(query: String) {
        val lowercaseQuery = query.trim().lowercase()

        val filteredList = productList.filter { product ->
            product.category.lowercase().contains(lowercaseQuery) ||
                    product.description.lowercase().contains(lowercaseQuery) ||
                    product.gender.lowercase().contains(lowercaseQuery) ||
                    product.category?.lowercase()?.contains(lowercaseQuery) == true
        }

        productAdapter.updateList(filteredList)
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
