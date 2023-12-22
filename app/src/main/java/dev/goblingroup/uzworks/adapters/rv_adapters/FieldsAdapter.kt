package dev.goblingroup.uzworks.adapters.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.databinding.FieldItemBinding

class FieldsAdapter(
    private var fieldsList: List<String>
) : RecyclerView.Adapter<FieldsAdapter.FieldsViewHolder>() {

    inner class FieldsViewHolder(fieldItemBinding: FieldItemBinding) :
        RecyclerView.ViewHolder(fieldItemBinding.root) {
        fun onBind(position: Int) {

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldsViewHolder {
        return FieldsViewHolder(
            FieldItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = fieldsList.size

    override fun onBindViewHolder(holder: FieldsViewHolder, position: Int) {
        holder.onBind(position)
    }

}