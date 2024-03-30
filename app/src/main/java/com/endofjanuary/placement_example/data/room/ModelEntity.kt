package com.endofjanuary.placement_example.data.room

import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import androidx.annotation.RequiresApi
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
    val isFromText: Boolean,
    val isRefine: Boolean,

) : Parcelable {
    @RequiresApi(Build.VERSION_CODES.Q)
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.createByteArray()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readBoolean(),
        parcel.readBoolean(),
    ) {
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeByteArray(modelInstance)
        parcel.writeString(modelPath)
        parcel.writeString(modelImageUrl)
        parcel.writeString(modelDescription)
        parcel.writeBoolean(isFromText)
        parcel.writeBoolean(isRefine)
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
        @RequiresApi(Build.VERSION_CODES.Q)
        override fun createFromParcel(parcel: Parcel): ModelEntity {
            return ModelEntity(parcel)
        }

        override fun newArray(size: Int): Array<ModelEntity?> {
            return arrayOfNulls(size)
        }
    }
}