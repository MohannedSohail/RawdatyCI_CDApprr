package org.mohanned.rawdatyci_cdapp.domain.usecase.complaint

import org.mohanned.rawdatyci_cdapp.core.network.ApiResponse
import org.mohanned.rawdatyci_cdapp.domain.model.Complaint
import org.mohanned.rawdatyci_cdapp.domain.repository.ComplaintsRepository

class ReplyToComplaintUseCase(private val repository: ComplaintsRepository) {
    suspend operator fun invoke(id: String, reply: String): Result<Complaint> {
        return when (val result = repository.replyToComplaint(id, reply)) {
            is ApiResponse.Success -> Result.success(result.data)
            is ApiResponse.Error -> Result.failure(Exception(result.message))
            is ApiResponse.NetworkError -> Result.failure(Exception(result.message))
        }
    }
}
