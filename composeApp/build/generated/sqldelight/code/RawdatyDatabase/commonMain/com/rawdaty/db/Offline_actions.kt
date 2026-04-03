package com.rawdaty.db

import kotlin.Long
import kotlin.String

public data class Offline_actions(
  public val id: Long,
  public val action_type: String,
  public val payload: String,
  public val created_at: String,
  public val retry_count: Long,
)
