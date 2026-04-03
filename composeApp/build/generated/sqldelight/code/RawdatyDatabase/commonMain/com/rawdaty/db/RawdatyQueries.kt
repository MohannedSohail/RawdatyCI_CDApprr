package com.rawdaty.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import kotlin.Any
import kotlin.Long
import kotlin.String

public class RawdatyQueries(
  driver: SqlDriver,
) : TransacterImpl(driver) {
  public fun <T : Any> getAuthCache(mapper: (
    id: Long,
    user_id: Long,
    user_name: String,
    user_email: String,
    user_role: String,
    tenant_slug: String,
    avatar_url: String?,
    cached_at: String,
  ) -> T): Query<T> = Query(833_379_985, arrayOf("auth_cache"), driver, "Rawdaty.sq",
      "getAuthCache",
      "SELECT auth_cache.id, auth_cache.user_id, auth_cache.user_name, auth_cache.user_email, auth_cache.user_role, auth_cache.tenant_slug, auth_cache.avatar_url, auth_cache.cached_at FROM auth_cache WHERE id = 1") {
      cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5)!!,
      cursor.getString(6),
      cursor.getString(7)!!
    )
  }

  public fun getAuthCache(): Query<Auth_cache> = getAuthCache { id, user_id, user_name, user_email,
      user_role, tenant_slug, avatar_url, cached_at ->
    Auth_cache(
      id,
      user_id,
      user_name,
      user_email,
      user_role,
      tenant_slug,
      avatar_url,
      cached_at
    )
  }

  public fun <T : Any> getAllClasses(mapper: (
    id: Long,
    name: String,
    description: String?,
    teacher_id: Long?,
    teacher_name: String?,
    children_count: Long,
    capacity: Long?,
    academic_year: String,
    created_at: String,
  ) -> T): Query<T> = Query(791_000_654, arrayOf("classes_cache"), driver, "Rawdaty.sq",
      "getAllClasses",
      "SELECT classes_cache.id, classes_cache.name, classes_cache.description, classes_cache.teacher_id, classes_cache.teacher_name, classes_cache.children_count, classes_cache.capacity, classes_cache.academic_year, classes_cache.created_at FROM classes_cache") {
      cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2),
      cursor.getLong(3),
      cursor.getString(4),
      cursor.getLong(5)!!,
      cursor.getLong(6),
      cursor.getString(7)!!,
      cursor.getString(8)!!
    )
  }

  public fun getAllClasses(): Query<Classes_cache> = getAllClasses { id, name, description,
      teacher_id, teacher_name, children_count, capacity, academic_year, created_at ->
    Classes_cache(
      id,
      name,
      description,
      teacher_id,
      teacher_name,
      children_count,
      capacity,
      academic_year,
      created_at
    )
  }

  public fun <T : Any> searchClasses(query: String, mapper: (
    id: Long,
    name: String,
    description: String?,
    teacher_id: Long?,
    teacher_name: String?,
    children_count: Long,
    capacity: Long?,
    academic_year: String,
    created_at: String,
  ) -> T): Query<T> = SearchClassesQuery(query) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2),
      cursor.getLong(3),
      cursor.getString(4),
      cursor.getLong(5)!!,
      cursor.getLong(6),
      cursor.getString(7)!!,
      cursor.getString(8)!!
    )
  }

  public fun searchClasses(query: String): Query<Classes_cache> = searchClasses(query) { id, name,
      description, teacher_id, teacher_name, children_count, capacity, academic_year, created_at ->
    Classes_cache(
      id,
      name,
      description,
      teacher_id,
      teacher_name,
      children_count,
      capacity,
      academic_year,
      created_at
    )
  }

  public fun <T : Any> getChildrenByClass(class_id: Long, mapper: (
    id: Long,
    full_name: String,
    date_of_birth: String?,
    gender: String,
    photo_url: String?,
    class_id: Long,
    class_name: String,
    parent_id: Long?,
    parent_name: String?,
    parent_phone: String?,
    enrollment_date: String,
    stars: Long,
    notes: String?,
    synced_at: String,
  ) -> T): Query<T> = GetChildrenByClassQuery(class_id) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2),
      cursor.getString(3)!!,
      cursor.getString(4),
      cursor.getLong(5)!!,
      cursor.getString(6)!!,
      cursor.getLong(7),
      cursor.getString(8),
      cursor.getString(9),
      cursor.getString(10)!!,
      cursor.getLong(11)!!,
      cursor.getString(12),
      cursor.getString(13)!!
    )
  }

  public fun getChildrenByClass(class_id: Long): Query<Children_cache> =
      getChildrenByClass(class_id) { id, full_name, date_of_birth, gender, photo_url, class_id_,
      class_name, parent_id, parent_name, parent_phone, enrollment_date, stars, notes, synced_at ->
    Children_cache(
      id,
      full_name,
      date_of_birth,
      gender,
      photo_url,
      class_id_,
      class_name,
      parent_id,
      parent_name,
      parent_phone,
      enrollment_date,
      stars,
      notes,
      synced_at
    )
  }

  public fun <T : Any> getChildById(id: Long, mapper: (
    id: Long,
    full_name: String,
    date_of_birth: String?,
    gender: String,
    photo_url: String?,
    class_id: Long,
    class_name: String,
    parent_id: Long?,
    parent_name: String?,
    parent_phone: String?,
    enrollment_date: String,
    stars: Long,
    notes: String?,
    synced_at: String,
  ) -> T): Query<T> = GetChildByIdQuery(id) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2),
      cursor.getString(3)!!,
      cursor.getString(4),
      cursor.getLong(5)!!,
      cursor.getString(6)!!,
      cursor.getLong(7),
      cursor.getString(8),
      cursor.getString(9),
      cursor.getString(10)!!,
      cursor.getLong(11)!!,
      cursor.getString(12),
      cursor.getString(13)!!
    )
  }

  public fun getChildById(id: Long): Query<Children_cache> = getChildById(id) { id_, full_name,
      date_of_birth, gender, photo_url, class_id, class_name, parent_id, parent_name, parent_phone,
      enrollment_date, stars, notes, synced_at ->
    Children_cache(
      id_,
      full_name,
      date_of_birth,
      gender,
      photo_url,
      class_id,
      class_name,
      parent_id,
      parent_name,
      parent_phone,
      enrollment_date,
      stars,
      notes,
      synced_at
    )
  }

  public fun <T : Any> getAttendanceByClassAndDate(
    class_id: Long,
    date: String,
    mapper: (
      id: Long,
      class_id: Long,
      child_id: Long,
      child_name: String,
      date: String,
      status: String,
      notes: String?,
      sync_status: String,
    ) -> T,
  ): Query<T> = GetAttendanceByClassAndDateQuery(class_id, date) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!,
      cursor.getLong(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5)!!,
      cursor.getString(6),
      cursor.getString(7)!!
    )
  }

  public fun getAttendanceByClassAndDate(class_id: Long, date: String): Query<Attendance_cache> =
      getAttendanceByClassAndDate(class_id, date) { id, class_id_, child_id, child_name, date_,
      status, notes, sync_status ->
    Attendance_cache(
      id,
      class_id_,
      child_id,
      child_name,
      date_,
      status,
      notes,
      sync_status
    )
  }

  public fun <T : Any> getPendingAttendance(mapper: (
    id: Long,
    class_id: Long,
    child_id: Long,
    child_name: String,
    date: String,
    status: String,
    notes: String?,
    sync_status: String,
  ) -> T): Query<T> = Query(696_968_375, arrayOf("attendance_cache"), driver, "Rawdaty.sq",
      "getPendingAttendance",
      "SELECT attendance_cache.id, attendance_cache.class_id, attendance_cache.child_id, attendance_cache.child_name, attendance_cache.date, attendance_cache.status, attendance_cache.notes, attendance_cache.sync_status FROM attendance_cache WHERE sync_status = 'pending'") {
      cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!,
      cursor.getLong(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5)!!,
      cursor.getString(6),
      cursor.getString(7)!!
    )
  }

  public fun getPendingAttendance(): Query<Attendance_cache> = getPendingAttendance { id, class_id,
      child_id, child_name, date, status, notes, sync_status ->
    Attendance_cache(
      id,
      class_id,
      child_id,
      child_name,
      date,
      status,
      notes,
      sync_status
    )
  }

  public fun <T : Any> getAllNews(mapper: (
    id: Long,
    title: String,
    body: String,
    image_url: String?,
    author_name: String,
    is_pinned: Long,
    created_at: String,
  ) -> T): Query<T> = Query(-1_306_264_149, arrayOf("news_cache"), driver, "Rawdaty.sq",
      "getAllNews",
      "SELECT news_cache.id, news_cache.title, news_cache.body, news_cache.image_url, news_cache.author_name, news_cache.is_pinned, news_cache.created_at FROM news_cache ORDER BY is_pinned DESC, created_at DESC") {
      cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3),
      cursor.getString(4)!!,
      cursor.getLong(5)!!,
      cursor.getString(6)!!
    )
  }

  public fun getAllNews(): Query<News_cache> = getAllNews { id, title, body, image_url, author_name,
      is_pinned, created_at ->
    News_cache(
      id,
      title,
      body,
      image_url,
      author_name,
      is_pinned,
      created_at
    )
  }

  public fun <T : Any> searchNews(query: String, mapper: (
    id: Long,
    title: String,
    body: String,
    image_url: String?,
    author_name: String,
    is_pinned: Long,
    created_at: String,
  ) -> T): Query<T> = SearchNewsQuery(query) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3),
      cursor.getString(4)!!,
      cursor.getLong(5)!!,
      cursor.getString(6)!!
    )
  }

  public fun searchNews(query: String): Query<News_cache> = searchNews(query) { id, title, body,
      image_url, author_name, is_pinned, created_at ->
    News_cache(
      id,
      title,
      body,
      image_url,
      author_name,
      is_pinned,
      created_at
    )
  }

  public fun <T : Any> getAllNotifications(mapper: (
    id: Long,
    title: String,
    body: String,
    type: String,
    is_read: Long,
    created_at: String,
  ) -> T): Query<T> = Query(-1_707_223_792, arrayOf("notifications_cache"), driver, "Rawdaty.sq",
      "getAllNotifications",
      "SELECT notifications_cache.id, notifications_cache.title, notifications_cache.body, notifications_cache.type, notifications_cache.is_read, notifications_cache.created_at FROM notifications_cache ORDER BY created_at DESC") {
      cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getLong(4)!!,
      cursor.getString(5)!!
    )
  }

  public fun getAllNotifications(): Query<Notifications_cache> = getAllNotifications { id, title,
      body, type, is_read, created_at ->
    Notifications_cache(
      id,
      title,
      body,
      type,
      is_read,
      created_at
    )
  }

  public fun <T : Any> getAllComplaints(mapper: (
    id: Long,
    title: String,
    body: String,
    status: String,
    parent_id: Long,
    parent_name: String,
    created_at: String,
    reply: String?,
  ) -> T): Query<T> = Query(-2_045_408_768, arrayOf("complaints_cache"), driver, "Rawdaty.sq",
      "getAllComplaints",
      "SELECT complaints_cache.id, complaints_cache.title, complaints_cache.body, complaints_cache.status, complaints_cache.parent_id, complaints_cache.parent_name, complaints_cache.created_at, complaints_cache.reply FROM complaints_cache ORDER BY created_at DESC") {
      cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getLong(4)!!,
      cursor.getString(5)!!,
      cursor.getString(6)!!,
      cursor.getString(7)
    )
  }

  public fun getAllComplaints(): Query<Complaints_cache> = getAllComplaints { id, title, body,
      status, parent_id, parent_name, created_at, reply ->
    Complaints_cache(
      id,
      title,
      body,
      status,
      parent_id,
      parent_name,
      created_at,
      reply
    )
  }

  public fun <T : Any> getChatMessages(conversation_id: Long, mapper: (
    id: Long,
    conversation_id: Long,
    sender_id: Long,
    sender_name: String,
    content: String,
    image_url: String?,
    is_read: Long,
    sent_at: String,
  ) -> T): Query<T> = GetChatMessagesQuery(conversation_id) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!,
      cursor.getLong(2)!!,
      cursor.getString(3)!!,
      cursor.getString(4)!!,
      cursor.getString(5),
      cursor.getLong(6)!!,
      cursor.getString(7)!!
    )
  }

  public fun getChatMessages(conversation_id: Long): Query<Messages_cache> =
      getChatMessages(conversation_id) { id, conversation_id_, sender_id, sender_name, content,
      image_url, is_read, sent_at ->
    Messages_cache(
      id,
      conversation_id_,
      sender_id,
      sender_name,
      content,
      image_url,
      is_read,
      sent_at
    )
  }

  public fun <T : Any> getPendingOfflineActions(mapper: (
    id: Long,
    action_type: String,
    payload: String,
    created_at: String,
    retry_count: Long,
  ) -> T): Query<T> = Query(979_003_496, arrayOf("offline_actions"), driver, "Rawdaty.sq",
      "getPendingOfflineActions",
      "SELECT offline_actions.id, offline_actions.action_type, offline_actions.payload, offline_actions.created_at, offline_actions.retry_count FROM offline_actions ORDER BY created_at ASC") {
      cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      cursor.getString(2)!!,
      cursor.getString(3)!!,
      cursor.getLong(4)!!
    )
  }

  public fun getPendingOfflineActions(): Query<Offline_actions> = getPendingOfflineActions { id,
      action_type, payload, created_at, retry_count ->
    Offline_actions(
      id,
      action_type,
      payload,
      created_at,
      retry_count
    )
  }

  public fun upsertAuthCache(
    user_id: Long,
    user_name: String,
    user_email: String,
    user_role: String,
    tenant_slug: String,
    avatar_url: String?,
    cached_at: String,
  ) {
    driver.execute(927_280_286, """
        |INSERT OR REPLACE INTO auth_cache(id, user_id, user_name, user_email, user_role, tenant_slug, avatar_url, cached_at)
        |VALUES (1, ?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 7) {
          bindLong(0, user_id)
          bindString(1, user_name)
          bindString(2, user_email)
          bindString(3, user_role)
          bindString(4, tenant_slug)
          bindString(5, avatar_url)
          bindString(6, cached_at)
        }
    notifyQueries(927_280_286) { emit ->
      emit("auth_cache")
    }
  }

  public fun clearAuthCache() {
    driver.execute(-1_450_660_294, """DELETE FROM auth_cache""", 0)
    notifyQueries(-1_450_660_294) { emit ->
      emit("auth_cache")
    }
  }

  public fun upsertClass(
    id: Long?,
    name: String,
    description: String?,
    teacher_id: Long?,
    teacher_name: String?,
    children_count: Long,
    capacity: Long?,
    academic_year: String,
    created_at: String,
  ) {
    driver.execute(-2_015_412_900,
        """INSERT OR REPLACE INTO classes_cache VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)""", 9) {
          bindLong(0, id)
          bindString(1, name)
          bindString(2, description)
          bindLong(3, teacher_id)
          bindString(4, teacher_name)
          bindLong(5, children_count)
          bindLong(6, capacity)
          bindString(7, academic_year)
          bindString(8, created_at)
        }
    notifyQueries(-2_015_412_900) { emit ->
      emit("classes_cache")
    }
  }

  public fun upsertChild(
    id: Long?,
    full_name: String,
    date_of_birth: String?,
    gender: String,
    photo_url: String?,
    class_id: Long,
    class_name: String,
    parent_id: Long?,
    parent_name: String?,
    parent_phone: String?,
    enrollment_date: String,
    stars: Long,
    notes: String?,
    synced_at: String,
  ) {
    driver.execute(-2_015_524_608,
        """INSERT OR REPLACE INTO children_cache VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)""",
        14) {
          bindLong(0, id)
          bindString(1, full_name)
          bindString(2, date_of_birth)
          bindString(3, gender)
          bindString(4, photo_url)
          bindLong(5, class_id)
          bindString(6, class_name)
          bindLong(7, parent_id)
          bindString(8, parent_name)
          bindString(9, parent_phone)
          bindString(10, enrollment_date)
          bindLong(11, stars)
          bindString(12, notes)
          bindString(13, synced_at)
        }
    notifyQueries(-2_015_524_608) { emit ->
      emit("children_cache")
    }
  }

  public fun upsertAttendance(
    class_id: Long,
    child_id: Long,
    child_name: String,
    date: String,
    status: String,
    notes: String?,
    sync_status: String,
  ) {
    driver.execute(-940_368_507, """
        |INSERT OR REPLACE INTO attendance_cache (class_id, child_id, child_name, date, status, notes, sync_status)
        |VALUES (?, ?, ?, ?, ?, ?, ?)
        """.trimMargin(), 7) {
          bindLong(0, class_id)
          bindLong(1, child_id)
          bindString(2, child_name)
          bindString(3, date)
          bindString(4, status)
          bindString(5, notes)
          bindString(6, sync_status)
        }
    notifyQueries(-940_368_507) { emit ->
      emit("attendance_cache")
    }
  }

  public fun upsertNews(
    id: Long?,
    title: String,
    body: String,
    image_url: String?,
    author_name: String,
    is_pinned: Long,
    created_at: String,
  ) {
    driver.execute(-2_142_901_649,
        """INSERT OR REPLACE INTO news_cache VALUES (?, ?, ?, ?, ?, ?, ?)""", 7) {
          bindLong(0, id)
          bindString(1, title)
          bindString(2, body)
          bindString(3, image_url)
          bindString(4, author_name)
          bindLong(5, is_pinned)
          bindString(6, created_at)
        }
    notifyQueries(-2_142_901_649) { emit ->
      emit("news_cache")
    }
  }

  public fun upsertNotification(
    id: Long?,
    title: String,
    body: String,
    type: String,
    is_read: Long,
    created_at: String,
  ) {
    driver.execute(812_747_911,
        """INSERT OR REPLACE INTO notifications_cache VALUES (?, ?, ?, ?, ?, ?)""", 6) {
          bindLong(0, id)
          bindString(1, title)
          bindString(2, body)
          bindString(3, type)
          bindLong(4, is_read)
          bindString(5, created_at)
        }
    notifyQueries(812_747_911) { emit ->
      emit("notifications_cache")
    }
  }

  public fun upsertComplaint(
    id: Long?,
    title: String,
    body: String,
    status: String,
    parent_id: Long,
    parent_name: String,
    created_at: String,
    reply: String?,
  ) {
    driver.execute(-1_910_243_697,
        """INSERT OR REPLACE INTO complaints_cache VALUES (?, ?, ?, ?, ?, ?, ?, ?)""", 8) {
          bindLong(0, id)
          bindString(1, title)
          bindString(2, body)
          bindString(3, status)
          bindLong(4, parent_id)
          bindString(5, parent_name)
          bindString(6, created_at)
          bindString(7, reply)
        }
    notifyQueries(-1_910_243_697) { emit ->
      emit("complaints_cache")
    }
  }

  public fun upsertChatMessage(
    id: Long?,
    conversation_id: Long,
    sender_id: Long,
    sender_name: String,
    content: String,
    image_url: String?,
    is_read: Long,
    sent_at: String,
  ) {
    driver.execute(854_403_699,
        """INSERT OR REPLACE INTO messages_cache VALUES (?, ?, ?, ?, ?, ?, ?, ?)""", 8) {
          bindLong(0, id)
          bindLong(1, conversation_id)
          bindLong(2, sender_id)
          bindString(3, sender_name)
          bindString(4, content)
          bindString(5, image_url)
          bindLong(6, is_read)
          bindString(7, sent_at)
        }
    notifyQueries(854_403_699) { emit ->
      emit("messages_cache")
    }
  }

  public fun insertOfflineAction(
    action_type: String,
    payload: String,
    created_at: String,
    retry_count: Long,
  ) {
    driver.execute(146_299_027,
        """INSERT INTO offline_actions (action_type, payload, created_at, retry_count) VALUES (?, ?, ?, ?)""",
        4) {
          bindString(0, action_type)
          bindString(1, payload)
          bindString(2, created_at)
          bindLong(3, retry_count)
        }
    notifyQueries(146_299_027) { emit ->
      emit("offline_actions")
    }
  }

  public fun deleteOfflineAction(id: Long) {
    driver.execute(1_582_722_785, """DELETE FROM offline_actions WHERE id = ?""", 1) {
          bindLong(0, id)
        }
    notifyQueries(1_582_722_785) { emit ->
      emit("offline_actions")
    }
  }

  private inner class SearchClassesQuery<out T : Any>(
    public val query: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("classes_cache", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("classes_cache", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_014_156_401, """
    |SELECT classes_cache.id, classes_cache.name, classes_cache.description, classes_cache.teacher_id, classes_cache.teacher_name, classes_cache.children_count, classes_cache.capacity, classes_cache.academic_year, classes_cache.created_at FROM classes_cache 
    |WHERE name LIKE ('%' || ? || '%') OR teacher_name LIKE ('%' || ? || '%')
    """.trimMargin(), mapper, 2) {
      bindString(0, query)
      bindString(1, query)
    }

    override fun toString(): String = "Rawdaty.sq:searchClasses"
  }

  private inner class GetChildrenByClassQuery<out T : Any>(
    public val class_id: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("children_cache", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("children_cache", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(1_954_336_505,
        """SELECT children_cache.id, children_cache.full_name, children_cache.date_of_birth, children_cache.gender, children_cache.photo_url, children_cache.class_id, children_cache.class_name, children_cache.parent_id, children_cache.parent_name, children_cache.parent_phone, children_cache.enrollment_date, children_cache.stars, children_cache.notes, children_cache.synced_at FROM children_cache WHERE class_id = ?""",
        mapper, 1) {
      bindLong(0, class_id)
    }

    override fun toString(): String = "Rawdaty.sq:getChildrenByClass"
  }

  private inner class GetChildByIdQuery<out T : Any>(
    public val id: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("children_cache", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("children_cache", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-696_775_963,
        """SELECT children_cache.id, children_cache.full_name, children_cache.date_of_birth, children_cache.gender, children_cache.photo_url, children_cache.class_id, children_cache.class_name, children_cache.parent_id, children_cache.parent_name, children_cache.parent_phone, children_cache.enrollment_date, children_cache.stars, children_cache.notes, children_cache.synced_at FROM children_cache WHERE id = ?""",
        mapper, 1) {
      bindLong(0, id)
    }

    override fun toString(): String = "Rawdaty.sq:getChildById"
  }

  private inner class GetAttendanceByClassAndDateQuery<out T : Any>(
    public val class_id: Long,
    public val date: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("attendance_cache", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("attendance_cache", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-805_622_250,
        """SELECT attendance_cache.id, attendance_cache.class_id, attendance_cache.child_id, attendance_cache.child_name, attendance_cache.date, attendance_cache.status, attendance_cache.notes, attendance_cache.sync_status FROM attendance_cache WHERE class_id = ? AND date = ?""",
        mapper, 2) {
      bindLong(0, class_id)
      bindString(1, date)
    }

    override fun toString(): String = "Rawdaty.sq:getAttendanceByClassAndDate"
  }

  private inner class SearchNewsQuery<out T : Any>(
    public val query: String,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("news_cache", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("news_cache", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-2_141_433_240, """
    |SELECT news_cache.id, news_cache.title, news_cache.body, news_cache.image_url, news_cache.author_name, news_cache.is_pinned, news_cache.created_at FROM news_cache 
    |WHERE title LIKE ('%' || ? || '%') OR body LIKE ('%' || ? || '%')
    |ORDER BY is_pinned DESC, created_at DESC
    """.trimMargin(), mapper, 2) {
      bindString(0, query)
      bindString(1, query)
    }

    override fun toString(): String = "Rawdaty.sq:searchNews"
  }

  private inner class GetChatMessagesQuery<out T : Any>(
    public val conversation_id: Long,
    mapper: (SqlCursor) -> T,
  ) : Query<T>(mapper) {
    override fun addListener(listener: Query.Listener) {
      driver.addListener("messages_cache", listener = listener)
    }

    override fun removeListener(listener: Query.Listener) {
      driver.removeListener("messages_cache", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(-643_446_387,
        """SELECT messages_cache.id, messages_cache.conversation_id, messages_cache.sender_id, messages_cache.sender_name, messages_cache.content, messages_cache.image_url, messages_cache.is_read, messages_cache.sent_at FROM messages_cache WHERE conversation_id = ? ORDER BY sent_at ASC""",
        mapper, 1) {
      bindLong(0, conversation_id)
    }

    override fun toString(): String = "Rawdaty.sq:getChatMessages"
  }
}
