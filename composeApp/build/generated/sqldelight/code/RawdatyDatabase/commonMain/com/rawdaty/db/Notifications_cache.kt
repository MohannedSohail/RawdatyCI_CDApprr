package com.rawdaty.db

import kotlin.Long
import kotlin.String

public data class Notifications_cache(
  public val id: Long,
  public val title: String,
  public val body: String,
  public val type: String,
  public val is_read: Long,
  public val created_at: String,
)
