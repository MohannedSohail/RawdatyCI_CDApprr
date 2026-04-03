package com.rawdaty.db

import kotlin.Long
import kotlin.String

public data class Children_cache(
  public val id: Long,
  public val full_name: String,
  public val date_of_birth: String?,
  public val gender: String,
  public val photo_url: String?,
  public val class_id: Long,
  public val class_name: String,
  public val parent_id: Long?,
  public val parent_name: String?,
  public val parent_phone: String?,
  public val enrollment_date: String,
  public val stars: Long,
  public val notes: String?,
  public val synced_at: String,
)
