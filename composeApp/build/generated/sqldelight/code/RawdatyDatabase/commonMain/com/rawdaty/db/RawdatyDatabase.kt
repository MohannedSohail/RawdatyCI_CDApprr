package com.rawdaty.db

import app.cash.sqldelight.Transacter
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import com.rawdaty.db.composeApp.newInstance
import com.rawdaty.db.composeApp.schema
import kotlin.Unit

public interface RawdatyDatabase : Transacter {
  public val rawdatyQueries: RawdatyQueries

  public companion object {
    public val Schema: SqlSchema<QueryResult.Value<Unit>>
      get() = RawdatyDatabase::class.schema

    public operator fun invoke(driver: SqlDriver): RawdatyDatabase =
        RawdatyDatabase::class.newInstance(driver)
  }
}
