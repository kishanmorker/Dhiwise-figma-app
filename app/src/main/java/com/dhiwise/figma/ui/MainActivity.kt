package com.dhiwise.figma.ui

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.dhiwise.figma.ui.viewmodel.FigmaNodeModel
import com.dhiwise.figma.R
import com.dhiwise.figma.ui.FigmaViewsActivity.Companion.FIGMA_SCREEN_DATA
import com.dhiwise.figma.apiController.ApiResults
import com.dhiwise.figma.ui.viewmodel.FigmaViewModel
import com.dhiwise.figma.apiController.isInternetAvailable
import com.dhiwise.figma.createCustomJsonHierarchy
import com.dhiwise.figma.databinding.ActivityMainBinding
import com.dhiwise.figma.utils.PermissionType
import com.dhiwise.figma.utils.checkDexterPermissions
import com.dhiwise.figma.utils.deleteExistingFolder
import com.dhiwise.figma.utils.fromJson
import com.dhiwise.figma.utils.readJsonFromFile
import com.dhiwise.figma.utils.saveJsonToFile
import com.dhiwise.figma.utils.toJsonString
import com.dhiwise.figma.utils.toast
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//activity for fetching figma file data
//and processing it to get SignUp and Login screen data
//and identifying it's view type
//and creating a custom json hierarchy for each screen
class MainActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mContext: Context
    private val gson = Gson()

    private val viewModel: FigmaViewModel by viewModels()
    private var signUpScreenData: FigmaNodeModel.Document.Children? = null
    private var loginScreenData: FigmaNodeModel.Document.Children? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        mContext = this
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) askPermissions()

        initView()
    }

    private fun initView() {
        with(mBinding) {
            btnSubmit.setOnClickListener {
                if (validateDetails()) {
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
                        askPermissions(isMandatory = false, onAllPermissionGranted = {
                            //if permission granted/fetch figma file data
                            fetchFigmaFileData(
                                edtFigmaLink.text.toString(), edtFigmaToken.text.toString()
                            )
                        })
                    } else {
                        //ssssfetch figma file data
                        fetchFigmaFileData(
                            edtFigmaLink.text.toString(), edtFigmaToken.text.toString()
                        )
                    }
                }
            }

            tvUseDefaults.setOnClickListener {
                edtFigmaLink.setText(getString(R.string.default_figma_link))
                edtFigmaToken.setText(getString(R.string.default_figma_token))
            }

            tvRenderLoginScreen.setOnClickListener {
                if (loginScreenData == null) return@setOnClickListener
                val intent = Intent(mContext, FigmaViewsActivity::class.java)
                intent.putExtra(FIGMA_SCREEN_DATA, loginScreenData!!.toJsonString())
                startActivity(intent)
            }

            tvRenderSignUpScreen.setOnClickListener {
                if (signUpScreenData == null) return@setOnClickListener
                val intent = Intent(mContext, FigmaViewsActivity::class.java)
                intent.putExtra(FIGMA_SCREEN_DATA, signUpScreenData!!.toJsonString())
                startActivity(intent)
            }
        }
    }

    //method to fetch figma file data from figma API asynchronously
    private fun fetchFigmaFileData(figmaLink: String, figmaToken: String) {
        resetData()
        with(mBinding) {
            clFigmaJsonLocation.isVisible = false
            clLoginJsonLocation.isVisible = false
            clSignUpJsonLocation.isVisible = false
            tvRenderLoginScreen.isVisible = false
            tvRenderSignUpScreen.isVisible = false
        }
        val figmaFileId = getFigmaFileId(figmaLink) ?: ""

        Log.i(TAG, "fetchFigmaFileData: figmaLink:$figmaLink")
        Log.i(TAG, "fetchFigmaFileData: figmaFileId:$figmaFileId")

        // Fetch the Figma file json data from figma api
        if (isInternetAvailable(mContext)) {
            viewModel.fetchFigmaFile(mContext, figmaFileId, figmaToken)
        } else {
            toast(getString(R.string.no_internet_connection))
        }
        // Observe the LiveData from the ViewModel
        viewModel.figmaResult.observe(this) { result ->
            when (result) {
                is ApiResults.Loading -> {
                    // Show loader
                    mBinding.tvSubmit.text = getString(R.string.loading)
                    mBinding.progressBar.isVisible = true
                }

                is ApiResults.Success -> {
                    // Handle Api success response
                    // store response json to mobile storage:
                    // Because we are storing it in Mobile's Public directory -> Documents/dhiwise
                    // Android 13 and up, We do not require storage
                    // read,write permissions for accessing Public directories.
                    processFigmaResponse(result.data)
                }

                is ApiResults.Error -> {
                    toast(result.message)
                    resetUI()
                }
            }
        }
    }

    //method to process figma response
    private fun processFigmaResponse(figmaResponse: FigmaNodeModel) {
        //manage figma output folder and if older data exists then delete existing folder
        deleteExistingFolder(mContext)
        Log.i(TAG, "fetchFigmaFileData: figmaResponse:$figmaResponse")
        mBinding.tvSubmit.text = getString(R.string.processing)

        //store figma response json to mobile storage
        //using kotlin coroutine to perform background task
        CoroutineScope(Dispatchers.Main).launch {
            val figmaFilePath = saveJsonToFile(
                mContext, figmaResponse.toJsonString(), JsonDataId.OriginalFigmaJson.ordinal
            ) ?: return@launch

            mBinding.clFigmaJsonLocation.isVisible = true
            mBinding.tvFigmaFileLocation.text = figmaFilePath

            // Read JSON file from storage
            val fileContent = readJsonFromFile(figmaFilePath)
            withContext(Dispatchers.Main) {
                if (fileContent != null) {
                    Log.d("FileContent", fileContent)
                    val figmaJsonData = fileContent.fromJson<FigmaNodeModel>()
                    try {
                        //here I'm getting SignUp and Login screen/node data by it's Node Id.
                        //It's statically defined in the code because there is no other way to get both screen's data.

                        loginScreenData =
                            figmaJsonData.document?.children?.get(0)?.children?.find { it?.id == "1409:1516" }
                        signUpScreenData =
                            figmaJsonData.document?.children?.get(0)?.children?.find { it?.id == "1409:1435" }

                        if (signUpScreenData == null || loginScreenData == null) {
                            toast(getString(R.string.screen_data_not_found))
                            resetUIPartially()
                        } else {
                            val signUpJsonPath = saveJsonToFile(
                                mContext, createCustomJsonHierarchy(
                                    gson.fromJson(
                                        signUpScreenData?.toJsonString(), JsonObject::class.java
                                    )
                                ).toJsonString(), JsonDataId.ProcessedSignUpScreenJson.ordinal
                            )

                            mBinding.clSignUpJsonLocation.isVisible = true
                            mBinding.tvSignUpLocation.text = signUpJsonPath

                            val loginJsonPath = saveJsonToFile(
                                mContext, createCustomJsonHierarchy(
                                    gson.fromJson(
                                        loginScreenData?.toJsonString(), JsonObject::class.java
                                    )
                                ).toJsonString(), JsonDataId.ProcessedLoginScreenJson.ordinal
                            )
                            mBinding.clLoginJsonLocation.isVisible = true
                            mBinding.tvLoginLocation.text = loginJsonPath

                            mBinding.progressBar.isVisible = false
                            mBinding.tvSubmit.text = getString(R.string.submit)

                            toast(getString(R.string.process_success))
                            mBinding.tvRenderLoginScreen.isVisible = true
                            mBinding.tvRenderSignUpScreen.isVisible = true
                        }

                    } catch (e: Exception) {
                        toast(getString(R.string.something_went_wrong))
                        resetUIPartially()
                        resetData()
                        Log.e(
                            TAG, "fetchFigmaFileData: error while parsing json data:${e.message}"
                        )
                    }

                } else {
                    resetData()
                    Log.e("FileContent", "No content read")
                    toast(getString(R.string.failed_to_read_file_content))
                }
            }
        }
    }

    private fun resetUI() {
        with(mBinding) {
            progressBar.isVisible = false
            clFigmaJsonLocation.isVisible = false
            clLoginJsonLocation.isVisible = false
            clSignUpJsonLocation.isVisible = false

            tvRenderLoginScreen.isVisible = false
            tvRenderSignUpScreen.isVisible = false

            tvSubmit.text = getString(R.string.submit)

            resetData()
        }
    }

    private fun resetUIPartially() {
        with(mBinding) {
            clLoginJsonLocation.isVisible = false
            clSignUpJsonLocation.isVisible = false
            tvRenderLoginScreen.isVisible = false
            tvRenderSignUpScreen.isVisible = false
            progressBar.isVisible = false
            tvSubmit.text = getString(R.string.submit)
        }
    }

    private fun resetData() {
        signUpScreenData = null
        loginScreenData = null
    }

    //method to get figma file id from link
    private fun getFigmaFileId(figmaLink: String): String? {
        if (!figmaLink.contains("https://www.figma.com", true)) {
            return figmaLink
        }

        val regex = if (figmaLink.contains("https://www.figma.com/file/", true)) {
            """https://www.figma.com/file/([a-zA-Z0-9]+)/""".toRegex()
        } else if (figmaLink.contains("https://www.figma.com/design/", true)) {
            """https://www.figma.com/design/([a-zA-Z0-9]+)/""".toRegex()
        } else {
            return figmaLink
        }

        // Regular expression to match the Figma file ID
        val matchResult = regex.find(figmaLink)
        return if (matchResult != null) {
            matchResult.groups[1]?.value
        } else {
            println("Figma file ID not found in the string.")
            figmaLink
        }
    }

    //method to validate user input
    private fun validateDetails(): Boolean {
        return if (mBinding.edtFigmaLink.text.toString().isBlank()) {
            toast(getString(R.string.please_enter_figma_link))
            false
        } else if (mBinding.edtFigmaToken.text.toString().isBlank()) {
            toast(getString(R.string.please_enter_figma_token))
            false
        } else true
    }

    //method to check storage permissions
    private fun askPermissions(
        isMandatory: Boolean = false, onAllPermissionGranted: (() -> Unit)? = null
    ) {
        checkDexterPermissions(
            mContext, PermissionType.StoragePermission.value, onAllPermissionGranted = {
                Log.d(TAG, "checkNotificationPer: onAllPermissionGranted")
                onAllPermissionGranted?.invoke()
            }, isMandatoryPer = isMandatory, permissionResultLauncher
        )
    }

    //permission launcher
    private val permissionResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            Handler(Looper.myLooper()!!).postDelayed({
                askPermissions()
            }, 0)
        }
}

enum class JsonDataId(id: Int) {
    OriginalFigmaJson(0), ProcessedSignUpScreenJson(1), ProcessedLoginScreenJson(2),
}