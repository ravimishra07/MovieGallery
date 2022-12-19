package com.ravi.fimoviesdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ravi.fimoviesdemo.databinding.ItemLoaderBinding
import com.ravi.fimoviesdemo.util.hide
import com.ravi.fimoviesdemo.util.show

class LoaderStateAdapter :
    LoadStateAdapter<LoaderStateAdapter.LoaderViewHolder>() {

    override fun onBindViewHolder(holder: LoaderViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoaderViewHolder {
        return LoaderViewHolder.getInstance(parent)
    }

        class LoaderViewHolder(private val binding: ItemLoaderBinding) : RecyclerView.ViewHolder(binding.root) {

        companion object {
            fun getInstance(parent: ViewGroup): LoaderViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemLoaderBinding.inflate(layoutInflater, parent, false)
                return LoaderViewHolder(binding)
            }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Loading) {
                binding.pbLoader.show()
            } else {
                binding.pbLoader.hide()
            }
        }
    }
}