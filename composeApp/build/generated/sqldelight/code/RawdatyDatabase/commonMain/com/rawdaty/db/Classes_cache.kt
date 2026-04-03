package com.rawdaty.db

import kotlin.Long
import kotlin.String

public data class Classes_cache(
  public val id: Long,
  public val name: String,
  public val description: String?,
  public val teacher_id: Long?,
  public val teacher_name: String?,
  public val children_count: Long,
  public val capacity: Long?,
  public val academic_year: String,
  public val created_at: String,
)
