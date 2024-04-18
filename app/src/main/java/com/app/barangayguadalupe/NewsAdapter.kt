package com.app.barangayguadalupe

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewsAdapter(
    private val newsList: List<NewsItem>,
    private val imageLoader: ImageLoader
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_card_view, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val currentItem = newsList[position]
        holder.newsTitle.text = currentItem.title
        holder.newsDescription.text = currentItem.description
        holder.newsDate.text = currentItem.datePublished
        imageLoader.loadImage(currentItem.image, holder.newsImage)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsImage: ImageView = itemView.findViewById(R.id.newsImage)
        val newsTitle: TextView = itemView.findViewById(R.id.newsTitle)
        val newsDescription: TextView = itemView.findViewById(R.id.newsDescription)
        val newsDate: TextView = itemView.findViewById(R.id.newsDate)
    }

    interface ImageLoader {
        fun loadImage(imageUrl: String, targetImageView: ImageView)
    }
}
