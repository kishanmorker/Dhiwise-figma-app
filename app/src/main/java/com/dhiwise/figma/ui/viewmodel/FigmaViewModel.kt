package com.dhiwise.figma.ui.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dhiwise.figma.R
import com.dhiwise.figma.apiController.ApiResults
import com.dhiwise.figma.apiController.isInternetAvailable
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

class FigmaViewModel : ViewModel() {
    private val repository = FigmaRepository()

    private val _figmaResult = MutableLiveData<ApiResults>()
    val figmaResult: LiveData<ApiResults> get() = _figmaResult

    fun fetchFigmaFile(mContext: Context, fileId: String, figmaToken: String) {
        viewModelScope.launch {
            _figmaResult.value = ApiResults.Loading

            if (isInternetAvailable(mContext)) {
                try {
                    val response = repository.getFigmaFile(fileId, figmaToken)
                    _figmaResult.value = ApiResults.Success(response)
                } catch (e: HttpException) {
                    _figmaResult.value = ApiResults.Error("HTTP Error: ${e.code()}")
                } catch (e: IOException) {
                    _figmaResult.value = ApiResults.Error("Network Error: ${e.message}")
                } catch (e: Exception) {
                    _figmaResult.value = ApiResults.Error("Unknown Error: ${e.message}")
                }
            } else {
                _figmaResult.value =
                    ApiResults.Error(mContext.getString(R.string.no_internet_connection))
            }
        }
    }
}
