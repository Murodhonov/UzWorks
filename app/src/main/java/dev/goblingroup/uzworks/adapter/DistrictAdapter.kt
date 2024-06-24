package dev.goblingroup.uzworks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.database.entity.DistrictEntity
import dev.goblingroup.uzworks.databinding.BottomSelectionItemBinding

class DistrictAdapter(
    private val regionList: List<DistrictEntity>,
    private val onItemSelected: (String, String) -> Unit
) : RecyclerView.Adapter<DistrictAdapter.DistrictViewHolder>() {

    inner class DistrictViewHolder(private val districtItem: BottomSelectionItemBinding) :
        RecyclerView.ViewHolder(districtItem.root) {
        fun onBind(district: DistrictEntity) {
            districtItem.apply {
                root.text = district.name
                root.setOnClickListener {
                    onItemSelected.invoke(district.id, district.name)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DistrictViewHolder {
        return DistrictViewHolder(
            BottomSelectionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = regionList.size

    override fun onBindViewHolder(holder: DistrictViewHolder, position: Int) {
        holder.onBind(regionList[position])
    }

}