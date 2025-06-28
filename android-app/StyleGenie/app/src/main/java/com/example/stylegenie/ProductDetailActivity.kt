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

        // ✅ Get Product from intent
        val product = intent.getSerializableExtra("product") as? Product

        if (product != null) {
            textProductName.text = product.category
            textProductPrice.text = "₹${product.price}"
            textProductDescription.text = product.description

            val imageUrl = if (product.images.isNotEmpty()) {
                "${product.img_path}/${product.images[0]}"
                    .replace("github.com", "raw.githubusercontent.com")
                    .replace("/tree/", "/")
            } else ""

            if (imageUrl.isNotEmpty()) {
                Glide.with(this).load(imageUrl).into(imageProduct)
            } else {
                imageProduct.setImageResource(R.drawable.fashion_image2) // fallback
            }
        } else {
            Toast.makeText(this, "Error: Product data missing", Toast.LENGTH_SHORT).show()
            finish()
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
            Toast.makeText(
                this,
                "${product?.category ?: "Product"} added to cart (x$quantity)",
                Toast.LENGTH_SHORT
            ).show()
        }

        btnBuyNow.setOnClickListener {
            Toast.makeText(
                this,
                "Proceeding to buy ${product?.category ?: "product"} (x$quantity)",
                Toast.LENGTH_SHORT
            ).show()
        }

        // Back button
        btnBack.setOnClickListener {
            finish()
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }
}
