package com.maxli.coursegpa

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CourseDao {

    // returns rowId; returns -1 if duplicate (because of IGNORE)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCourse(course: Course): Long

    @Query("SELECT * FROM courses WHERE courseName = :name")
    fun findCourse(name: String): List<Course>

    @Query("DELETE FROM courses WHERE courseName = :name")
    fun deleteCourse(name: String)

    @Query("SELECT * FROM courses")
    fun getAllCourses(): LiveData<List<Course>>
}
