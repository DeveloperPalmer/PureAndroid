package ru.sla.pure.android.openapi.schema.api

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import ru.sla.pure.android.openapi.schema.model.CharacterNM
import ru.sla.pure.android.openapi.schema.model.CharacterPageNM

interface CharacterApi {
    @GET("character")
    suspend fun readPage(@Query("page") page: Int): CharacterPageNM

    @GET("character/{id}")
    suspend fun readCharacter(@Path("id") id: Int): CharacterNM
}
