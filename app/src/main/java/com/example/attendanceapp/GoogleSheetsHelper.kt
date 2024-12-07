package com.example.attendanceapp

import android.content.Context
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

    fun getData(spreadsheetId: String, range: String): List<List<String>> {
        val response: ValueRange = sheetsService.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute()
        return response.getValues().map { s -> s.map { t -> t.toString() } } ?: emptyList()
    }

    fun updateData(spreadsheetId: String, range: String, values: List<List<Any>>) {
        val body = ValueRange().setValues(values)
        sheetsService.spreadsheets().values()
            .update(spreadsheetId, range, body)
            .setValueInputOption("RAW")
            .execute()
    }
}

