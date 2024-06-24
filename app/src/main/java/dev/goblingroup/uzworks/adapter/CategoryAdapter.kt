package dev.goblingroup.uzworks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity
import dev.goblingroup.uzworks.databinding.BottomSelectionItemBinding

class CategoryAdapter(
    private val regionList: List<JobCategoryEntity>,
    private val onItemSelected: (String, String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    inner class CategoryViewHolder(private val categoryItem: BottomSelectionItemBinding) :
        RecyclerView.ViewHolder(categoryItem.root) {
        fun onBind(category: JobCategoryEntity) {
            categoryItem.apply {
                root.text = category.title
                root.setOnClickListener {
                    onItemSelected.invoke(category.id, category.title)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            BottomSelectionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = regionList.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.onBind(regionList[position])
    }

}