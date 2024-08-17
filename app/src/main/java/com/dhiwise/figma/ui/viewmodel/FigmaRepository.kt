package com.dhiwise.figma.ui.viewmodel

import com.dhiwise.figma.apiController.RetrofitInstance

class FigmaRepository {
    suspend fun getFigmaFile(
        fileId: String,
        figmaToken: String,
    ): FigmaNodeModel {
        return RetrofitInstance.api.getFigmaFile(fileId, figmaToken)
    }
}
