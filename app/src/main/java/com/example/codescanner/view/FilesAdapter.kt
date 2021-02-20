package com.example.codescanner.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.codescanner.R
import com.example.codescanner.databinding.FileItemBinding
import com.example.codescanner.model.File

class FilesAdapter(private val removedListener: OnItemClick,
                   private var items: ArrayList<File>,
                   private val context: Context?) : RecyclerView.Adapter<FilesAdapter.MyHolder>() {

    private var lastPosition = -1
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding: FileItemBinding =
                DataBindingUtil.inflate(layoutInflater, R.layout.file_item, parent, false)
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

    fun submitData(giftItems: ArrayList<File>) {
        this.items = giftItems
        notifyDataSetChanged()
    }


    class MyHolder(private val binding: FileItemBinding,
                   private val listener: OnItemClick) : RecyclerView.ViewHolder(binding.root) {
        fun bind(file: File) {
            binding.fileName.text = file.fileName
            val split = file.date.split(" ")
            binding.fileDate.text = split[0]
            val time ="${split[1]} ${split[2]}"
            binding.fileTime.text = time
            binding.position.text = (adapterPosition + 1).toString()
            binding.deleteItem.setOnClickListener { listener.onItemClick(adapterPosition , binding.deleteItem) }
            binding.share.setOnClickListener { listener.onItemClick(adapterPosition , binding.share) }
            binding.export.setOnClickListener { listener.onItemClick(adapterPosition , binding.export) }
        }
    }
}
