package org.mohanned.rawdatyci_cdapp.domain.repository

import org.mohanned.rawdatyci_cdapp.core.network.remote.ApiResponse
import org.mohanned.rawdatyci_cdapp.data.remote.api.*
import org.mohanned.rawdatyci_cdapp.data.remote.dto.ApiListDto
import org.mohanned.rawdatyci_cdapp.data.remote.dto.toDomain
import org.mohanned.rawdatyci_cdapp.domain.model.*
import com.rawdaty.db.RawdatyDatabase
import kotlinx.datetime.Clock

// ── Helper ────────────────────────────────────────────
private fun <T, R> ApiResponse<ApiListDto<T>>.mapList(
    transform: (T) -> R,
): ApiResponse<PaginatedResult<R>> = when (this) {
    is ApiResponse.Success -> ApiResponse.Success(
        PaginatedResult(
            data = data.data.map(transform),
            meta = data.meta?.let {
                PageMeta(it.page, it.perPage, it.total, it.lastPage)
            } ?: PageMeta(1, 15, 0, 1),
        )
    )
    is ApiResponse.Error        -> ApiResponse.Error(code, message)
    is ApiResponse.NetworkError -> ApiResponse.NetworkError(message)
}

private fun <T> ApiResponse<T>.toUnit(): ApiResponse<Unit> = when (this) {
    is ApiResponse.Success      -> ApiResponse.Success(Unit)
    is ApiResponse.Error        -> ApiResponse.Error(code, message)
    is ApiResponse.NetworkError -> ApiResponse.NetworkError(message)
}

// ── Chat Repository ───────────────────────────────────
class ChatRepositoryImpl(
    private val api: ChatApiService,
    private val db: RawdatyDatabase,
) : ChatRepository {

    private val queries = db.rawdatyQueries

    override suspend fun getConversations(page: Int) =
        api.getConversations(page).mapList { it.toDomain() }

    override suspend fun getMessages(conversationId: Int, page: Int): ApiResponse<PaginatedResult<Message>> {
        return when (val r = api.getMessages(conversationId, page)) {
            is ApiResponse.Success -> {
                val messages = r.data.data.map { it.toDomain() }
                messages.forEach {
                    queries.upsertChatMessage(
                        it.id.toLong(),
                        it.conversationId.toLong(),
                        it.senderId.toLong(),
                        it.senderName,
                        it.content,
                        it.imageUrl,
                        if (it.isRead) 1L else 0L,
                        it.sentAt
                    )
                }
                ApiResponse.Success(
                    PaginatedResult(
                        data = messages,
                        meta = r.data.meta?.let {
                            PageMeta(it.page, it.perPage, it.total, it.lastPage)
                        } ?: PageMeta(1, 15, 0, 1)
                    )
                )
            }
            is ApiResponse.NetworkError -> {
                val cached = queries.getChatMessages(conversationId.toLong()).executeAsList().map {
                    Message(
                        id = it.id.toInt(), 
                        conversationId = it.conversation_id.toInt(), 
                        senderId = it.sender_id.toInt(), 
                        senderName = it.sender_name, 
                        content = it.content, 
                        imageUrl = it.image_url, 
                        isRead = it.is_read == 1L, 
                        sentAt = it.sent_at
                    )
                }
                if (cached.isNotEmpty()) {
                    ApiResponse.Success(PaginatedResult(data = cached, meta = PageMeta(1, cached.size, cached.size, 1)))
                } else ApiResponse.NetworkError(r.message)
            }
            is ApiResponse.Error -> ApiResponse.Error(r.code, r.message)
        }
    }

    override suspend fun sendMessage(
        conversationId: Int,
        content: String,
    ): ApiResponse<Message> {
        return when (val r = api.sendMessage(conversationId, content)) {
            is ApiResponse.Success -> {
                val msg = r.data.toDomain()
                queries.upsertChatMessage(msg.id.toLong(), msg.conversationId.toLong(), msg.senderId.toLong(), msg.senderName, msg.content, msg.imageUrl, 0L, msg.sentAt)
                ApiResponse.Success(msg)
            }
            is ApiResponse.NetworkError -> {
                queries.insertOfflineAction(
                    "send_message",
                    "{\"conversation_id\":$conversationId, \"content\":\"$content\"}",
                    Clock.System.now().toString(),
                    0L
                )
                ApiResponse.NetworkError("Saved locally")
            }
            is ApiResponse.Error -> ApiResponse.Error(r.code, r.message)
        }
    }

    override suspend fun startConversation(
        participantId: Int,
        childId: Int?,
    ): ApiResponse<Conversation> = api.startConversation(participantId, childId).let { r ->
        if (r is ApiResponse.Success) ApiResponse.Success(r.data.toDomain()) else r as ApiResponse<Conversation>
    }
}

// ── News Repository ───────────────────────────────────
class NewsRepositoryImpl(
    private val api: NewsApiService,
    private val db: RawdatyDatabase,
) : NewsRepository {

    private val queries = db.rawdatyQueries

    override suspend fun getNews(search: String?, page: Int): ApiResponse<PaginatedResult<News>> {
        return when (val r = api.getNews(search, page)) {
            is ApiResponse.Success -> {
                val news = r.data.data.map { it.toDomain() }
                news.forEach {
                    queries.upsertNews(
                        it.id.toLong(),
                        it.title,
                        it.body,
                        it.imageUrl,
                        it.authorName,
                        if (it.isPinned) 1L else 0L,
                        it.createdAt
                    )
                }
                ApiResponse.Success(
                    PaginatedResult(
                        data = news,
                        meta = r.data.meta?.let {
                            PageMeta(it.page, it.perPage, it.total, it.lastPage)
                        } ?: PageMeta(1, 15, 0, 1)
                    )
                )
            }
            is ApiResponse.NetworkError -> {
                val cached = if (search.isNullOrEmpty()) {
                    queries.getAllNews().executeAsList()
                } else {
                    queries.searchNews(search).executeAsList()
                }.map {
                    News(
                        id = it.id.toInt(), 
                        title = it.title, 
                        body = it.body, 
                        imageUrl = it.image_url, 
                        isPinned = it.is_pinned == 1L, 
                        authorName = it.author_name, 
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

    override suspend fun getNewsItem(id: Int): ApiResponse<News> {
        return when (val r = api.getNewsItem(id)) {
            is ApiResponse.Success -> ApiResponse.Success(r.data.toDomain())
            is ApiResponse.NetworkError -> {
                val cachedItem = queries.getAllNews().executeAsList().find { it.id == id.toLong() }
                if (cachedItem != null) {
                    ApiResponse.Success(
                        News(
                            id = cachedItem.id.toInt(), 
                            title = cachedItem.title, 
                            body = cachedItem.body, 
                            imageUrl = cachedItem.image_url, 
                            isPinned = cachedItem.is_pinned == 1L, 
                            authorName = cachedItem.author_name, 
                            createdAt = cachedItem.created_at
                        )
                    )
                } else ApiResponse.NetworkError(r.message)
            }
            is ApiResponse.Error -> ApiResponse.Error(r.code, r.message)
        }
    }

    override suspend fun createNews(title: String, body: String, isPinned: Boolean) = api.createNews(title, body, isPinned).let { r ->
        if (r is ApiResponse.Success) ApiResponse.Success(r.data.toDomain()) else r as ApiResponse<News>
    }

    override suspend fun updateNews(id: Int, title: String?, body: String?) = api.updateNews(id, title, body).let { r ->
        if (r is ApiResponse.Success) ApiResponse.Success(r.data.toDomain()) else r as ApiResponse<News>
    }

    override suspend fun deleteNews(id: Int) = api.deleteNews(id).let { r ->
        when (r) {
            is ApiResponse.Success      -> ApiResponse.Success(Unit)
            is ApiResponse.Error        -> ApiResponse.Error(r.code, r.message)
            is ApiResponse.NetworkError -> ApiResponse.NetworkError(r.message)
        }
    }
}

// ── Complaints Repository ─────────────────────────────
class ComplaintsRepositoryImpl(
    private val api: ComplaintsApiService,
    private val db: RawdatyDatabase,
) : ComplaintsRepository {

    private val queries = db.rawdatyQueries

    override suspend fun getComplaints(status: String?, page: Int): ApiResponse<PaginatedResult<Complaint>> {
        return when (val r = api.getComplaints(status, page)) {
            is ApiResponse.Success -> {
                val list = r.data.data.map { it.toDomain() }
                list.forEach {
                    queries.upsertComplaint(
                        it.id.toLong(),
                        it.title,
                        it.body,
                        it.status.name,
                        it.parentId.toLong(),
                        it.parentName,
                        it.createdAt,
                        it.adminReply
                    )
                }
                ApiResponse.Success(PaginatedResult(data = list, meta = r.data.meta?.let { PageMeta(it.page, it.perPage, it.total, it.lastPage) } ?: PageMeta(1, 15, 0, 1)))
            }
            is ApiResponse.NetworkError -> {
                val cached = queries.getAllComplaints().executeAsList().map {
                    Complaint(
                        id = it.id.toInt(), 
                        title = it.title, 
                        body = it.body, 
                        parentId = it.parent_id.toInt(), 
                        parentName = it.parent_name, 
                        status = try { ComplaintStatus.valueOf(it.status.uppercase()) } catch(e: Exception) { ComplaintStatus.PENDING }, 
                        adminReply = it.reply, 
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

    override suspend fun getComplaint(id: Int): ApiResponse<Complaint> {
        return when (val r = api.getComplaint(id)) {
            is ApiResponse.Success -> ApiResponse.Success(r.data.toDomain())
            is ApiResponse.NetworkError -> {
                val cachedItem = queries.getAllComplaints().executeAsList().find { it.id == id.toLong() }
                if (cachedItem != null) {
                    ApiResponse.Success(
                        Complaint(
                            id = cachedItem.id.toInt(), 
                            title = cachedItem.title, 
                            body = cachedItem.body, 
                            parentId = cachedItem.parent_id.toInt(),
                            parentName = cachedItem.parent_name, 
                            status = try { ComplaintStatus.valueOf(cachedItem.status.uppercase()) } catch(e: Exception) { ComplaintStatus.PENDING }, 
                            adminReply = cachedItem.reply, 
                            createdAt = cachedItem.created_at
                        )
                    )
                } else ApiResponse.NetworkError(r.message)
            }
            is ApiResponse.Error -> ApiResponse.Error(r.code, r.message)
        }
    }

    override suspend fun createComplaint(title: String, body: String): ApiResponse<Complaint> {
        return when (val r = api.createComplaint(title, body)) {
            is ApiResponse.Success -> ApiResponse.Success(r.data.toDomain())
            is ApiResponse.NetworkError -> {
                queries.insertOfflineAction("create_complaint", "{\"title\":\"$title\", \"body\":\"$body\"}", Clock.System.now().toString(), 0L)
                ApiResponse.NetworkError("Saved locally")
            }
            is ApiResponse.Error -> ApiResponse.Error(r.code, r.message)
        }
    }

    override suspend fun replyToComplaint(id: Int, reply: String, status: String) = api.replyToComplaint(id, reply, status).let { r ->
        if (r is ApiResponse.Success) ApiResponse.Success(r.data.toDomain()) else r as ApiResponse<Complaint>
    }
}

// ── Notifications Repository ──────────────────────────
class NotificationsRepositoryImpl(
    private val api: NotificationsApiService,
    private val db: RawdatyDatabase,
) : NotificationsRepository {

    private val queries = db.rawdatyQueries

    override suspend fun getNotifications(page: Int): ApiResponse<PaginatedResult<AppNotification>> {
        return when (val r = api.getNotifications(page)) {
            is ApiResponse.Success -> {
                val list = r.data.data.map { it.toDomain() }
                list.forEach {
                    queries.upsertNotification(
                        it.id.toLong(),
                        it.title,
                        it.body,
                        it.type,
                        if (it.isRead) 1L else 0L,
                        it.createdAt
                    )
                }
                ApiResponse.Success(PaginatedResult(data = list, meta = r.data.meta?.let { PageMeta(it.page, it.perPage, it.total, it.lastPage) } ?: PageMeta(1, 15, 0, 1)))
            }
            is ApiResponse.NetworkError -> {
                val cached = queries.getAllNotifications().executeAsList().map {
                    AppNotification(
                        id = it.id.toInt(), 
                        title = it.title, 
                        body = it.body, 
                        type = it.type, 
                        isRead = it.is_read == 1L, 
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

    override suspend fun markRead(id: Int) = api.markRead(id).toUnit()
    override suspend fun markAllRead() = api.markAllRead().toUnit()
    override suspend fun sendNotification(title: String, body: String, target: String, classId: Int?) = api.sendNotification(title, body, target, classId).toUnit()
}

// ── Games Repository ──────────────────────────────────
class GamesRepositoryImpl(
    private val api: GamesApiService,
) : GamesRepository {
    override suspend fun getQuestions(gameType: String, level: Int) = api.getQuestions(gameType, level).let { r ->
        if (r is ApiResponse.Success) ApiResponse.Success(r.data.data.map { it.toDomain() }) else r as ApiResponse<List<GameQuestion>>
    }
    override suspend fun saveResult(childId: Int, gameType: String, score: Int, totalQuestions: Int, durationSeconds: Int) = 
        api.saveResult(childId, gameType, score, totalQuestions, durationSeconds).toUnit()
}

// ── Users Repository ──────────────────────────────────
class UsersRepositoryImpl(
    private val api: UsersApiService,
    private val db: RawdatyDatabase,
) : UsersRepository {

    private val queries = db.rawdatyQueries

    override suspend fun getUsers(role: String?, classId: Int?, search: String?, page: Int) = api.getUsers(role, classId, search, page).mapList { it.toDomain() }

    override suspend fun getUser(id: Int) = api.getUser(id).let { r ->
        if (r is ApiResponse.Success) ApiResponse.Success(r.data.toDomain()) else r as ApiResponse<User>
    }

    override suspend fun createUser(name: String, email: String, password: String, role: String, phone: String?, classId: Int?) = 
        api.createUser(name, email, password, role, phone, classId).let { r ->
            if (r is ApiResponse.Success) ApiResponse.Success(r.data.toDomain()) else r as ApiResponse<User>
        }

    override suspend fun updateUser(id: Int, name: String?, phone: String?, isActive: Boolean?) = 
        api.updateUser(id, name, phone, isActive).let { r ->
            if (r is ApiResponse.Success) ApiResponse.Success(r.data.toDomain()) else r as ApiResponse<User>
        }

    override suspend fun deleteUser(id: Int) = api.deleteUser(id).toUnit()

    override suspend fun getProfile(): ApiResponse<User> {
        return when (val r = api.getProfile()) {
            is ApiResponse.Success -> ApiResponse.Success(r.data.toDomain())
            else -> r as ApiResponse<User>
        }
    }

    override suspend fun updateProfile(name: String, phone: String?) = api.updateProfile(name, phone).let { r ->
        if (r is ApiResponse.Success) ApiResponse.Success(r.data.toDomain()) else r as ApiResponse<User>
    }

    override suspend fun changePassword(current: String, newPass: String, confirm: String) = api.changePassword(current, newPass, confirm).toUnit()
}
