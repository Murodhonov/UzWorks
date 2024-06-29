package com.goblindevs.uzworks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goblindevs.uzworks.databinding.ChatUserItemBinding

class ChatUsersAdapter(
    private val userList: List<Int>,
    private val onUserClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<ChatUsersAdapter.ChatUserViewHolder>() {

    private val TAG = "ChatUsersAdapter"

    inner class ChatUserViewHolder(private val chatUserItemBinding: ChatUserItemBinding) :
        RecyclerView.ViewHolder(chatUserItemBinding.root) {

        fun onBind(position: Int) {
            chatUserItemBinding.apply {
                chatItem.setOnClickListener {
                    onUserClick.invoke(position)
                }

                deleteBtn.setOnClickListener {
                    onDeleteClick.invoke(position)
                }

            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        val chatBinding =
            ChatUserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatUserViewHolder(chatBinding)
    }

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {
        holder.onBind(position)
    }
}