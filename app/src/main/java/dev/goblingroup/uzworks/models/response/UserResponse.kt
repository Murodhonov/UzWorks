package dev.goblingroup.uzworks.models.response

import android.os.Parcel
import android.os.Parcelable

data class UserResponse(
    val birthDate: String,
    val email: String,
    val firstName: String,
    val gender: String,
    val id: String,
    val lastName: String,
    val mobileId: String,
    val phoneNumber: String,
    val userName: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(birthDate)
        parcel.writeString(email)
        parcel.writeString(firstName)
        parcel.writeString(gender)
        parcel.writeString(id)
        parcel.writeString(lastName)
        parcel.writeString(mobileId)
        parcel.writeString(phoneNumber)
        parcel.writeString(userName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<UserResponse> {
        override fun createFromParcel(parcel: Parcel): UserResponse {
            return UserResponse(parcel)
        }

        override fun newArray(size: Int): Array<UserResponse?> {
            return arrayOfNulls(size)
        }
    }
}