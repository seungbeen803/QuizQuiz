package com.example.quizquiz.database

import android.content.Context
import androidx.room.*

// Annotation을 붙여줘야만 자동으로 구현을 한다
// Data Access Object
@Dao
interface QuizDAO {
    @Insert
    fun insert(quiz: Quiz): Long
    @Update
    fun update(quiz: Quiz)
    @Delete
    fun delete(quize: Quiz)
    @Query("SELECT * FROM quiz")
    fun getAll(): List<Quiz>
}

@Database(entities = [Quiz::class], version = 1)
// 정의 해놓은 converter가 있으면 작성해줘야함(내가 작성한 건 하나이므로 한 개만 작성)
@TypeConverters(StringListTypeConverter::class)
abstract class QuizDatabase : RoomDatabase() {
    abstract fun quizDAO(): QuizDAO

    // 클래스 메서드랑, 상수 만들 때 사용(정적 메서드)
    companion object {
        private var INSTANCE: QuizDatabase? = null

        // 싱글턴 패턴
        fun getInstance(context: Context): QuizDatabase {
            if (INSTANCE == null) {
                // Room -> 데이터베이스 객체를 만듦
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    QuizDatabase::class.java, "database.db").build()
            }
            return INSTANCE!!
        }
    }
}