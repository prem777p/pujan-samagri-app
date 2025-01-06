package com.prem.pujansamagri.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton.OnCheckedChangeListener
import com.prem.pujansamagri.R
import com.prem.pujansamagri.models.Item

class ParcheListAdapter(
    var context: Context,
    private var parcheList: ArrayList<Item>,
    private val onCheckBoxChanged: (Boolean) -> Unit
) :
    RecyclerView.Adapter<ParcheListAdapter.ViewHolder>() {

    private var filteredList = parcheList

    init {
        onCheckBoxChange()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.parche_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, filteredList[position])
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(position: Int, item: Item) {

            itemView.findViewById<TextView>(R.id.index_tv).text =
                java.lang.String.valueOf(position + 1)
            itemView.findViewById<TextView>(R.id.title_tv).text = item.name
            itemView.findViewById<EditText>(R.id.quantity_edt).visibility = View.GONE
            itemView.findViewById<Spinner>(R.id.notation_spinner).visibility = View.GONE

            val checkBox = itemView.findViewById<CheckBox>(R.id.checkbox)

            checkBox.buttonTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, R.color.jacarta_matte_black))

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                item.isChecked = isChecked
                onCheckBoxChanged(parcheList.any { it.isChecked })  // callback
            }

            checkBox.isChecked = item.isChecked

        }
    }

    fun filter(query: String) {
        filteredList = if (query.isEmpty()) {
            parcheList
        } else {
            ArrayList(parcheList.filter { it.name.contains(query, ignoreCase = true) })
        }
        notifyDataSetChanged()
    }

    fun getCheckedItemList(): ArrayList<Item> =
        parcheList.filter { it.isChecked } as ArrayList<Item>

    fun onCheckBoxChange(){
        onCheckBoxChanged(parcheList.any { it.isChecked })
    }
}