package com.goblindevs.uzworks.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.goblindevs.uzworks.database.entity.JobCategoryEntity
import com.goblindevs.uzworks.databinding.BottomSelectionItemBinding

class CategoryAdapter(
    private val categoryList: List<JobCategoryEntity>,
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

    override fun getItemCount() = categoryList.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.onBind(categoryList[position])
    }

}