package com.example.stylegenie

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var imageProduct: ImageView
    private lateinit var textProductName: TextView
    private lateinit var textProductPrice: TextView
    private lateinit var textProductDescription: TextView
    private lateinit var textQuantity: TextView
    private lateinit var btnIncreaseQty: ImageButton
    private lateinit var btnDecreaseQty: ImageButton
    private lateinit var btnAddToCart: Button
    private lateinit var btnBuyNow: Button
    private lateinit var btnBack: ImageView

    private var quantity = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        // Bind views
        imageProduct = findViewById(R.id.imageProduct)
        textProductName = findViewById(R.id.textProductName)
        textProductPrice = findViewById(R.id.textProductPrice)
        textProductDescription = findViewById(R.id.textProductDescription)
        textQuantity = findViewById(R.id.textQuantity)
        btnIncreaseQty = findViewById(R.id.btnIncreaseQty)
        btnDecreaseQty = findViewById(R.id.btnDecreaseQty)
        btnAddToCart = findViewById(R.id.btnAddToCart)
        btnBuyNow = findViewById(R.id.btnBuyNow)
        btnBack = findViewById(R.id.btnBack)

        // ✅ Get intent data (match keys from adapter)
        val name = intent.getStringExtra("name") ?: "Sample Product"
        val price = intent.getStringExtra("price") ?: "₹0"
        val description = intent.getStringExtra("description") ?: "No description available."
        val imgPath = intent.getStringExtra("img_path") ?: ""
        val imageList = intent.getStringArrayListExtra("imageList") ?: arrayListOf()

        // Set product info
        textProductName.text = name
        textProductPrice.text = price
        textProductDescription.text = description

        // Build valid image URL from GitHub raw link
        val firstImageUrl = if (imageList.isNotEmpty()) {
            "$imgPath/${imageList[0]}"
                .replace("github.com", "raw.githubusercontent.com")
                .replace("/tree/", "/")
        } else ""

        // Load image
        if (firstImageUrl.isNotEmpty()) {
            Glide.with(this).load(firstImageUrl).into(imageProduct)
        } else {
            imageProduct.setImageResource(R.drawable.fashion_image2)
        }

        // Back button
        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        // Quantity logic
        textQuantity.text = quantity.toString()

        btnIncreaseQty.setOnClickListener {
            if (quantity < 10) {
                quantity++
                textQuantity.text = quantity.toString()
            }
        }

        btnDecreaseQty.setOnClickListener {
            if (quantity > 1) {
                quantity--
                textQuantity.text = quantity.toString()
            }
        }

        btnAddToCart.setOnClickListener {
            Toast.makeText(this, "$name added to bag (x$quantity)", Toast.LENGTH_SHORT).show()
        }

        btnBuyNow.setOnClickListener {
            Toast.makeText(this, "Proceeding to buy $name (x$quantity)", Toast.LENGTH_SHORT).show()
        }
    }
}
