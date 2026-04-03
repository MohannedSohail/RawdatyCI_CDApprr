package com.rawdaty.db.composeApp

import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.AfterVersion
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.rawdaty.db.RawdatyDatabase
import com.rawdaty.db.RawdatyQueries
import kotlin.Long
import kotlin.Unit
import kotlin.reflect.KClass

internal val KClass<RawdatyDatabase>.schema: SqlSchema<QueryResult.Value<Unit>>
  get() = RawdatyDatabaseImpl.Schema

internal fun KClass<RawdatyDatabase>.newInstance(driver: SqlDriver): RawdatyDatabase =
    RawdatyDatabaseImpl(driver)

private class RawdatyDatabaseImpl(
  driver: SqlDriver,
) : TransacterImpl(driver), RawdatyDatabase {
  override val rawdatyQueries: RawdatyQueries = RawdatyQueries(driver)

  public object Schema : SqlSchema<QueryResult.Value<Unit>> {
    override val version: Long
      get() = 1

    override fun create(driver: SqlDriver): QueryResult.Value<Unit> {
      driver.execute(null, """
          |CREATE TABLE auth_cache (
          |    id INTEGER NOT NULL PRIMARY KEY DEFAULT 1,
          |    user_id INTEGER NOT NULL,
          |    user_name TEXT NOT NULL DEFAULT '',
          |    user_email TEXT NOT NULL DEFAULT '',
          |    user_role TEXT NOT NULL DEFAULT '',
          |    tenant_slug TEXT NOT NULL DEFAULT '',
          |    avatar_url TEXT,
          |    cached_at TEXT NOT NULL DEFAULT ''
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE classes_cache (
          |    id INTEGER NOT NULL PRIMARY KEY,
          |    name TEXT NOT NULL,
          |    description TEXT,
          |    teacher_id INTEGER,
          |    teacher_name TEXT,
          |    children_count INTEGER NOT NULL DEFAULT 0,
          |    capacity INTEGER,
          |    academic_year TEXT NOT NULL DEFAULT '',
          |    created_at TEXT NOT NULL DEFAULT ''
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE children_cache (
          |    id INTEGER NOT NULL PRIMARY KEY,
          |    full_name TEXT NOT NULL,
          |    date_of_birth TEXT,
          |    gender TEXT NOT NULL DEFAULT '',
          |    photo_url TEXT,
          |    class_id INTEGER NOT NULL,
          |    class_name TEXT NOT NULL DEFAULT '',
          |    parent_id INTEGER,
          |    parent_name TEXT,
          |    parent_phone TEXT,
          |    enrollment_date TEXT NOT NULL DEFAULT '',
          |    stars INTEGER NOT NULL DEFAULT 0,
          |    notes TEXT,
          |    synced_at TEXT NOT NULL DEFAULT ''
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE attendance_cache (
          |    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    class_id INTEGER NOT NULL,
          |    child_id INTEGER NOT NULL,
          |    child_name TEXT NOT NULL DEFAULT '',
          |    date TEXT NOT NULL,
          |    status TEXT NOT NULL DEFAULT 'present',
          |    notes TEXT,
          |    sync_status TEXT NOT NULL DEFAULT 'synced'
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE news_cache (
          |    id INTEGER NOT NULL PRIMARY KEY,
          |    title TEXT NOT NULL,
          |    body TEXT NOT NULL,
          |    image_url TEXT,
          |    author_name TEXT NOT NULL DEFAULT '',
          |    is_pinned INTEGER NOT NULL DEFAULT 0,
          |    created_at TEXT NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE notifications_cache (
          |    id INTEGER NOT NULL PRIMARY KEY,
          |    title TEXT NOT NULL,
          |    body TEXT NOT NULL,
          |    type TEXT NOT NULL DEFAULT 'general',
          |    is_read INTEGER NOT NULL DEFAULT 0,
          |    created_at TEXT NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE complaints_cache (
          |    id INTEGER NOT NULL PRIMARY KEY,
          |    title TEXT NOT NULL,
          |    body TEXT NOT NULL,
          |    status TEXT NOT NULL DEFAULT 'pending',
          |    parent_id INTEGER NOT NULL DEFAULT 0,
          |    parent_name TEXT NOT NULL DEFAULT '',
          |    created_at TEXT NOT NULL,
          |    reply TEXT
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE messages_cache (
          |    id INTEGER NOT NULL PRIMARY KEY,
          |    conversation_id INTEGER NOT NULL,
          |    sender_id INTEGER NOT NULL,
          |    sender_name TEXT NOT NULL DEFAULT '',
          |    content TEXT NOT NULL,
          |    image_url TEXT,
          |    is_read INTEGER NOT NULL DEFAULT 0,
          |    sent_at TEXT NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE offline_actions (
          |    id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
          |    action_type TEXT NOT NULL,
          |    payload TEXT NOT NULL,
          |    created_at TEXT NOT NULL,
          |    retry_count INTEGER NOT NULL DEFAULT 0
          |)
          """.trimMargin(), 0)
      return QueryResult.Unit
    }

    override fun migrate(
      driver: SqlDriver,
      oldVersion: Long,
      newVersion: Long,
      vararg callbacks: AfterVersion,
    ): QueryResult.Value<Unit> = QueryResult.Unit
  }
}
