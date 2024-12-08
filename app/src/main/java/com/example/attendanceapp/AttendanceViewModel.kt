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

class AttendanceViewModel() : ViewModel() {
    private val _data: MutableList<RowData> = mutableStateListOf()

    val data: List<RowData> get() = _data
    var googleSheetsHelper: GoogleSheetsHelper? = null

    suspend fun fetchDataFromSheet(context: Context, spreadsheetId: String): List<RowData> {
        googleSheetsHelper = GoogleSheetsHelper(context = context)

        val fetchedData = googleSheetsHelper!!.getData(spreadsheetId)
        return fetchedData
    }

    fun startPolling(context: Context, spreadsheetId: String) {
        viewModelScope.launch {
            while (true) {
                withContext(Dispatchers.IO) {
                    val fetchedData = fetchDataFromSheet(context, spreadsheetId)
                    _data.clear()
                    _data.addAll(fetchedData)
                }
                delay(5000)
            }
        }
    }

    fun updateRow(newRowData: RowData) {
        println("sort" + newRowData.index)
        _data[newRowData.index] = newRowData
    }

    fun sendUpdatedRow(rowData: RowData, spreadsheetId: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                googleSheetsHelper!!.updateRow(spreadsheetId, rowData.index, rowData)
            }
        }
    }

    fun sortBySurname() {
        _data.sortBy { it.surname }
    }
}
