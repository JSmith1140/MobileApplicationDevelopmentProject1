package com.maxli.coursegpa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.livedata.observeAsState
import kotlin.random.Random

import com.maxli.coursegpa.ui.theme.CourseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CourseTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val owner = LocalViewModelStoreOwner.current

                    owner?.let {
                        val viewModel: MainViewModel = viewModel(
                            it,
                            "MainViewModel",
                            MainViewModelFactory(
                                LocalContext.current.applicationContext
                                        as Application)
                        )
                        ScreenSetup(viewModel)
                    }
                }
            }
        }
    }

    // Adding the button to your layout - Adapt this to your layout configuration
    // For example, if you have a LinearLayout, you can add the button to it
    // val layout: LinearLayout = findViewById(R.id.your_layout_id)
    // layout.addView(gpaButton)
    // GPA calculation function
//        private fun calculateGPA(): Double {
//            // Retrieve courses data from database - Replace with actual data retrieval
//            val courses = listOf(
//                Triple("Course1", 3, "A"), // Example data: CourseName, CreditHour, LetterGrade
//                Triple("Course2", 4, "B"),
//                Triple("Course3", 2, "C")
//            )
//            val gradePoints = mapOf("A" to 4.0, "B" to 3.0, "C" to 2.0, "D" to 1.0, "F" to 0.0)
//            val totalCreditHours = courses.sumOf { it.second }
//            val totalPoints = courses.sumOf { it.second * (gradePoints[it.third] ?: 0.0) }
//
//            return if (totalCreditHours > 0) totalPoints / totalCreditHours else 0.0
//        }

}

@Composable
fun ScreenSetup(viewModel: MainViewModel) {
    val allCourses by viewModel.allCourses.observeAsState(listOf())
    val searchResults by viewModel.searchResults.observeAsState(listOf())

    MainScreen(
        allCourses = allCourses,
        searchResults = searchResults,
        viewModel = viewModel
    )

}

@Composable
fun MainScreen(
    allCourses: List<Course>,
    searchResults: List<Course>,
    viewModel: MainViewModel
) {
    var courseName by remember { mutableStateOf("") }
    var courseCreditHour by remember { mutableStateOf("") }
    var letterGrade by remember {
        mutableStateOf("")
    }

    var calculatedGPA by remember {
        mutableDoubleStateOf(-1.0)
    }

    var searching by remember { mutableStateOf(false) }

    val onCourseTextChange = { text : String ->
        courseName = text
    }

    val onCreditHourTextChange = { text : String ->
        courseCreditHour = text
    }

    val onLetterGradeTextChange = { text : String ->
        letterGrade = text
    }


    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        CustomTextField(
            title = "Course Name",
            textState = courseName,
            onTextChange = onCourseTextChange,
            keyboardType = KeyboardType.Text
        )

        CustomTextField(
            title = "Credit Hour",
            textState = courseCreditHour,
            onTextChange = onCreditHourTextChange,
            keyboardType = KeyboardType.Number
        )

        CustomTextField(
            title = "Letter Grade",
            textState = letterGrade,
            onTextChange = onLetterGradeTextChange,
            keyboardType = KeyboardType.Text
        )


        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            Button(onClick = {
                if (courseCreditHour.isNotEmpty()) {
                    viewModel.insertCourse(
                        Course(
                            courseName,
                            courseCreditHour.toInt(),
                            letterGrade
                        )
                    )
                    searching = false
                }
            }) {
                Text("Add")
            }

            Button(onClick = {
                searching = true
                viewModel.findCourse(courseName)
            }) {
                Text("Sch")
            }

            Button(onClick = {
                searching = false
                viewModel.deleteCourse(courseName)
            }) {
                Text("Del")
            }

            Button(onClick = {
                searching = false
                courseName = ""
                courseCreditHour = ""
                letterGrade = ""
            }// ,
//                modifier = Modifier.size(width = 80.dp, height = 50.dp)

            ) {
                Text("Clr")
            }

            Button(onClick = {

                val gpa = calculateGPA2(allCourses)
                // Display the calculated GPA - Implement the display logic as needed
//                    Toast.makeText(this, "GPA: $gpa", Toast.LENGTH_LONG).show()
                println("GPA is: $gpa")
                calculatedGPA = gpa


            }) {
                Text("GPA")
            }
        }

        Row {
            Spacer(modifier = Modifier.weight(1f))
            Text("GPA: %.2f".format(calculatedGPA))

        }
        LazyColumn(
            Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            val list = if (searching) searchResults else allCourses

            item {
                TitleRow(head1 = "ID", head2 = "Course", head3 = "CreditHour", head4 = "LetterGrade")
            }

            items(list) { course ->
                CourseRow(id = course.id, name = course.courseName,
                    creditHour = course.creditHour,
                    letterGrade = course.letterGrade)
            }
        }
    }
}

// GPA calculation functionality
private fun calculateGPA2(courses: List<Course>): Double {

    val gradePoints = mapOf("A" to 4.0, "A-" to 3.67, "B+" to 3.33, "B" to 3.0, "B-" to 2.67, "C+" to 2.33, "C" to 2.0, "C-" to 1.67, "D+" to 1.33, "D" to 1.0, "D-" to 0.67, "F" to 0.0)
    val totalCreditHours = courses.sumOf { it.creditHour }
    if (totalCreditHours == 0) return 0.0
    val totalPoints = courses.sumOf {
        it.creditHour * (gradePoints[it.letterGrade.uppercase()] ?: 0.0)
    }

    return if (totalCreditHours > 0) totalPoints / totalCreditHours else 0.0
}

@Composable
fun TitleRow(head1: String, head2: String, head3: String, head4: String) {
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primary)
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(head1, color = Color.White,
            modifier = Modifier
                .weight(0.1f))
        Text(head2, color = Color.White,
            modifier = Modifier
                .weight(0.2f))
        Text(head3, color = Color.White,
            modifier = Modifier.weight(0.2f))
        Text(head4, color = Color.White,
            modifier = Modifier.weight(0.2f))
    }
}

@Composable
fun CourseRow(id: Int, name: String, creditHour: Int, letterGrade: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
    ) {
        Text(id.toString(), modifier = Modifier
            .weight(0.1f))
        Text(name, modifier = Modifier.weight(0.2f))
        Text(creditHour.toString(), modifier = Modifier.weight(0.2f))
        Text(letterGrade, modifier = Modifier.weight(0.2f))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomTextField(
    title: String,
    textState: String,
    onTextChange: (String) -> Unit,
    keyboardType: KeyboardType
) {
    OutlinedTextField(
        value = textState,
        onValueChange = { onTextChange(it) },
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType
        ),
        singleLine = true,
        label = { Text(title)},
        modifier = Modifier.padding(10.dp),
        textStyle = TextStyle(fontWeight = FontWeight.Bold,
            fontSize = 30.sp)
    )
}

class MainViewModelFactory(val application: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application) as T
    }
}
