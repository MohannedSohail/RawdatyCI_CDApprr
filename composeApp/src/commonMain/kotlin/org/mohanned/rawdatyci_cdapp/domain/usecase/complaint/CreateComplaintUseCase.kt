package org.mohanned.rawdatyci_cdapp.domain.usecase.complaint

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.Complaint
import org.mohanned.rawdatyci_cdapp.domain.repository.ComplaintsRepository

class CreateComplaintUseCase(private val repository: ComplaintsRepository) {
    suspend operator fun invoke(content: String, type: String = "complaint"): Result<Complaint> {
        return when (val result = repository.createComplaint(content, type)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
