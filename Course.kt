package com.maxli.coursegpa

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index


@Entity(
    tableName = "courses",
    indices = [Index(value = ["courseName"], unique = true)]
)
class Course {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "courseId")
    var id: Int = 0

    @ColumnInfo(name = "courseName")
    var courseName: String = ""

    @ColumnInfo(name = "creditHour")
    var creditHour: Int = 0

    @ColumnInfo(name = "letterGrade")
    var letterGrade: String = ""

    constructor() {}

    constructor(coursename: String, creditHour: Int, letterGrade: String) {
        this.courseName = coursename
        this.creditHour = creditHour
        this.letterGrade = letterGrade
    }
}
