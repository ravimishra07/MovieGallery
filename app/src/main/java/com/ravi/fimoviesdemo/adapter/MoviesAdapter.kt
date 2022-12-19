package com.ravi.fimoviesdemo.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ravi.fimoviesdemo.R
import com.ravi.fimoviesdemo.databinding.MoviesRowGridLayoutBinding
import com.ravi.fimoviesdemo.model.Movie

class MoviesAdapter : PagingDataAdapter<Movie, RecyclerView.ViewHolder>(REPO_COMPARATOR) {
    companion object {
        private val REPO_COMPARATOR = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie) =
                oldItem.imdbID == newItem.imdbID

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie) =
                oldItem.imdbID == newItem.imdbID
        }
    }

    class GridViewHolder(private val binding: MoviesRowGridLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(movie: Movie) {
            binding.tvMovieTitle.text = movie.movieTitle
            binding.tvMovieType.text = movie.type
            Glide.with(binding.root.context)
                .load(movie.poster)
                .placeholder(R.drawable.placeholder_img)
                .into(binding.movieImage)
        }

        companion object {
            fun from(parent: ViewGroup): GridViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MoviesRowGridLayoutBinding.inflate(layoutInflater, parent, false)
                return GridViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
      return GridViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        getItem(position)?.let {
            (holder as GridViewHolder).bind(it)
        }
    }
}