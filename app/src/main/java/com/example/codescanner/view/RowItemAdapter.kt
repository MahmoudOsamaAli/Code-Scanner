package com.example.codescanner.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.codescanner.R
import com.example.codescanner.databinding.RowItemBinding

class RowItemAdapter(private val removedListener: onItemRemovedListener,
                     private var items: ArrayList<String>,
                     private val context: Context?) : RecyclerView.Adapter<RowItemAdapter.MyHolder>() {

    private var lastPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: RowItemBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.row_item, parent, false)
        return MyHolder(binding, removedListener)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.bind(items[position])
        setAnimation(holder.itemView, position)
    }

    private fun setAnimation(viewToAnimate: View, position: Int) {
        if (position > lastPosition) {
            val animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in)
            animation.duration = 1000
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }

    override fun getItemCount(): Int = items.size

    fun setLastPosition(i:Int){
        lastPosition = i
    }

    fun submitData(giftItems: ArrayList<String>) {
        this.items = giftItems
        notifyDataSetChanged()
    }


    class MyHolder(private val binding: RowItemBinding,
                   private val listener: onItemRemovedListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: String) {
            binding.serialNum.text = product
            binding.position.text = (adapterPosition + 1).toString()
            binding.deleteItem.setOnClickListener { listener.onItemRemoved(adapterPosition) }
        }
    }
}
