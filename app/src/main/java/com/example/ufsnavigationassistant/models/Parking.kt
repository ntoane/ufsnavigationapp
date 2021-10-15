package com.example.ufsnavigationassistant.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class Parking(
    var parking_id: Int = 0,
    var category_id: Int = 0,
    var cat_id: Int = 0,
    var parking_name: String? = null,
    var parking_type: String? = null,
    var lat_coordinate: Double = 0.0,
    var lon_coordinate: Double = 0.0,
    var description: String? = null,
    var images : @RawValue Array<Images> = emptyArray()
    ): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Parking

        if (parking_id != other.parking_id) return false
        if (category_id != other.category_id) return false
        if (cat_id != other.cat_id) return false
        if (parking_name != other.parking_name) return false
        if (parking_type != other.parking_type) return false
        if (lat_coordinate != other.lat_coordinate) return false
        if (lon_coordinate != other.lon_coordinate) return false
        if (description != other.description) return false
        if (!images.contentEquals(other.images)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = parking_id
        result = 31 * result + category_id
        result = 31 * result + cat_id
        result = 31 * result + (parking_name?.hashCode() ?: 0)
        result = 31 * result + (parking_type?.hashCode() ?: 0)
        result = 31 * result + lat_coordinate.hashCode()
        result = 31 * result + lon_coordinate.hashCode()
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + images.contentHashCode()
        return result
    }
}

@Parcelize
data class ParkingImages(var url: String? = null): Parcelable
