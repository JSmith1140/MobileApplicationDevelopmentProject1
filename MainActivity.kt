// ===============================
// File: app/src/main/java/com/maxli/coursegpa/MainActivity.kt
// ===============================
package com.maxli.coursegpa

import android.app.Application
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.maxli.coursegpa.ui.theme.CourseTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CourseTheme {
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
                                LocalContext.current.applicationContext as Application
                            )
                        )
                        ScreenSetup(viewModel)
                    }
                }
            }
        }
    }
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
    var letterGrade by remember { mutableStateOf("") }

    var calculatedGPA by remember { mutableDoubleStateOf(-1.0) }
    var searching by remember { mutableStateOf(false) }

    val navyButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondary,   // navy
        contentColor = MaterialTheme.colorScheme.onSecondary
    )
    val lightBlueButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary,     // light blue
        contentColor = MaterialTheme.colorScheme.onPrimary
    )
    val orangeButtonColors = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.tertiary,    // orange
        contentColor = MaterialTheme.colorScheme.onTertiary
    )

    Column(
        horizontalAlignment = CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
    ) {
        // ===== Header with logo + gradient-ish accent (navy bar + orange divider) =====
        HeaderSection()

        // ===== Inputs =====
        CustomTextField(
            title = "Course Name",
            textState = courseName,
            onTextChange = { courseName = it },
            keyboardType = KeyboardType.Text
        )

        CustomTextField(
            title = "Credit Hour",
            textState = courseCreditHour,
            onTextChange = { courseCreditHour = it },
            keyboardType = KeyboardType.Number
        )

        CustomTextField(
            title = "Letter Grade",
            textState = letterGrade,
            onTextChange = { letterGrade = it },
            keyboardType = KeyboardType.Text
        )

        // ===== Buttons =====
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            ) {
                Button(
                    onClick = {
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
                    },
                    colors = navyButtonColors
                ) { Text("Add") }

                Button(
                    onClick = {
                        searching = true
                        viewModel.findCourse(courseName)
                    },
                    colors = navyButtonColors
                ) { Text("Sch") }

                Button(
                    onClick = {
                        searching = false
                        viewModel.deleteCourse(courseName)
                    },
                    colors = navyButtonColors
                ) { Text("Del") }

                Button(
                    onClick = {
                        searching = false
                        courseName = ""
                        courseCreditHour = ""
                        letterGrade = ""
                    },
                    colors = navyButtonColors
                ) { Text("Clr") }

                // Accent button
                Button(
                    onClick = {
                        calculatedGPA = calculateGPA2(allCourses)
                    },
                    colors = orangeButtonColors
                ) { Text("GPA") }
            }
        }

        // ===== GPA display =====
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Current GPA",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = if (calculatedGPA < 0) "--" else "%.2f".format(calculatedGPA),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary // orange highlight
                )
            }
        }

        // ===== Course list =====
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            val list = if (searching) searchResults else allCourses

            item {
                TitleRow(
                    head1 = "ID",
                    head2 = "Course",
                    head3 = "Credit",
                    head4 = "Grade"
                )
            }

            items(list) { course ->
                CourseRow(
                    id = course.id,
                    name = course.courseName,
                    creditHour = course.creditHour,
                    letterGrade = course.letterGrade
                )
            }
        }
    }
}

@Composable
private fun HeaderSection() {
    // Logo image
    Image(
        painter = painterResource(id = R.drawable.rwulogo),
        contentDescription = "Header Image",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentScale = ContentScale.Crop
    )

    // Navy bar with title + orange accent divider
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.secondary)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(
            text = "Course GPA Tracker",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSecondary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
    }

    Divider(
        color = MaterialTheme.colorScheme.tertiary, // orange
        thickness = 3.dp
    )
}

// GPA calculation functionality
private fun calculateGPA2(courses: List<Course>): Double {
    val gradePoints = mapOf(
        "A" to 4.0, "A-" to 3.67,
        "B+" to 3.33, "B" to 3.0, "B-" to 2.67,
        "C+" to 2.33, "C" to 2.0, "C-" to 1.67,
        "D+" to 1.33, "D" to 1.0, "D-" to 0.67,
        "F" to 0.0
    )
    val totalCreditHours = courses.sumOf { it.creditHour }
    if (totalCreditHours == 0) return 0.0

    val totalPoints = courses.sumOf {
        it.creditHour * (gradePoints[it.letterGrade.uppercase()] ?: 0.0)
    }
    return totalPoints / totalCreditHours
}

@Composable
fun TitleRow(head1: String, head2: String, head3: String, head4: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.secondary) // navy
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 6.dp)
        ) {
            val headerColor = MaterialTheme.colorScheme.onSecondary
            Text(head1, color = headerColor, modifier = Modifier.weight(0.12f), fontWeight = FontWeight.Bold)
            Text(head2, color = headerColor, modifier = Modifier.weight(0.38f), fontWeight = FontWeight.Bold)
            Text(head3, color = headerColor, modifier = Modifier.weight(0.20f), fontWeight = FontWeight.Bold)
            Text(head4, color = headerColor, modifier = Modifier.weight(0.20f), fontWeight = FontWeight.Bold)
        }

        Divider(
            color = MaterialTheme.colorScheme.tertiary, // orange underline
            thickness = 2.dp
        )
    }
}

@Composable
fun CourseRow(id: Int, name: String, creditHour: Int, letterGrade: String) {
    // A tiny bit of styling: card row + grade highlight
    val grade = letterGrade.uppercase()
    val gradeColor = when {
        grade.startsWith("A") -> MaterialTheme.colorScheme.tertiary // orange pop for A-range
        grade.startsWith("B") -> MaterialTheme.colorScheme.secondary
        grade.startsWith("C") -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp)
        ) {
            Text(id.toString(), modifier = Modifier.weight(0.12f), color = MaterialTheme.colorScheme.onSurface)
            Text(name, modifier = Modifier.weight(0.38f), color = MaterialTheme.colorScheme.onSurface)
            Text(creditHour.toString(), modifier = Modifier.weight(0.20f), color = MaterialTheme.colorScheme.onSurface)
            Text(
                letterGrade,
                modifier = Modifier.weight(0.20f),
                color = gradeColor,
                fontWeight = FontWeight.Bold
            )
        }
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
        onValueChange = onTextChange,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        singleLine = true,
        label = { Text(title) },
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 6.dp)
            .fillMaxWidth(),
        textStyle = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        ),
        colors = OutlinedTextFieldDefaults.colors(

            focusedBorderColor = MaterialTheme.colorScheme.tertiary,   // orange focus
            focusedLabelColor = MaterialTheme.colorScheme.secondary,   // navy label
            cursorColor = MaterialTheme.colorScheme.secondary,         // navy cursor
            unfocusedBorderColor = MaterialTheme.colorScheme.primary,  // light blue
            unfocusedLabelColor = MaterialTheme.colorScheme.secondary  // navy
        )
    )
}

class MainViewModelFactory(val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(application) as T
    }
}
