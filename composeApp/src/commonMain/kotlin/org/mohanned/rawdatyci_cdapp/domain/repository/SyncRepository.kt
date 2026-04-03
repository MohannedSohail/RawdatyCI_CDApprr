package org.mohanned.rawdatyci_cdapp.domain.repository

import com.rawdaty.db.RawdatyDatabase
import kotlinx.datetime.Clock
import kotlinx.serialization.json.*
import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.remote.api.*

class SyncRepository(
    private val db: RawdatyDatabase,
    private val chatApi: ChatApiService,
    private val complaintsApi: ComplaintsApiService,
    private val attendanceApi: AttendanceApiService,
) {
    private val queries = db.rawdatyQueries

    suspend fun syncPendingActions() {
        val pending = queries.getPendingOfflineActions().executeAsList()
        if (pending.isEmpty()) return

        for (action in pending) {
            val payload = try { Json.parseToJsonElement(action.payload).jsonObject } catch(e: Exception) { null }
            if (payload == null) {
                queries.deleteOfflineAction(action.id)
                continue
            }
            
            val success = when (action.action_type) {
                "send_message"    -> syncMessage(payload)
                "create_complaint" -> syncComplaint(payload)
                "create_attendance" -> syncAttendance(payload)
                else               -> false
            }

            if (success) {
                queries.deleteOfflineAction(action.id)
            } else {
                // retry_count increment logic could go here
            }
        }
    }

    private suspend fun syncMessage(payload: JsonObject): Boolean {
        val convId = payload["conversation_id"]?.jsonPrimitive?.intOrNull ?: return false
        val content = payload["content"]?.jsonPrimitive?.content ?: return false
        return chatApi.sendMessage(convId, content) is ApiResponse.Success
    }

    private suspend fun syncComplaint(payload: JsonObject): Boolean {
        val title = payload["title"]?.jsonPrimitive?.content ?: return false
        val body = payload["body"]?.jsonPrimitive?.content ?: return false
        return complaintsApi.createComplaint(title, body) is ApiResponse.Success
    }

    private suspend fun syncAttendance(payload: JsonObject): Boolean {
        val classId = payload["class_id"]?.jsonPrimitive?.intOrNull ?: return false
        val date = payload["date"]?.jsonPrimitive?.content ?: return false
        val recordsJson = payload["records"]?.jsonArray ?: return false
        val records = recordsJson.map { 
            val obj = it.jsonObject
            AttendanceRecordRequest(
                child_id = obj["child_id"]?.jsonPrimitive?.intOrNull ?: 0,
                status = obj["status"]?.jsonPrimitive?.content ?: "present"
            )
        }
        return attendanceApi.createAttendance(classId, date, records) is ApiResponse.Success
    }
}
