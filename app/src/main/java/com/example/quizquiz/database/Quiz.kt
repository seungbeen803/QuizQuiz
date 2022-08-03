package com.example.quizquiz.database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import org.w3c.dom.DOMStringList

// Entity클래스는 표 즉, 데이터베이스와 관련된 클래스
@Entity(tableName = "quiz")
data class Quiz
 (
    @PrimaryKey(autoGenerate = true)
    // 값이 없을 경우를 대비하여 null을 써줌
    var id: Long? = null,
    // 퀴즈의 종류(OX, N지선다)
    var type: String?,
    // 발문
    var question: String?,
    // 정답
    var answer: String?,
    // 퀴즈의 카테고리
    var category: String?,
    // N지선다 문제의 선택지
    @TypeConverters(StringListTypeConverter::class)
    var guesses: List<String>? = null
// Parcelable : 필요한 데이터 클래스를 소포처럼 만든다, 액티비티 간 이동이 많고 전달할 것이 많으면 사용
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Long::class.java.classLoader) as? Long,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.createStringArrayList()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(type)
        parcel.writeString(question)
        parcel.writeString(answer)
        parcel.writeString(category)
        parcel.writeStringList(guesses)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Quiz> {
        override fun createFromParcel(parcel: Parcel): Quiz {
            return Quiz(parcel)
        }

        override fun newArray(size: Int): Array<Quiz?> {
            return arrayOfNulls(size)
        }
    }

}

class StringListTypeConverter {
    @TypeConverter
    fun stringListToString(stringList: List<String>?): String? {
        return stringList?.joinToString(",")
    }
    @TypeConverter
    fun stringToStringList(string: String?): List<String>? {
        return string?.split(",")?.toList()
    }
}
