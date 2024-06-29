package com.goblindevs.uzworks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goblindevs.uzworks.database.entity.RegionEntity
import com.goblindevs.uzworks.databinding.BottomSelectionItemBinding

class RegionAdapter(
    private val regionList: List<RegionEntity>,
    private val onItemSelected: (String, String) -> Unit
) : RecyclerView.Adapter<RegionAdapter.RegionViewHolder>() {

    inner class RegionViewHolder(private val regionItem: BottomSelectionItemBinding) :
        RecyclerView.ViewHolder(regionItem.root) {
        fun onBind(region: RegionEntity) {
            regionItem.apply {
                root.text = region.name
                root.setOnClickListener {
                    onItemSelected.invoke(region.id, region.name)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegionViewHolder {
        return RegionViewHolder(
            BottomSelectionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = regionList.size

    override fun onBindViewHolder(holder: RegionViewHolder, position: Int) {
        holder.onBind(regionList[position])
    }

}