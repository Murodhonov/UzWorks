package dev.goblingroup.uzworks.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.RegionEntity

class RegionAdapter(
    private val context: Context,
    private val resourceId: Int,
    private val regionList: List<RegionEntity>
) : ArrayAdapter<RegionEntity>(context, resourceId, regionList) {

    private val tempItems: List<RegionEntity> = ArrayList(regionList)
    private val suggestions: MutableList<RegionEntity> = ArrayList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        try {
            if (convertView == null) {
                val inflater = (context as Activity).layoutInflater
                view = inflater.inflate(resourceId, parent, false)
            }
            val region = getItem(position)
            val name = view!!.findViewById<TextView>(R.id.tv)
            name.text = region?.name
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view!!
    }

    override fun getItem(position: Int): RegionEntity? {
        return regionList[position]
    }

    override fun getCount(): Int {
        return regionList.size
    }

    override fun getFilter(): Filter {
        return regionFilter
    }

    private val regionFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence {
            val region = resultValue as RegionEntity
            return region.name
        }

        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            return if (charSequence != null) {
                suggestions.clear()
                for (region in tempItems) {
                    if (region.name.toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                        suggestions.add(region)
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
            val tempValues = filterResults.values as ArrayList<RegionEntity>?
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