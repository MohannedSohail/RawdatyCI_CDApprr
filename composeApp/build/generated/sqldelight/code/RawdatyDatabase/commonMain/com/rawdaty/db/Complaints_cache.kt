package com.rawdaty.db

import kotlin.Long
import kotlin.String

public data class Complaints_cache(
  public val id: Long,
  public val title: String,
  public val body: String,
  public val status: String,
  public val parent_id: Long,
  public val parent_name: String,
  public val created_at: String,
  public val reply: String?,
)
