package dev.baharudin.themoviedb.presentation.detail.movie_review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import dev.baharudin.themoviedb.databinding.ItemReviewCardBinding
import dev.baharudin.themoviedb.domain.entities.Review
import dev.baharudin.themoviedb.presentation.common.toDate
import dev.baharudin.themoviedb.presentation.common.toString

class MovieReviewListAdapter(
) : PagingDataAdapter<Review, MovieReviewListAdapter.ListViewHolder>(differCallback) {
    companion object {
        private val differCallback = object : DiffUtil.ItemCallback<Review>() {
            override fun areItemsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Review, newItem: Review): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ListViewHolder(private val binding: ItemReviewCardBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(review: Review?) {
            if (review == null) return
            with(binding) {
                tvName.text = review.author
                tvContent.text = review.content

                val date = review.updatedAt.toDate()
                date?.let {
                    tvDate.text = it.toString(format = "EEE, dd MMM yyyy")
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            ItemReviewCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }
}