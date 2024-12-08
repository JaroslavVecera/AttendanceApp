package com.example.attendanceapp

import AttendanceViewModel
import android.content.Context
import android.os.Bundle
import android.widget.Space
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.attendanceapp.model.RowData
import com.example.attendanceapp.ui.theme.AttendanceAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AttendanceAppTheme {
                AttendanceScreen()
            }
        }
    }
}

@Composable
fun AttendanceScreen(viewModel: AttendanceViewModel = viewModel()) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredList = remember(searchQuery) {
        when (searchQuery) {
            "" -> viewModel.data
            else -> viewModel.data.filter {
                it.firstName.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    val spreadsheetId = getSpreadsheetId(LocalContext.current)
    val columns = listOf("First name", "Surname", "Bowling", "Bowling", "Quiz", "Quiz", "Present", "Note")
    val weights = listOf(2f, 2f, 1f, 1f, 1f, 1f, 1f, 2f)
    val data = viewModel.data
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.startPolling(context, spreadsheetId)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        TextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Search") },
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        )

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                columns.forEachIndexed() { index, column ->
                    Row(
                        modifier = Modifier
                            .weight(weights[index])
                    ) {
                        Text(
                            text = column,
                            modifier = Modifier.weight(1f),
                            style = MaterialTheme.typography.bodyLarge
                        )
                        if (column == "Surname") {
                            Button(
                                onClick = {
                                    viewModel.sortBySurname()
                                },
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                Text("Sort")
                            }
                        }
                    }
                }
            }

            AttendanceList(filteredList, viewModel, weights, spreadsheetId)
        }
    }
}



@Composable
fun AttendanceList(data: List<RowData>, viewModel: AttendanceViewModel, weights: List<Float>, spreadsheetId: String) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(data) { index, row ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(top = 16.dp)
            ) {

                Text(
                    text = row.firstName,
                    modifier = Modifier.weight(weights[0])
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = row.surname,
                    modifier = Modifier.weight(weights[1])
                )
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(
                    enabled = false,
                    checked = row.bowling1,
                    onCheckedChange = { newVal ->
                        val updatedRow = row.copy(bowling1 = newVal)
                        viewModel.updateRow(updatedRow)
                        viewModel.sendUpdatedRow(updatedRow, spreadsheetId)
                    },
                    modifier = Modifier.weight(weights[2])
                )
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(
                    checked = row.bowling2,
                    onCheckedChange = { newVal ->
                        val updatedRow = row.copy(bowling2 = newVal)
                        viewModel.updateRow(updatedRow)
                        viewModel.sendUpdatedRow(updatedRow, spreadsheetId)
                    },
                    modifier = Modifier.weight(weights[3])
                )
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(
                    enabled = false,
                    checked = row.quiz1,
                    onCheckedChange = { newVal ->
                        val updatedRow = row.copy(quiz1 = newVal)
                        viewModel.updateRow(updatedRow)
                        viewModel.sendUpdatedRow(updatedRow, spreadsheetId)
                    },
                    modifier = Modifier.weight(weights[4])
                )
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(
                    checked = row.quiz2,
                    onCheckedChange = { newVal ->
                        val updatedRow = row.copy(quiz2 = newVal)
                        viewModel.updateRow(updatedRow)
                        viewModel.sendUpdatedRow(updatedRow, spreadsheetId)
                    },
                    modifier = Modifier.weight(weights[5])
                )
                Spacer(modifier = Modifier.width(8.dp))
                Checkbox(
                    checked = row.present,
                    onCheckedChange = { newVal ->
                        val updatedRow = row.copy(present = newVal)
                        viewModel.updateRow(updatedRow)
                        viewModel.sendUpdatedRow(updatedRow, spreadsheetId)
                    },
                    modifier = Modifier.weight(weights[6])
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = row.note,
                    onValueChange = { newName ->
                        val updatedRow = row.copy(note = newName)
                        viewModel.updateRow(updatedRow)
                        viewModel.sendUpdatedRow(updatedRow, spreadsheetId)
                    },
                    modifier = Modifier.weight(weights[7]),
                    singleLine = true
                )
            }
        }
    }
}

fun getSpreadsheetId(context: Context): String {
    val inputStream = context.resources.openRawResource(R.raw.sheet)
    val json = inputStream.bufferedReader().use { it.readText() }
    val jsonObject = JSONObject(json)
    return jsonObject.getString("id")
}
