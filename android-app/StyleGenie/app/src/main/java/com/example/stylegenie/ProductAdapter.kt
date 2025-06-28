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
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]

        // ✅ Load product image from 'img_path' (full URL)
        Glide.with(holder.itemView.context)
            .load(product.img_path)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.imageProduct)

        // ✅ Set product name and price
        holder.textProductName.text = product.category // category is the product name
        holder.textProductPrice.text = product.price.toString()

        // ✅ Favorite toggle
        val favoriteIcon = if (product.isFavorite) {
            R.drawable.ic_heart_filled
        } else {
            R.drawable.ic_heart_border
        }
        holder.imageFavorite.setImageResource(favoriteIcon)

        holder.imageFavorite.setOnClickListener {
            product.isFavorite = !product.isFavorite
            notifyItemChanged(position)
        }

        // ✅ On item click, go to ProductDetailActivity
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, ProductDetailActivity::class.java).apply {
                putExtra("category", product.category)
                putExtra("price", product.price)
                putExtra("description", product.description)
                putExtra("img_path", product.img_path)
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
}
