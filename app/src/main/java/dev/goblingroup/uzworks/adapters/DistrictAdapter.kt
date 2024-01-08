package dev.goblingroup.uzworks.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import dev.goblingroup.uzworks.database.entity.DistrictEntity

class DistrictAdapter(
    context: Context,
    private val districtList: List<DistrictEntity>
) : ArrayAdapter<DistrictEntity>(
    context,
    android.R.layout.simple_dropdown_item_1line,
    districtList
) {

    init {
        if (districtList.isEmpty()) {
            add(DistrictEntity(id = "-1", name = "Select region first", regionId = ""))
        } else {
            add(DistrictEntity(id = "-1", name = "Select district", regionId = ""))
            addAll(districtList)
        }
    }

    override fun isEnabled(position: Int) = position != 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        if (position == 0) {
            val textView = view.findViewById<TextView>(android.R.id.text1)
            textView.setTextColor(ContextCompat.getColor(context, android.R.color.darker_gray))
        }

        return view
    }

}