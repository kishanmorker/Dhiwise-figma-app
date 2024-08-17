package com.dhiwise.figma.utils

import android.app.Activity
import android.content.Context
import android.os.Environment
import android.util.Log
import com.dhiwise.figma.ui.JsonDataId
import com.dhiwise.figma.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

private val TAG = "Utils"

fun saveJsonToFile(context: Context, jsonData: String, jsonDataId: Int): String? {

    val fileName = when (jsonDataId) {
        JsonDataId.OriginalFigmaJson.ordinal -> {
            "figma_api_response_${System.currentTimeMillis()}.json"
        }

        JsonDataId.ProcessedSignUpScreenJson.ordinal -> {
            "signup_screen_json_hierarchy_${System.currentTimeMillis()}.json"
        }

        JsonDataId.ProcessedLoginScreenJson.ordinal -> {
            "login_screen_json_hierarchy_${System.currentTimeMillis()}.json"
        }

        else -> "figma_api_response_${System.currentTimeMillis()}.json"
    }

    val downloadsFolder =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    val dhiwiseFolder = File(downloadsFolder, context.getString(R.string.app_folder_name))

    if (!dhiwiseFolder.exists()) {
        dhiwiseFolder.mkdirs()
    }

    val file = File(dhiwiseFolder, fileName)

    try {
        FileOutputStream(file).use { outputStream ->
            outputStream.write(jsonData.toByteArray())
        }
        return file.absolutePath
    } catch (e: IOException) {
        Log.e(TAG, "saveJsonToFile: failed to save:" + e.message)
        e.printStackTrace()
        (context as Activity).runOnUiThread { context.toast("Failed to save file") }
        return null
    }
}

fun deleteDirectory(file: File): Boolean {
    if (file.isDirectory) {
        val children = file.listFiles()
        if (children != null) {
            for (child in children) {
                deleteDirectory(child)
            }
        }
    }
    return file.delete()
}


suspend fun readJsonFromFile(figmaFilePath: String): String? {
    return withContext(Dispatchers.IO) {
        val jsonFile = File(figmaFilePath)

        if (!jsonFile.exists()) {
            Log.e("FileRead", "File not found")
            return@withContext null
        }

        try {
            FileInputStream(jsonFile).use { input ->
                val content = input.readBytes().toString(Charsets.UTF_8)
                Log.d("FileRead", "File content: $content")
                content
            }
        } catch (e: IOException) {
            null
        }
    }
}

fun deleteExistingFolder(mContext: Context) {
    val downloadsFolder =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    val dhiwiseFolder = File(downloadsFolder, mContext.getString(R.string.app_folder_name))

    deleteDirectory(dhiwiseFolder)
}