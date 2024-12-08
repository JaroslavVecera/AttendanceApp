package com.example.attendanceapp

import android.content.Context
import com.example.attendanceapp.model.RowData
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.http.javanet.NetHttpTransport
import java.io.InputStream

class GoogleSheetsHelper(context: Context) {

    private val sheetsService: Sheets

    init {
        val inputStream: InputStream = context.resources.openRawResource(R.raw.key)
        val credentials = GoogleCredentials.fromStream(inputStream)
            .createScoped(listOf("https://www.googleapis.com/auth/spreadsheets"))
        sheetsService = Sheets.Builder(
            NetHttpTransport(),
            GsonFactory.getDefaultInstance(),
            HttpCredentialsAdapter(credentials)
        ).setApplicationName("AttendanceApp")
            .build()
    }

    fun getData(spreadsheetId: String): List<RowData> {
        val response: ValueRange = sheetsService.spreadsheets().values()
            .get(spreadsheetId, "Data!A:H")
            .execute()

        val values = response.getValues() ?: emptyList()
        var i = 0
        return values.mapNotNull { row ->
            if (row.size >= 8) {
                try {
                    RowData(
                        i++,
                        row[0] as String,
                        row[1] as String,
                        row[2] as String == "1",
                        row[3] as String == "1",
                        row[4] as String == "1",
                        row[5] as String == "1",
                        row[6] as String == "1",
                        row[7] as String
                    )
                } catch (e: Exception) {
                    null
                }
            } else {
                null
            }
        }
    }

    fun updateRow(spreadsheetId: String, row: Int, updatedRow: RowData) {
        val index = row + 1
        val range = "Data!A$index:H$index"
        val valueRange = ValueRange().setValues(listOf(listOf(
            updatedRow.firstName,
            updatedRow.surname,
            if (updatedRow.bowling1) 1 else 0,
            if (updatedRow.bowling2) 1 else 0,
            if (updatedRow.quiz1) 1 else 0,
            if (updatedRow.quiz2) 1 else 0,
            if (updatedRow.present) 1 else 0,
            updatedRow.note)))

        val updateRequest = sheetsService.spreadsheets().values()
            .update(spreadsheetId, range, valueRange)
            .setValueInputOption("RAW")
        updateRequest.execute()
    }
}

