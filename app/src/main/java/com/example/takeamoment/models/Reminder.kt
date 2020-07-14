package com.example.takeamoment.models

import android.os.Parcel
import android.os.Parcelable

data class Reminder(
    val user: String = "",
    val myDateTime: String = "",
    val momName: String = "",
    val momDateTime: String = "",
    var documentId: String = ""
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int)= with(parcel) {
        parcel.writeString(user)
        parcel.writeString(myDateTime)
        parcel.writeString(momName)
        parcel.writeString(momDateTime)
        parcel.writeString(documentId)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Reminder> {
        override fun createFromParcel(parcel: Parcel): Reminder {
            return Reminder(parcel)
        }

        override fun newArray(size: Int): Array<Reminder?> {
            return arrayOfNulls(size)
        }
    }
}