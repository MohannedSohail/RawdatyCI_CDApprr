package com.rawdaty.db

import kotlin.Long
import kotlin.String

public data class Messages_cache(
  public val id: Long,
  public val conversation_id: Long,
  public val sender_id: Long,
  public val sender_name: String,
  public val content: String,
  public val image_url: String?,
  public val is_read: Long,
  public val sent_at: String,
)
