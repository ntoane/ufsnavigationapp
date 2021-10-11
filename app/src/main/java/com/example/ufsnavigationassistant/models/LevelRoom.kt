package com.example.ufsnavigationassistant.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LevelRoom(
    var room_id: Int = 0,
    var building_id: Int = 0,
    var building_name: String? = null,
    var room_name: String? = null,
    var floor_num: Int = 0,
    var description: String? =null
): Parcelable
