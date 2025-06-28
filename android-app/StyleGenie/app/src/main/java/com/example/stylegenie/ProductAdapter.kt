package com.example.stylegenie

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase

class ProductAdapter(
    private val productList: MutableList<Product>
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageProduct: ImageView = itemView.findViewById(R.id.imageProduct)
        val imageFavorite: ImageView = itemView.findViewById(R.id.imageFavorite)
        val textProductName: TextView = itemView.findViewById(R.id.textProductName)
        val textProductPrice: TextView = itemView.findViewById(R.id.textProductPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        // ✅ Construct valid image URL from GitHub
        val imageUrl = if (product.images.isNotEmpty()) {
            "${product.img_path}/${product.images[0]}"
                .replace("github.com", "raw.githubusercontent.com")
                .replace("/tree/", "/")
        } else ""

        // Load image
        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.imageProduct)

        // Set product name and price
        holder.textProductName.text = product.category
        holder.textProductPrice.text = "₹${product.price}"

        // Set favorite icon state
        val favoriteIcon = if (product.isFavorite) {
            R.drawable.ic_heart_filled
        } else {
            R.drawable.ic_heart_border
        }
        holder.imageFavorite.setImageResource(favoriteIcon)

        // ✅ Favorite toggle with Firebase update
        holder.imageFavorite.setOnClickListener {
            val newFavorite = !product.isFavorite
            product.isFavoriteRaw = newFavorite

            notifyItemChanged(position)

            // Ensure product has ID (assigned during fetch)
            val productId = product.id ?: run {
                Log.e("ProductAdapter", "Product ID is null, can't update favorite")
                return@setOnClickListener
            }

            val databaseRef = FirebaseDatabase.getInstance(
                "https://stylegenie-9c50a-default-rtdb.asia-southeast1.firebasedatabase.app"
            ).reference.child(productId)

            databaseRef.child("isFavorite").setValue(product.isFavorite)
                .addOnSuccessListener {
                    Log.d("ProductAdapter", "Favorite updated for $productId")
                }
                .addOnFailureListener { e ->
                    Log.e("ProductAdapter", "Failed to update favorite: ${e.message}")
                }
        }


        // Go to ProductDetailActivity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProductDetailActivity::class.java).apply {
                putExtra("category", product.category)
                putExtra("price", "₹${product.price}")
                putExtra("description", product.description)
                putExtra("img_path", product.img_path)
                putStringArrayListExtra("imageList", ArrayList(product.images))
            }
            context.startActivity(intent)

            if (context is Activity) {
                context.overridePendingTransition(
                    android.R.anim.slide_in_left,
                    android.R.anim.slide_out_right
                )
            }
        }
    }

    override fun getItemCount(): Int = productList.size

    fun updateList(filtered: List<Product>) {
        productList.clear()
        productList.addAll(filtered)
        notifyDataSetChanged()
    }
}
