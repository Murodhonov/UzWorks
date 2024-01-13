package dev.goblingroup.uzworks.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.JobCategoryEntity

class JobCategoryAdapter(
    private val context: Context,
    private val resourceId: Int,
    private val jobCategoryList: List<JobCategoryEntity>
) : ArrayAdapter<JobCategoryEntity>(context, resourceId, jobCategoryList) {

    private val tempItems: List<JobCategoryEntity> = ArrayList(jobCategoryList)
    private val suggestions: MutableList<JobCategoryEntity> = ArrayList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        try {
            if (convertView == null) {
                val inflater = (context as Activity).layoutInflater
                view = inflater.inflate(resourceId, parent, false)
            }
            val jobCategory = getItem(position)
            val name = view!!.findViewById<TextView>(R.id.tv)
            name.text = jobCategory?.title
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view!!
    }

    override fun getItem(position: Int): JobCategoryEntity? {
        return jobCategoryList[position]
    }

    override fun getCount(): Int {
        return jobCategoryList.size
    }

    override fun getFilter(): Filter {
        return jobCategoryFilter
    }

    private val jobCategoryFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence {
            val region = resultValue as JobCategoryEntity
            return region.title
        }

        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            return if (charSequence != null) {
                suggestions.clear()
                for (jobCategory in tempItems) {
                    if (jobCategory.title.toLowerCase()
                            .startsWith(charSequence.toString().toLowerCase())
                    ) {
                        suggestions.add(jobCategory)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            } else {
                FilterResults()
            }
        }

        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            val tempValues = filterResults.values as ArrayList<JobCategoryEntity>?
            if (filterResults != null && filterResults.count > 0) {
                clear()
                tempValues?.let { addAll(it) }
                notifyDataSetChanged()
            } else {
                clear()
                notifyDataSetChanged()
            }
        }
    }
}