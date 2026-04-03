package com.rawdaty.db

import kotlin.Long
import kotlin.String

public data class Attendance_cache(
  public val id: Long,
  public val class_id: Long,
  public val child_id: Long,
  public val child_name: String,
  public val date: String,
  public val status: String,
  public val notes: String?,
  public val sync_status: String,
)
