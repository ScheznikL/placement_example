package com.endofjanuary.placement_example.data.room

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "models",
    indices = [
        Index("id", unique = true)
    ]
)
data class ModelEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    //val modelInstance: ModelInstance,
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val modelInstance: ByteArray,

    val modelPath: String,
    val modelImageUrl: String,
    val modelDescription: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.createByteArray()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeByteArray(modelInstance)
        parcel.writeString(modelPath)
        parcel.writeString(modelImageUrl)
        parcel.writeString(modelDescription)
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ModelEntity

        if (!modelInstance.contentEquals(other.modelInstance)) return false

        return true
    }

    override fun hashCode(): Int {
        return modelInstance.contentHashCode()
    }

    companion object CREATOR : Parcelable.Creator<ModelEntity> {
        override fun createFromParcel(parcel: Parcel): ModelEntity {
            return ModelEntity(parcel)
        }

        override fun newArray(size: Int): Array<ModelEntity?> {
            return arrayOfNulls(size)
        }
    }
}