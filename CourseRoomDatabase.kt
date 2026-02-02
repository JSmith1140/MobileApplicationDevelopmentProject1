package com.maxli.coursegpa

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [(Course::class)], version = 2)
abstract class CourseRoomDatabase: RoomDatabase() {

    abstract fun courseDao(): CourseDao

    companion object {

        private var INSTANCE: CourseRoomDatabase? = null

        fun getInstance(context: Context): CourseRoomDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        CourseRoomDatabase::class.java,
                        "course_database"
                    ).fallbackToDestructiveMigration()
                        .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}
