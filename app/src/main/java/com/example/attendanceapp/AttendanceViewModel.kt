import android.content.Context
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendanceapp.GoogleSheetsHelper
import com.example.attendanceapp.R
import com.example.attendanceapp.model.RowData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class AttendanceViewModel() : ViewModel() {
    private val _data: MutableList<RowData> = mutableStateListOf()

    val data: List<RowData> get() = _data

    suspend fun fetchDataFromSheet(context: Context): List<RowData> {
        val googleSheetsHelper = GoogleSheetsHelper(context = context)

        val spreadsheetId = getSpreadsheetId(context)
        val fetchedData = googleSheetsHelper.getData(spreadsheetId)
        return fetchedData
    }

    fun startPolling(context: Context) {
        viewModelScope.launch {
            while (true) {
                withContext(Dispatchers.IO) {
                    val fetchedData = fetchDataFromSheet(context)
                    _data.clear()
                    _data.addAll(fetchedData)
                }
                delay(5000)
            }
        }
    }

    fun updateRow(index: Int, newRowData: RowData) {
        _data[index] = newRowData
    }

    fun sendUpdatedRow(rowData: RowData) {
        // Implementace pro odeslání dat
    }

    private fun getSpreadsheetId(context: Context): String {
        val inputStream = context.resources.openRawResource(R.raw.sheet)
        val json = inputStream.bufferedReader().use { it.readText() }
        val jsonObject = JSONObject(json)
        return jsonObject.getString("id")
    }
}
