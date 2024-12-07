package com.example.attendanceapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.attendanceapp.ui.theme.AttendanceAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var data by remember { mutableStateOf<List<List<String>>?>(null) }

            val pollingInterval = 5_000L

            val spreadsheetId = getSpreadsheetId(this)
            LaunchedEffect(Unit) {
                CoroutineScope(Dispatchers.IO).launch {
                    while (true) {
                        try {
                            val googleSheetsHelper =
                                GoogleSheetsHelper(context = applicationContext)

                            val range = "Data!A:C"
                            val fetchedData = googleSheetsHelper.getData(spreadsheetId, range)

                            withContext(Dispatchers.Main) {
                                data = fetchedData
                            }

                            delay(pollingInterval)
                        } catch (e: Exception) {
                            Log.d("TAG", e.message.toString())
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    applicationContext,
                                    "Error loading data",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    }
                }
            }

            MaterialTheme {
                if (data == null) {
                    Text("Loading data...")
                } else {
                    DataList(data = data ?: emptyList())
                }
            }
        }
    }
}

@Composable
fun DataList(data: List<List<String>>) {
    LazyColumn {
        itemsIndexed(data, key = { index, _ -> index }) { index, row ->
            RowItem(row = row)
        }
    }
}

@Composable
fun RowItem(row: List<String>) {
    Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        TextField(
            value = row[0],
            onValueChange = { },
            modifier = Modifier.weight(1f)
        )
        Checkbox(
            checked = row[1] == "1",
            onCheckedChange = { }
        )
    }
}

fun getSpreadsheetId(context: Context): String {
    val inputStream = context.resources.openRawResource(R.raw.sheet)
    val json = inputStream.bufferedReader().use { it.readText() }
    val jsonObject = JSONObject(json)
    return jsonObject.getString("id")
}