package com.example.sms2

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.telephony.SmsManager
import android.util.Log
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MyBackgroundService : Service() {

    private val coroutineScope = CoroutineScope(Dispatchers.IO)
    private var isRunning = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isRunning) {
            isRunning = true
            coroutineScope.launch {
                while (isRunning) {
                    fetchDataAndSendSMS()
                    delay(1000) // Delay for 1 minute (60000ms) before next fetch
                }
            }
        }
        return START_STICKY
    }

    private suspend fun fetchDataAndSendSMS() {
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://47pz9bp14f.execute-api.me-south-1.amazonaws.com/Prod/otp")
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "GET"

                val inputStream = httpURLConnection.inputStream
                val result = inputStream.bufferedReader().use { it.readText() }

                val jsonResponse = JSONObject(result)

                if (jsonResponse.has("body")) {
                    val body = jsonResponse.getString("body")
                    val jsonArray = JSONArray(body)

                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = jsonArray.getJSONObject(i)
                        val id = jsonObject.getLong("id")
                        val phoneNumber = jsonObject.getString("phone")
                        val otp = jsonObject.getString("otp")

                        val smsSent = sendSMS(phoneNumber, "Your OTP: $otp")
                        val newStatus = if (smsSent) "sentsms" else "failedsms"
                        updateRowStatus(id, newStatus)
                    }
                    withContext(Dispatchers.Main) {
                        showToast("All SMS sent successfully.")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("Error: 'body' key missing in JSON response.")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showToast("Error: ${e.message}")
                }
            }
        }
    }

    private fun sendSMS(phoneNumber: String, message: String): Boolean {
        val localPhoneNumber = phoneNumber.replaceFirst("^92".toRegex(), "0")
        Log.d("SMS", "Sending SMS to: $localPhoneNumber")

        val smsManager = SmsManager.getDefault()
        return try {
            smsManager.sendTextMessage(localPhoneNumber, null, message, null, null)
            true
        } catch (e: Exception) {
            Log.e("SMS", "SMS Failed to Send to $localPhoneNumber", e)
            false
        }
    }

    private suspend fun updateRowStatus(rowId: Long, newStatus: String) {
        withContext(Dispatchers.IO) {
            try {
                val url = URL("https://g9lutwgepj.execute-api.me-south-1.amazonaws.com/prod/otp")
                val apiKey = "mQP7e8tyrn8klIB6CJsmo6pMcbL9Vgfu7aZiTNn5"
                val httpURLConnection = url.openConnection() as HttpURLConnection
                httpURLConnection.requestMethod = "POST"
                httpURLConnection.doOutput = true
                httpURLConnection.setRequestProperty("Content-Type", "application/json")
                httpURLConnection.setRequestProperty("x-api-key", apiKey)

                val jsonInputString = "{\"id\": $rowId, \"status\": \"$newStatus\"}"
                httpURLConnection.outputStream.use { os ->
                    val input = jsonInputString.toByteArray(Charsets.UTF_8)
                    os.write(input, 0, input.size)
                }

                if (httpURLConnection.responseCode == HttpURLConnection.HTTP_OK) {
                    withContext(Dispatchers.Main) {
                        showToast("Status updated successfully for ID $rowId")
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        showToast("Failed to update status for ID $rowId")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    showToast("Error updating status for ID $rowId: ${e.message}")
                }
            }
        }
    }

    private fun showToast(message: String) {
        CoroutineScope(Dispatchers.Main).launch {
            Toast.makeText(applicationContext, message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        coroutineScope.cancel() // Cancel coroutines when the service is destroyed
    }
}