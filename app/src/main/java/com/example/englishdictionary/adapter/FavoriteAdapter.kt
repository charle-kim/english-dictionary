package com.example.englishdictionary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.englishdictionary.data.FavoriteWord
import com.example.englishdictionary.databinding.ItemFavoriteBinding

class FavoriteAdapter(
    private val items: MutableList<FavoriteWord>,
    private val onClick: (FavoriteWord) -> Unit,
    private val onDelete: (FavoriteWord) -> Unit
) : RecyclerView.Adapter<FavoriteAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.binding.textWord.text = item.word
        holder.binding.textMeaning.text = item.meaningKo
        holder.binding.root.setOnClickListener { onClick(item) }
        holder.binding.buttonDelete.setOnClickListener { onDelete(item) }
    }

    override fun getItemCount(): Int = items.size

    fun setItems(newItems: List<FavoriteWord>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }
}
