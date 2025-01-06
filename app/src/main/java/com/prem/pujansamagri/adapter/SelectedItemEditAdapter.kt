package com.prem.pujansamagri.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.prem.pujansamagri.R
import com.prem.pujansamagri.models.Item

class SelectedItemEditAdapter(var context: Context, private var parcheList: ArrayList<Item>, private val onCheckBoxChanged: (Boolean) -> Unit):
    RecyclerView.Adapter<SelectedItemEditAdapter.ViewHolder>() {

    fun getItemList(): ArrayList<Item> {
        return parcheList.filter { it.isChecked } as ArrayList<Item>
    }

    fun setParchaList(selectedItemList: ArrayList<Item>) {
        parcheList = selectedItemList
        onCheckBoxChanged(parcheList.any { it.isChecked })
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.parche_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, parcheList[position])
        holder.setItemQuantity()
        holder.checkBoxListener()
    }

    override fun getItemCount(): Int {
        return parcheList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        AdapterView.OnItemSelectedListener {
        private var position: Int = 0
        private lateinit var quantity: EditText
        private lateinit var unitSpinner: Spinner
        private lateinit var checkBox: CheckBox


        fun bind(position: Int, item: Item) {
            this.position = position
            this.quantity = itemView.findViewById<EditText>(R.id.quantity_edt)
            this.unitSpinner = itemView.findViewById<Spinner>(R.id.notation_spinner)
            this.checkBox = itemView.findViewById<CheckBox>(R.id.checkbox)

            itemView.findViewById<TextView>(R.id.index_tv).text =
                java.lang.String.valueOf(position + 1)
            itemView.findViewById<TextView>(R.id.title_tv).text = item.name

            quantity.setText(item.quantity)
            checkBox.buttonTintList =
                ColorStateList.valueOf(context.getColor(R.color.vivid_auburn_matte_black))
            checkBox.isChecked = item.isChecked

            val selectedItemPosition =
                context.resources.getStringArray(R.array.quantity_notation).indexOf(item.unit)

            // set Notation List to spinner
            ArrayAdapter.createFromResource(
                itemView.context,
                R.array.quantity_notation,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                // Specify the layout to use when the list of choices appears.
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                // Apply the adapter to the spinner.
                unitSpinner.adapter = adapter
            }
            unitSpinner.setSelection(selectedItemPosition)
            unitSpinner.onItemSelectedListener = this
        }

        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            parcheList[this.position].unit = parent?.selectedItem.toString()

        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
        }


        fun setItemQuantity() {
            quantity.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?,start: Int,count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
                override fun afterTextChanged(s: Editable?) {
                    parcheList[position].quantity = s.toString()
                }
            })
        }

        fun checkBoxListener() {
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                parcheList[position].isChecked = isChecked
                onCheckBoxChanged(parcheList.any { it.isChecked })  // callback
            }
        }
    }
}