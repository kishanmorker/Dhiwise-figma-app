package com.dhiwise.figma.apiController

import com.dhiwise.figma.ui.viewmodel.FigmaNodeModel
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface ApiService {
    @GET("v1/files/{file_id}")
    suspend fun getFigmaFile(
        @Path("file_id") fileId: String,
        @Header("X-Figma-Token") figmaToken: String,
    ): FigmaNodeModel
}