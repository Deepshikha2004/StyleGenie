package com.example.stylegenie

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ProductAdapter(
    private val productList: List<Product>
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
        Log.d("Adapter", "ViewHolder created")
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        holder.imageProduct.setImageResource(product.imageResId)
        holder.textProductName.text = product.name
        holder.textProductPrice.text = product.price

        val favoriteIcon = if (product.isFavorite) {
            R.drawable.ic_heart_filled
        } else {
            R.drawable.ic_heart_border
        }
        holder.imageFavorite.setImageResource(favoriteIcon)

        // Handle favorite toggle
        holder.imageFavorite.setOnClickListener {
            product.isFavorite = !product.isFavorite
            notifyItemChanged(position)
        }

        // 🔥 Handle item click to go to detail
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProductDetailActivity::class.java).apply {
                putExtra("name", product.name)
                putExtra("price", product.price)
                putExtra("description", product.description)
                putExtra("imageResId", product.imageResId)
            }
            context.startActivity(intent)

            if (context is android.app.Activity) {
                context.overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
            }
        }
    }

    override fun getItemCount(): Int = productList.size
}
