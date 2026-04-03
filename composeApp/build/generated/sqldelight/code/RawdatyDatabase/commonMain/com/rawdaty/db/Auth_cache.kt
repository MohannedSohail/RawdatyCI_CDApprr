package com.rawdaty.db

import kotlin.Long
import kotlin.String

public data class Auth_cache(
  public val id: Long,
  public val user_id: Long,
  public val user_name: String,
  public val user_email: String,
  public val user_role: String,
  public val tenant_slug: String,
  public val avatar_url: String?,
  public val cached_at: String,
)
