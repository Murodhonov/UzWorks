package dev.goblingroup.uzworks.adapters

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import dev.goblingroup.uzworks.R
import dev.goblingroup.uzworks.database.entity.DistrictEntity

class DistrictAdapter(
    private val context: Context,
    private val resourceId: Int,
    private val items: List<DistrictEntity>
) : ArrayAdapter<DistrictEntity>(context, resourceId, items) {

    private val tempItems: List<DistrictEntity> = ArrayList(items)
    private val suggestions: MutableList<DistrictEntity> = ArrayList()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        try {
            if (convertView == null) {
                val inflater = (context as Activity).layoutInflater
                view = inflater.inflate(resourceId, parent, false)
            }
            val district = getItem(position)
            val name = view!!.findViewById<TextView>(R.id.tv)
            name.text = district?.name
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return view!!
    }

    override fun getItem(position: Int): DistrictEntity {
        return items[position]
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getFilter(): Filter {
        return districtFilter
    }

    private val districtFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): CharSequence {
            val district = resultValue as DistrictEntity
            return district.name
        }

        override fun performFiltering(charSequence: CharSequence?): FilterResults {
            if (charSequence != null) {
                suggestions.clear()
                for (district in tempItems) {
                    if (district.name.toLowerCase().startsWith(charSequence.toString().toLowerCase())) {
                        suggestions.add(district)
                    }
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                return filterResults
            } else {
                return FilterResults()
            }
        }

        override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
            val tempValues = filterResults.values as ArrayList<DistrictEntity>?
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
