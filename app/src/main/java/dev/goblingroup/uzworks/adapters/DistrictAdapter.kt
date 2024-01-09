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
    private val districtList: List<DistrictEntity>
) : ArrayAdapter<DistrictEntity>(
    context,
    resourceId,
    districtList
) {
    private val tempItems: ArrayList<DistrictEntity> = ArrayList(districtList)
    private val suggestions: ArrayList<DistrictEntity> = ArrayList()

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

    override fun getItem(position: Int): DistrictEntity? = districtList[position]

    override fun getCount(): Int = districtList.size

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getFilter(): Filter = districtFilter

    private val districtFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any?): CharSequence {
            val district = resultValue as DistrictEntity
            return district.name
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return if (constraint != null) {
                suggestions.clear()
                for (district in tempItems) {
                    if (district.name.lowercase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(district)
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

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val tempValues = results?.values as ArrayList<DistrictEntity>
            if (results != null && results.count > 0) {
                clear()
                for (districtObj in districtList)
            }
        }

    }

}