package com.prem.pujansamagri.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.prem.pujansamagri.R
import com.prem.pujansamagri.models.Item
import com.prem.pujansamagri.models.UserInfo


class PageAdapter(
    private val context: Context,
    var userInfo: UserInfo?,
    private var itemList: ArrayList<Item>
) :
    RecyclerView.Adapter<PageAdapter.PageViewHolder>() {

    private val PAGE_ITEM_COUNT: Int = 30
    private val pages = ArrayList<String>() // Holds the text content for each page
    private var textSize = 0f

    init {
        pageContent()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PageViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.page_item, parent, false)
        return PageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.binding(position)
    }

    override fun getItemCount(): Int {
        return pages.size
    }

    inner class PageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun binding(position: Int) {
            val title = itemView.findViewById<TextView>(R.id.header_title_tv)
            val userDetail = itemView.findViewById<TextView>(R.id.user_info_tv)
            val content = itemView.findViewById<TextView>(R.id.parche_item_tv)

            title.text = userInfo!!.title
            if (position > 0) {
                title.visibility = View.INVISIBLE
            }

            textSize = content.textSize
            userDetail.text = userInfo!!.name.plus("-").plus(userInfo!!.phoneNo)
            content.text = pages[position]
        }
    }


    private fun pageContent() {
        var pageContentText = ""

        for (count in 0..<this.itemList.size) {

            val item = this.itemList[count]
            var oneLineText = (count + 1).toString().plus(" ").plus(item.name).plus("  ")

            if (item.quantity.isNotEmpty() && item.unit.isNotEmpty()) {
                var tempsize = 70 - (item.unit.length + item.name.length + item.quantity.length)

                while (tempsize > 0) {
                    tempsize--
                    oneLineText += "."
                }
            }

            oneLineText += "  ".plus(item.quantity).plus("  ").plus(item.unit)
            pageContentText += oneLineText.plus("\n")

            if ((count + 1) % PAGE_ITEM_COUNT == 0 || count == this.itemList.size - 1) {
                pages.add(pageContentText)

                pageContentText = ""
            }
        }
    }

    fun getPagesContent(): Pair<Float, ArrayList<String>> {
        return Pair(textSize, pages)
    }

    fun updatePageContent() {
        pages.clear()
        pageContent()
        notifyDataSetChanged()
    }

    fun editUserInfo(userInfo: UserInfo) {
        this.userInfo = userInfo
    }

}
