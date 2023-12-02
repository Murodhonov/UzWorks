package dev.goblingroup.uzworks.adapters.rv_adapters

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.databinding.ChatUserItemBinding

class ChatUsersAdapter(
    private val context: Context,
    private val onUserClick: () -> Unit
) : RecyclerView.Adapter<ChatUsersAdapter.ChatUserViewHolder>() {

    private val TAG = "ChatUsersAdapter"

    inner class ChatUserViewHolder(private val chatUserItemBinding: ChatUserItemBinding) :
        RecyclerView.ViewHolder(chatUserItemBinding.root) {

        private var initialX = 0f
        private var isSwipedLeft = false
        private var isSwipedRight = false
        private var dX = 0f

        fun onBind() {
            chatUserItemBinding.apply {
                chatItem.setOnClickListener {
                    Toast.makeText(context, "item clicked", Toast.LENGTH_SHORT).show()
                    onUserClick.invoke()
                }

                chatItem.setOnTouchListener { _, event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            Log.d(TAG, "onBind: MotionEvent.ACTION_DOWN case")
                            Log.d(TAG, "onBind: before initialX -> $initialX")
                            Log.d(TAG, "onBind: before isSwipedLeft -> $isSwipedLeft")
                            Log.d(TAG, "onBind: before isSwipedRight -> $isSwipedRight")
                            initialX = event.rawX
                            isSwipedLeft = false
                            isSwipedRight = false
                            Log.d(TAG, "onBind: after initialX -> $initialX")
                            Log.d(TAG, "onBind: after isSwipedLeft -> $isSwipedLeft")
                            Log.d(TAG, "onBind: after isSwipedRight -> $isSwipedRight")
                        }

                        MotionEvent.ACTION_MOVE -> {
                            Log.d(TAG, "onBind: MotionEvent.ACTION_MOVE case")
                            dX = event.rawX - initialX
                            Log.d(TAG, "onBind: dX -> $dX")
                            Log.d(TAG, "onBind: event.rawX -> ${event.rawX}")
                            Log.d(TAG, "onBind: initialX -> $initialX")

                            // Swipe to the left
                            if (dX < 0) {
                                isSwipedLeft = true
                                isSwipedRight = false
                            }

                            // Swipe to the right
                            if (dX > 0) {
                                isSwipedLeft = false
                                isSwipedRight = true
                            }

                            // Set translationX based on the swipe direction
                            chatItem.translationX = dX
                        }

                        MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                            Log.d(TAG, "onBind: MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL case")
                            Log.d(TAG, "onBind: isSwipedRight -> $isSwipedRight")
                            Log.d(TAG, "onBind: isSwipedLeft -> $isSwipedLeft")
                            if (isSwipedLeft && chatItem.translationX < -dpToPx(63)) {
                                // Case 1: Left from 1st point
                                chatItem.animate().translationX(-dpToPx(63)).setDuration(200)
                                    .start()
                                Log.d(TAG, "onBind: case 1")
                            } else if (chatItem.translationX in -dpToPx(50)..dpToPx(50)) {
                                // Case 2: At the 1st point
                                if (dX != 0f) {
                                    chatItem.animate().translationX(-dpToPx(63)).setDuration(200)
                                        .start()
                                }
                                Log.d(TAG, "onBind: case 2")
                            } else if (isSwipedRight && chatItem.translationX > 0) {
                                // Case 3: Right from the default position
                                chatItem.animate().translationX(0f).setDuration(200).start()
                                Log.d(TAG, "onBind: case 3")
                            } else {
                                // Case 4: Between 1st and 2nd points
                                Log.d(TAG, "onBind: case 4")
                                if (chatItem.translationX < -dpToPx(25)) {
                                    // Closer to the 1st point, move to the 1st point
                                    chatItem.animate().translationX(-dpToPx(63)).setDuration(200)
                                        .start()
                                    Log.d(TAG, "onBind: if condition in case 4")
                                } else {
                                    // Closer to the 2nd point, move to the default position
                                    chatItem.animate().translationX(0f).setDuration(200).start()
                                    Log.d(TAG, "onBind: else condition in case 4")
                                }
                            }

                            // Reset flags
                            isSwipedLeft = false
                            isSwipedRight = false
                        }
                    }
                    true
                }
            }
        }

        private fun dpToPx(distance: Int): Float {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                distance.toFloat(),
                Resources.getSystem().displayMetrics
            )
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatUserViewHolder {
        val chatBinding =
            ChatUserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatUserViewHolder(chatBinding)
    }

    override fun getItemCount(): Int = 15

    override fun onBindViewHolder(holder: ChatUserViewHolder, position: Int) {
        holder.onBind()
    }
}