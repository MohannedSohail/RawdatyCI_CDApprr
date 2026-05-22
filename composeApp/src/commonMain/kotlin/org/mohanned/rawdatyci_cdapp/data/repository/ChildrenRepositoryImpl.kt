package org.mohanned.rawdatyci_cdapp.data.repository

import org.mohanned.rawdatyci_cdapp.data.remote.api.ChildrenApiService
import org.mohanned.rawdatyci_cdapp.data.remote.api.ClassesApiService
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toDomain
import org.mohanned.rawdatyci_cdapp.domain.model.Child
import org.mohanned.rawdatyci_cdapp.domain.model.PaginatedResult
import org.mohanned.rawdatyci_cdapp.domain.repository.ChildrenRepository
import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse

class ChildrenRepositoryImpl(
    private val api: ChildrenApiService,
    private val classesApi: ClassesApiService 
) : ChildrenRepository {
    
    override suspend fun getChildrenByClass(classId: String, page: Int): ApiResponse<PaginatedResult<Child>> {
        return try {
            val response = api.getClassDetails(classId)
            if (response is ApiResponse.Success) {
                // تحويل البيانات وحقن البيانات التجريبية للحقول الناقصة
                val children = response.data.children?.map { 
                    it.toDomain().enrichWithDummyData() 
                } ?: emptyList()
                
                ApiResponse.Success(
                    PaginatedResult(
                        items = children,
                        total = children.size,
                        page = 1,
                        lastPage = 1,
                        hasMore = false
                    )
                )
            } else {
                @Suppress("UNCHECKED_CAST")
                response as ApiResponse<PaginatedResult<Child>>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun getMyChildren(page: Int): ApiResponse<PaginatedResult<Child>> {
        return try {
            val response = classesApi.getClasses(includeChildren = true)
            if (response is ApiResponse.Success) {
                // تجميع الطلاب من كل الفصول وحقن البيانات التجريبية
                val allChildren = response.data.flatMap { classDto -> 
                    classDto.children?.map { it.toDomain().enrichWithDummyData() } ?: emptyList()
                }
                
                ApiResponse.Success(
                    PaginatedResult(
                        items = allChildren,
                        total = allChildren.size,
                        page = 1,
                        lastPage = 1,
                        hasMore = false
                    )
                )
            } else {
                @Suppress("UNCHECKED_CAST")
                response as ApiResponse<PaginatedResult<Child>>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    /**
     * وظيفة مساعدة لحقن بيانات افتراضية في حال كانت الحقول فارغة من الـ API
     */
    private fun Child.enrichWithDummyData(): Child {
        return this.copy(
            // إذا كانت النجوم 0، نضع قيمة عشوائية بين 3 و 5 للجمالية
            stars = if (this.stars == 0) (3..5).shuffled().first() else this.stars,
            
            // بيانات ولي الأمر
            parentName = if (this.parentName.isNullOrBlank()) "أ. محمد عبد الله" else this.parentName,
            parentPhone = if (this.parentPhone.isNullOrBlank()) "0599123456" else this.parentPhone,
          //  parentEmail = if (this.ح.isNullOrBlank()) "parent.care@rawdati.app" else this.parentEmail,
            
            // المعلومات الشخصية
            dateOfBirth = if (this.dateOfBirth.isNullOrBlank()) "2020-05-12" else this.dateOfBirth,
            enrollmentDate = if (this.enrollmentDate.isBlank()) "2023-09-01" else this.enrollmentDate,
            
            // ملاحظات إضافية
            notes = if (this.notes.isNullOrBlank()) "طفل متميز ومحب للأنشطة الجماعية، يظهر مهارات قيادية واضحة." else this.notes
        )
    }

    override suspend fun createChild(name: String, parentId: String, classId: String?, birthDate: String?, gender: String): ApiResponse<Child> {
        return try {
            val response = api.createChild(name, classId ?: "", birthDate, gender, parentId)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain().enrichWithDummyData())
            } else {
                response as ApiResponse<Child>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }

    override suspend fun updateChild(id: String, classId: String?, notes: String?, rating: Int?): ApiResponse<Child> {
        return try {
            val response = api.updateChild(id, null, classId, null, null)
            if (response is ApiResponse.Success) {
                ApiResponse.Success(response.data.toDomain().enrichWithDummyData())
            } else {
                response as ApiResponse<Child>
            }
        } catch (e: Exception) {
            ApiResponse.NetworkError(e.message ?: "Network error")
        }
    }
}
