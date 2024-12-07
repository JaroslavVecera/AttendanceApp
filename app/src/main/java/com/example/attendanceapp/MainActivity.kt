package com.example.attendanceapp

import AttendanceViewModel
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.attendanceapp.model.RowData
import com.example.attendanceapp.ui.theme.AttendanceAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AttendanceAppTheme {
                AttendanceList()
            }
        }
    }
}

@Composable
fun AttendanceList(viewModel: AttendanceViewModel = viewModel()) {
    val data = viewModel.data
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.startPolling(context)
    }

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        itemsIndexed(data) { index, row ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .padding(top = 16.dp)
            ) {
                TextField(
                    value = row.firstName,
                    onValueChange = { newName ->
                        val updatedRow = row.copy(firstName = newName)
                        viewModel.updateRow(index, updatedRow)
                        viewModel.sendUpdatedRow(updatedRow)
                    },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))

                Checkbox(
                    checked = row.present,
                    onCheckedChange = { newPresent ->
                        val updatedRow = row.copy(present = newPresent)
                        viewModel.updateRow(index, updatedRow)
                        viewModel.sendUpdatedRow(updatedRow)
                    }
                )
            }
        }
    }
}
