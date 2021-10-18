package com.example.ufsnavigationassistant.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class RoomDirection(
    var room_direction_id: Int = 0,
    var room_id: Int = 0,
    var entrance: String? = null,
    var directions: String? = null,
): Parcelable
