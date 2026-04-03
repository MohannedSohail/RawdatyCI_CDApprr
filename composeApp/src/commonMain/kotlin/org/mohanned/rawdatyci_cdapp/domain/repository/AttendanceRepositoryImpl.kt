package org.mohanned.rawdatyci_cdapp.domain.repository

import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.remote.api.AttendanceApiService
import org.mohanned.rawdatyci_cdapp.data.remote.api.AttendanceRecordRequest
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toDomain
import org.mohanned.rawdatyci_cdapp.domain.model.*
import com.rawdaty.db.RawdatyDatabase
import kotlinx.datetime.Clock

class AttendanceRepositoryImpl(
    private val api: AttendanceApiService,
    private val db: RawdatyDatabase,
) : AttendanceRepository {

    private val queries = db.rawdatyQueries

    override suspend fun createAttendance(
        classId: Int,
        date: String,
        records: List<Pair<Int, String>>
    ): ApiResponse<AttendanceSummary> {
        val request = records.map { (childId, status) ->
            AttendanceRecordRequest(child_id = childId, status = status)
        }
        
        return when (val r = api.createAttendance(classId, date, request)) {
            is ApiResponse.Success -> {
                val summary = r.data.toDomain()
                summary.records.forEach { record ->
                    queries.upsertAttendance(
                        class_id = classId.toLong(),
                        child_id = record.childId.toLong(),
                        child_name = record.childName,
                        date = date,
                        status = record.status.name.lowercase(),
                        notes = record.notes,
                        sync_status = "synced"
                    )
                }
                ApiResponse.Success(summary)
            }
            is ApiResponse.NetworkError -> {
                records.forEach { (childId, status) ->
                    queries.upsertAttendance(
                        class_id = classId.toLong(),
                        child_id = childId.toLong(),
                        child_name = "", 
                        date = date,
                        status = status,
                        notes = "Offline Submission",
                        sync_status = "pending"
                    )
                }
                ApiResponse.NetworkError("تم حفظ التحضير محلياً")
            }
            is ApiResponse.Error -> ApiResponse.Error(r.code, r.message)
        }
    }

    override suspend fun getClassAttendance(
        classId: Int,
        date: String?,
        page: Int
    ): ApiResponse<PaginatedResult<AttendanceSummary>> {
        val safeDate = date ?: Clock.System.now().toString().split("T")[0]
        return when (val r = api.getClassAttendance(classId, safeDate, page)) {
            is ApiResponse.Success -> ApiResponse.Success(
                PaginatedResult(
                    data = r.data.data.map { it.toDomain() },
                    meta = r.data.meta?.let {
                        PageMeta(it.page, it.perPage, it.total, it.lastPage)
                    } ?: PageMeta(1, 15, 0, 1)
                )
            )
            is ApiResponse.NetworkError -> {
                val cached = queries.getAttendanceByClassAndDate(classId.toLong(), safeDate).executeAsList()
                if (cached.isNotEmpty()) {
                    val domainRecords = cached.map {
                        AttendanceRecord(
                            id = it.id.toInt(), 
                            childId = it.child_id.toInt(), 
                            childName = it.child_name, 
                            childPhoto = null,
                            status = try { AttendanceStatus.valueOf(it.status.uppercase()) } catch(e: Exception) { AttendanceStatus.PRESENT }, 
                            notes = it.notes,
                            date = it.date
                        )
                    }
                    val presentCount = domainRecords.count { it.status == AttendanceStatus.PRESENT }
                    val summary = AttendanceSummary(
                        date = safeDate, 
                        classId = classId,
                        present = presentCount, 
                        total = domainRecords.size, 
                        records = domainRecords,
                        absent = domainRecords.count { it.status == AttendanceStatus.ABSENT },
                        late = domainRecords.count { it.status == AttendanceStatus.LATE },
                        presentPct = if (domainRecords.isNotEmpty()) presentCount * 100f / domainRecords.size else 0f
                    )
                    ApiResponse.Success(PaginatedResult(data = listOf(summary), meta = PageMeta(1, 1, 1, 1)))
                } else ApiResponse.NetworkError(r.message)
            }
            is ApiResponse.Error -> ApiResponse.Error(r.code, r.message)
        }
    }

    override suspend fun getChildAttendance(childId: Int, page: Int) = 
        api.getChildAttendance(childId, page).let { r ->
            if (r is ApiResponse.Success) {
                ApiResponse.Success(PaginatedResult(
                    data = r.data.data.map { it.toDomain() },
                    meta = r.data.meta?.let { PageMeta(it.page, it.perPage, it.total, it.lastPage) } ?: PageMeta(1, 15, 0, 1)
                ))
            } else r as ApiResponse<PaginatedResult<AttendanceRecord>>
        }
}
