package dev.goblingroup.uzworks.adapters.rv_adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.databinding.DistrictItemBinding
import dev.goblingroup.uzworks.models.response.DistrictResponse

class DistrictAdapter(
    private var districtList: List<DistrictResponse>,
    private val onEditClick: (DistrictResponse) -> Unit,
    private val onDeleteClick: (DistrictResponse, Int, ProgressBar, ImageView) -> Unit
) : RecyclerView.Adapter<DistrictAdapter.FieldsViewHolder>() {

    inner class FieldsViewHolder(private var fieldItemBinding: DistrictItemBinding) :
        RecyclerView.ViewHolder(fieldItemBinding.root) {
        fun onBind(districtResponse: DistrictResponse, position: Int) {
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