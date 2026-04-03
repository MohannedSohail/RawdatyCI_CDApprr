package com.rawdaty.db

import kotlin.Long
import kotlin.String

public data class News_cache(
  public val id: Long,
  public val title: String,
  public val body: String,
  public val image_url: String?,
  public val author_name: String,
  public val is_pinned: Long,
  public val created_at: String,
)
