package com.prem.pujansamagri.models

import android.content.res.Resources
import android.os.Parcel
import android.os.Parcelable
import com.prem.pujansamagri.R

data class Item(
    val name: String,
    var quantity: String = "0",
    var unit: String = Resources.getSystem().getStringArray(R.array.quantity_notation)[0],
    var isChecked: Boolean
) : Parcelable {

    constructor(name: String, isChecked: Boolean = false) : this(name, "0", "नग", isChecked)

    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readByte() != 0.toByte()
    )


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(quantity)
        parcel.writeString(unit)
        parcel.writeByte(if (isChecked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }

}
