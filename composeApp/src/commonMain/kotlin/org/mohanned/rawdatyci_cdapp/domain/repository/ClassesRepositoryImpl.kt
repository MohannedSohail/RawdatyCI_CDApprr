package org.mohanned.rawdatyci_cdapp.domain.repository

import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.remote.api.ClassesApiService
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toDomain
import org.mohanned.rawdatyci_cdapp.domain.model.*
import com.rawdaty.db.RawdatyDatabase
import kotlinx.datetime.Clock

class ClassesRepositoryImpl(
    private val api: ClassesApiService,
    private val db: RawdatyDatabase,
) : ClassesRepository {

    private val queries = db.rawdatyQueries

    override suspend fun getClasses(search: String?, page: Int): ApiResponse<PaginatedResult<Classroom>> {
        return when (val r = api.getClasses(search, page)) {
            is ApiResponse.Success -> {
                val classes = r.data.data.map { it.toDomain() }
                classes.forEach {
                    queries.upsertClass(
                        it.id.toLong(),
                        it.name,
                        it.description,
                        it.teacherId?.toLong(),
                        it.teacherName,
                        it.childrenCount.toLong(),
                        it.capacity?.toLong(),
                        it.academicYear,
                        it.createdAt
                    )
                }
                ApiResponse.Success(
                    PaginatedResult(
                        data = classes,
                        meta = r.data.meta?.let { 
                            PageMeta(it.page, it.perPage, it.total, it.lastPage) 
                        } ?: PageMeta(1, 15, 0, 1)
                    )
                )
            }
            is ApiResponse.NetworkError -> {
                val cached = if (search.isNullOrEmpty()) {
                    queries.getAllClasses().executeAsList()
                } else {
                    queries.searchClasses(search).executeAsList()
                }.map {
                    Classroom(
                        id = it.id.toInt(), 
                        name = it.name, 
                        description = it.description, 
                        teacherId = it.teacher_id?.toInt(),
                        teacherName = it.teacher_name, 
                        childrenCount = it.children_count.toInt(), 
                        capacity = it.capacity?.toInt(),
                        academicYear = it.academic_year,
                        createdAt = it.created_at
                    )
                }
                
                if (cached.isNotEmpty()) {
                    ApiResponse.Success(PaginatedResult(data = cached, meta = PageMeta(1, cached.size, cached.size, 1)))
                } else ApiResponse.NetworkError(r.message)
            }
            is ApiResponse.Error -> ApiResponse.Error(r.code, r.message)
        }
    }

    override suspend fun getClass(id: Int): ApiResponse<Classroom> {
        return when (val r = api.getClass(id)) {
            is ApiResponse.Success -> ApiResponse.Success(r.data.toDomain())
            is ApiResponse.NetworkError -> {
                val it = queries.getAllClasses().executeAsList().find { it.id == id.toLong() }
                if (it != null) {
                    ApiResponse.Success(Classroom(
                        id = it.id.toInt(), 
                        name = it.name, 
                        description = it.description, 
                        teacherId = it.teacher_id?.toInt(),
                        teacherName = it.teacher_name, 
                        childrenCount = it.children_count.toInt(), 
                        capacity = it.capacity?.toInt(),
                        academicYear = it.academic_year,
                        createdAt = it.created_at
                    ))
                } else ApiResponse.NetworkError(r.message)
            }
            is ApiResponse.Error -> ApiResponse.Error(r.code, r.message)
        }
    }

    override suspend fun createClass(name: String, teacherId: Int?, capacity: Int?) = api.createClass(name, teacherId, capacity).let { r ->
        if (r is ApiResponse.Success) ApiResponse.Success(r.data.toDomain()) else r as ApiResponse<Classroom>
    }

    override suspend fun updateClass(id: Int, name: String?, teacherId: Int?) = api.updateClass(id, name, teacherId).let { r ->
        if (r is ApiResponse.Success) ApiResponse.Success(r.data.toDomain()) else r as ApiResponse<Classroom>
    }

    override suspend fun deleteClass(id: Int) = api.deleteClass(id).let { r ->
        if (r is ApiResponse.Success) ApiResponse.Success(Unit) else r as ApiResponse<Unit>
    }

    override suspend fun getChildrenByClass(classId: Int, page: Int): ApiResponse<PaginatedResult<Child>> {
        return when (val r = api.getChildrenByClass(classId, page)) {
            is ApiResponse.Success -> {
                val children = r.data.data.map { it.toDomain() }
                children.forEach {
                    queries.upsertChild(
                        it.id.toLong(),
                        it.fullName,
                        it.dateOfBirth,
                        it.gender,
                        it.photoUrl,
                        it.classId.toLong(),
                        it.className,
                        it.parentId?.toLong(),
                        it.parentName,
                        it.parentPhone,
                        it.enrollmentDate,
                        it.stars.toLong(),
                        it.notes,
                        Clock.System.now().toString()
                    )
                }
                ApiResponse.Success(
                    PaginatedResult(
                        data = children,
                        meta = r.data.meta?.let { PageMeta(it.page, it.perPage, it.total, it.lastPage) } ?: PageMeta(1, 15, 0, 1)
                    )
                )
            }
            is ApiResponse.NetworkError -> {
                val cached = queries.getChildrenByClass(classId.toLong()).executeAsList().map {
                    Child(
                        id = it.id.toInt(),
                        fullName = it.full_name,
                        dateOfBirth = it.date_of_birth,
                        gender = it.gender,
                        photoUrl = it.photo_url,
                        classId = it.class_id.toInt(),
                        className = it.class_name,
                        parentId = it.parent_id?.toInt(),
                        parentName = it.parent_name,
                        parentPhone = it.parent_phone,
                        enrollmentDate = it.enrollment_date,
                        stars = it.stars.toInt(),
                        notes = it.notes,
                        allergies = emptyList()
                    )
                }
                if (cached.isNotEmpty()) {
                    ApiResponse.Success(PaginatedResult(data = cached, meta = PageMeta(1, cached.size, cached.size, 1)))
                } else ApiResponse.NetworkError(r.message)
            }
            is ApiResponse.Error -> ApiResponse.Error(r.code, r.message)
        }
    }

    override suspend fun getMyChildren(): ApiResponse<PaginatedResult<Child>> {
        return api.getMyChildren().let { r ->
            if (r is ApiResponse.Success) {
                ApiResponse.Success(PaginatedResult(
                    data = r.data.data.map { it.toDomain() },
                    meta = r.data.meta?.let { PageMeta(it.page, it.perPage, it.total, it.lastPage) } ?: PageMeta(1, 15, 0, 1)
                ))
            } else r as ApiResponse<PaginatedResult<Child>>
        }
    }
}
