package dev.goblingroup.uzworks.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import dev.goblingroup.uzworks.database.entity.RegionEntity

class RegionAdapter(
    context: Context,
    private val regionList: List<RegionEntity>
) : ArrayAdapter<RegionEntity>(
    context,
    android.R.layout.simple_dropdown_item_1line,
    regionList
) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        val region = getItem(position)
        val textView = view.findViewById<TextView>(android.R.id.text1)
        textView.text = region?.name
        return view
    }

}