package com.example.ufsnavigationassistant.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Building(
    var building_id: Int = 0,
    var category_id: Int = 0,
    var building_name: String? = null,
    var lat_coordinate: Double = 0.0,
    var lon_coordinate: Double = 0.0,
    var description: String? = null,
    var images : @RawValue Array<Images> = emptyArray()
    ): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Building

        if (building_id != other.building_id) return false
        if (category_id != other.category_id) return false
        if (building_name != other.building_name) return false
        if (lat_coordinate != other.lat_coordinate) return false
        if (lon_coordinate != other.lon_coordinate) return false
        if (description != other.description) return false
        if (!images.contentEquals(other.images)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = building_id
        result = 31 * result + category_id
        result = 31 * result + (building_name?.hashCode() ?: 0)
        result = 31 * result + lat_coordinate.hashCode()
        result = 31 * result + lon_coordinate.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + images.contentHashCode()
        return result
    }
}

@Parcelize
data class Images(var url: String? = null): Parcelable
