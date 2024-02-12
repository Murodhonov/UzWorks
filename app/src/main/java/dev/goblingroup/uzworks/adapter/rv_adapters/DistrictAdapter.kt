package dev.goblingroup.uzworks.adapter.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.databinding.DistrictItemBinding

class DistrictAdapter(
    private var districtList: List<DistrictEntity>,
    private val onEditClick: (DistrictEntity) -> Unit,
    private val onDeleteClick: (DistrictEntity, Int, ProgressBar, ImageView) -> Unit
) : RecyclerView.Adapter<DistrictAdapter.FieldsViewHolder>() {

    inner class FieldsViewHolder(private var fieldItemBinding: DistrictItemBinding) :
        RecyclerView.ViewHolder(fieldItemBinding.root) {
        fun onBind(districtResponse: DistrictEntity, position: Int) {
            fieldItemBinding.apply {
                fieldNameTv.text = districtResponse.name
                edit.setOnClickListener {
                    onEditClick.invoke(districtResponse)
                }
                delete.setOnClickListener {
                    onDeleteClick.invoke(districtResponse, position, deleteProgress, delete)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FieldsViewHolder {
        return FieldsViewHolder(
            DistrictItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = districtList.size

    override fun onBindViewHolder(holder: FieldsViewHolder, position: Int) {
        holder.onBind(districtList[position], position)
    }

}