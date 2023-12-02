package dev.goblingroup.uzworks.adapters.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.databinding.LeftMessageItemBinding
import dev.goblingroup.uzworks.databinding.RightMessageItemBinding

class MessagesAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val right = 1
    private val left = 2

    inner class LeftMessageViewHolder(private val leftItemBinding: LeftMessageItemBinding) :
        RecyclerView.ViewHolder(leftItemBinding.root) {

        fun onBind() {

        }
    }

    inner class RightMessageViewHolder(private val rightItemBinding: RightMessageItemBinding) :
        RecyclerView.ViewHolder(rightItemBinding.root) {
        fun onBind() {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == left) {
            LeftMessageViewHolder(
                LeftMessageItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else RightMessageViewHolder(
            RightMessageItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = 30

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is LeftMessageViewHolder) {
            holder.onBind()
        } else if (holder is RightMessageViewHolder) {
            holder.onBind()
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position % 2 == 0) left else right
    }

}