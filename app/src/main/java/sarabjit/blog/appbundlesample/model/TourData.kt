package sarabjit.blog.appbundlesample.model

import android.os.Parcel
import android.os.Parcelable

import sarabjit.blog.appbundlesample.R

data class TourData(
    val name: String?,
    val description: String?,
    val image: Int,
    val cost: Int,
    val days: Int,
    val inviteLetter: String?
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString(),
        source.readString(),
        source.readInt(),
        source.readInt(),
        source.readInt(),
        source.readString()
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(name)
        writeString(description)
        writeInt(image)
        writeInt(cost)
        writeInt(days)
        writeString(inviteLetter)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<TourData> = object : Parcelable.Creator<TourData> {
            override fun createFromParcel(source: Parcel): TourData = TourData(source)
            override fun newArray(size: Int): Array<TourData?> = arrayOfNulls(size)
        }
    }
}


fun getData(): List<TourData> {
    return listOf(
        TourData(
            "United Kingdom", "Enjoy your Stay in United Kingdom", R.drawable.unitedkingdom_flag,
            20_000_00, 5, "Invite Letter for United Kingdom"
        ),
        TourData(
            "Greece", "Enjoy your Stay in Greece", R.drawable.greece_flag, 30_00_000,
            8, "Invite letter for Greece"
        ),
        TourData(
            "france", "Enjoy your Stay in france", R.drawable.france_flag, 40_00_000,
            11, "Invite letter for Greece"
        )
    )
}