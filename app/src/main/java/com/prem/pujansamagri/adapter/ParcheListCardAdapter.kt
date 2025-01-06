package com.prem.pujansamagri.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.prem.pujansamagri.R
import com.prem.pujansamagri.activity.ParchaCanvas
import com.prem.pujansamagri.service.LocalParcheStorageImpl

class ParcheListCardAdapter(var context: Context, private var parcheList: ArrayList<String>) :
    RecyclerView.Adapter<ParcheListCardAdapter.ViewHolder>() {

    val localParcheStorageImpl = LocalParcheStorageImpl(context, "Parche")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.parche_list_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(parcheList[position])
    }

    override fun getItemCount(): Int {
        return parcheList.size;
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(fileParcheName: String) {
            val titleTv = itemView.findViewById<TextView>(R.id.title_tv)
            val backgroundIV = itemView.findViewById<ImageView>(R.id.background_imv)
            when (fileParcheName) {
                "SUNDARKAND" -> {
                    titleTv.text = context.resources.getString(R.string.sundarkand)
                    backgroundIV.background = AppCompatResources.getDrawable(context, R.drawable.sundarkand)
                }

                "RAMAYAN" -> {
                    titleTv.text = context.resources.getString(R.string.ramayan)
                    backgroundIV.background = AppCompatResources.getDrawable(context, R.drawable.ramayan)
                }

                "MATA" -> {
                    titleTv.text = context.resources.getString(R.string.mata)
                    backgroundIV.background = AppCompatResources.getDrawable(context, R.drawable.mata)
                }

                "HAVAN" -> {
                    titleTv.text = context.resources.getString(R.string.havan)
                    backgroundIV.background = AppCompatResources.getDrawable(context, R.drawable.havan)
                }

                "JUGAT" -> {
                    titleTv.text = context.resources.getString(R.string.jugat)
                    backgroundIV.background =
                        AppCompatResources.getDrawable(context, R.drawable.garud_puran)
                }

                "PATA" -> {
                    titleTv.text = context.resources.getString(R.string.pata)
                    backgroundIV.background = AppCompatResources.getDrawable(context, R.drawable.pata)
                }

                "EKADASHI" -> {
                    titleTv.text = context.resources.getString(R.string.ekadashi)
                    backgroundIV.background = AppCompatResources.getDrawable(context, R.drawable.ekadashi)
                }

                "RISHI_PANCHMI" -> {
                    titleTv.text = context.resources.getString(R.string.rishi_panchmi)
                    backgroundIV.background = AppCompatResources.getDrawable(context, R.drawable.rishi_panchami)
                }

                "SATYANARAYAN" -> {
                    titleTv.text = context.resources.getString(R.string.satyanarayan)
                    backgroundIV.background = AppCompatResources.getDrawable(context, R.drawable.vishnu)
                }

                else -> {
                    titleTv.text = fileParcheName
                    backgroundIV.setBackgroundColor(context.getColor(R.color.jacarta))
                }
            }

            itemView.findViewById<ConstraintLayout>(R.id.card).setOnClickListener {

                val parchaContentPair = localParcheStorageImpl.getParche(fileParcheName)
                if (parchaContentPair != null) {
                    val intent = Intent(itemView.context, ParchaCanvas::class.java).apply {
                        putParcelableArrayListExtra("selectedItemList", parchaContentPair.second)
                        putExtra("PARCHA_NAME", fileParcheName)
                        putExtra("userinfo", parchaContentPair.first)
                    }
                    itemView.context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Parcha is not Found!", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
}