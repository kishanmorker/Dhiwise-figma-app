package com.dhiwise.figma.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dhiwise.figma.ui.viewmodel.FigmaNodeModel
import com.dhiwise.figma.databinding.ActivityFigmaViewsBinding
import com.dhiwise.figma.utils.fromJson

class FigmaViewsActivity : AppCompatActivity() {
    private val TAG = javaClass.simpleName
    private lateinit var mBinding: ActivityFigmaViewsBinding
    private lateinit var mContext: Context

    private var figmaScreenData: FigmaNodeModel.Document.Children? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityFigmaViewsBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mContext = this

        if (intent.hasExtra(FIGMA_SCREEN_DATA)) {
            val jsonData = intent.getStringExtra(FIGMA_SCREEN_DATA)
            figmaScreenData = jsonData?.fromJson<FigmaNodeModel.Document.Children>()
            renderFigmaUI(figmaScreenData)
        }
    }

    private fun renderFigmaUI(jsonData: FigmaNodeModel.Document.Children?) {
        val (offsetX, offsetY) = calculateOffsets(jsonData)
        val rootView = createViewFromJson(this, jsonData, offsetX, offsetY)
        mBinding.main.addView(rootView)
    }

    private fun createViewFromJson(
        context: Context,
        node: FigmaNodeModel.Document.Children?,
        offsetX: Float,
        offsetY: Float,
        parentType: String? = null,
    ): View? {

        if (node?.type == null) return null
        val type = node.type
        val bounds = node.absoluteBoundingBox
        Log.i(TAG, "createViewFromJson: node type:$type, name:${node.name}, bounds:$bounds")
        return when (type) {
            "RECTANGLE" -> {
                createRectangleView(context, node, offsetX, offsetY)
            }

            "TEXT" -> {

                if (parentType == null) {
                    createTextView(context, node, offsetX, offsetY)
                } else {
                    if (parentType == "Form" || parentType == "email" || parentType == "password" || parentType.contains(
                            "Input:EditText",
                            true
                        )
                    ) {
                        createEditText(context, node, offsetX, offsetY)
                    } else {
                        createTextView(context, node, offsetX, offsetY)
                    }
                }
            }

            "FRAME", "GROUP" -> {
                createFrameView(context, node, offsetX, offsetY)
            }

            "VECTOR" -> createVectorView(context, node, offsetX, offsetY)
            "BOOLEAN_OPERATION" -> createBooleanOperationView(
                context, node, offsetX, offsetY
            )

            else -> View(context) // Default view for unsupported types
        }
    }

    private fun createEditText(
        context: Context, node: FigmaNodeModel.Document.Children?, offsetX: Float, offsetY: Float
    ): EditText? {
        if (node?.type == null) return null

        //TODO: draw proper EDITTEXT style-> ROUNDED RECTANGLE, COLOR, FONT, SHAPES ETC

        val editText = EditText(context)
        editText.id = View.generateViewId() // Generate a unique ID
        editText.textSize = node.textStyles?.fontSize?.toFloat() ?: 12f
        editText.setTextColor(Color.BLACK) // Set a default color or extract from JSON

        editText.setBackgroundColor(Color.GRAY) // Default background color

        editText.hint = node.characters?.ifEmpty { node.name }

        val bounds = node.absoluteBoundingBox

        val layoutParams = LinearLayout.LayoutParams(
            bounds?.width?.toInt() ?: 0, bounds?.height?.toInt() ?: 0
        )

        val offsetDataX = ((bounds?.x?.toInt() ?: 0) - offsetX.toInt())
        val offsetDataY = ((bounds?.y?.toInt() ?: 0) - offsetY.toInt())
        Log.i(
            TAG,
            ":VIEW_DATA: createEditText: node type:${node.type},name:${node.name}, :OFFSET_DATA: X:$offsetDataX, Y:$offsetDataY"
        )
        layoutParams.leftMargin = offsetDataX
        layoutParams.topMargin = offsetDataY
        editText.layoutParams = layoutParams
        return editText
    }

    private fun createRectangleView(
        context: Context, node: FigmaNodeModel.Document.Children?, offsetX: Float, offsetY: Float
    ): View? {
        if (node?.type == null) return null
        val view = View(context)
        view.id = View.generateViewId() // Generate a unique ID
        val color =
            if (!node.fills.isNullOrEmpty()) node.fills!![0]?.color else FigmaNodeModel.Document.Children.Color(
                1.0, 1.0, 1.0, 1.0
            )
        // Set the background color based on the color object
        val backgroundColor = Color.argb(
            ((color?.a ?: 1.0) * 255).toInt(),
            ((color?.r ?: 1.0) * 255).toInt(),
            ((color?.g ?: 1.0) * 255).toInt(),
            ((color?.b ?: 1.0) * 255).toInt()
        )
        view.setBackgroundColor(backgroundColor)

        val bounds = node.absoluteBoundingBox
        val layoutParams = LinearLayout.LayoutParams(
            bounds?.width?.toInt() ?: 0, bounds?.height?.toInt() ?: 0
        )

        val offsetDataX = ((bounds?.x?.toInt() ?: 0) - offsetX.toInt())
        val offsetDataY = ((bounds?.y?.toInt() ?: 0) - offsetY.toInt())
        Log.i(
            TAG,
            "createRectangleView: node type:${node.type},name:${node.name}, :OFFSET_DATA: X:$offsetDataX, Y:$offsetDataY"
        )
        layoutParams.leftMargin = offsetDataX
        layoutParams.topMargin = offsetDataY
        view.layoutParams = layoutParams
        return view
    }

    private fun createTextView(
        context: Context, node: FigmaNodeModel.Document.Children?, offsetX: Float, offsetY: Float
    ): TextView? {
        if (node?.type == null) return null
        val textView = TextView(context)
        textView.id = View.generateViewId() // Generate a unique ID


        textView.text = node.characters?.ifBlank { node.name }
        textView.textSize = node.textStyles?.fontSize?.toFloat() ?: 12f
        textView.setTextColor(Color.BLACK) // Set a default color or extract from JSON

        val bounds = node.absoluteBoundingBox

        val layoutParams = LinearLayout.LayoutParams(
            bounds?.width?.toInt() ?: 0, bounds?.height?.toInt() ?: 0
        )

        val offsetDataX = ((bounds?.x?.toInt() ?: 0) - offsetX.toInt())
        val offsetDataY = ((bounds?.y?.toInt() ?: 0) - offsetY.toInt())
        Log.i(
            TAG,
            ":VIEW_DATA: createTextView: node type:${node.type}, name:${node.name}, :OFFSET_DATA: X:$offsetDataX, Y:$offsetDataY, text Style: ${node.textStyles}"
        )
        layoutParams.leftMargin = offsetDataX
        layoutParams.topMargin = offsetDataY
        textView.layoutParams = layoutParams
        return textView
    }

    private fun createFrameView(
        context: Context, node: FigmaNodeModel.Document.Children?, offsetX: Float, offsetY: Float
    ): FrameLayout? {
        if (node?.type == null) return null
        val layout = FrameLayout(context)
        layout.id = View.generateViewId() // Generate a unique ID

        if (node.name == "Button" || node.name?.contains("Button /", true) == true) {
            layout.setOnClickListener {
                var message = "Button Clicked..."
                if (node.children?.isNotEmpty() == true) {
                    message =
                        node.children?.find { it?.type == "TEXT" }?.characters?.ifEmpty { node.name }
                            .toString()
                }
                Log.v(TAG, "createButton: on Button Click:${message}")
                Toast.makeText(mContext, "$message Button Clicked...", Toast.LENGTH_SHORT).show()
            }
        }

        val children = node.children
        if (!children.isNullOrEmpty()) {
            children.forEach { child ->
                mBinding.main.addView(
                    createViewFromJson(
                        context, child, offsetX, offsetY, parentType = node.name
                    )
                )
            }
        }

        val bounds = node.absoluteBoundingBox
        val layoutParams = LinearLayout.LayoutParams(
            bounds?.width?.toInt() ?: 0, bounds?.height?.toInt() ?: 0
        )

        val offsetDataX = ((bounds?.x?.toInt() ?: 0) - offsetX.toInt())
        val offsetDataY = ((bounds?.y?.toInt() ?: 0) - offsetY.toInt())
        Log.i(
            TAG,
            "createFrameView: node type:${node.type},name:${node.name}, :OFFSET_DATA: X:$offsetDataX, Y:$offsetDataY"
        )
        layoutParams.leftMargin = offsetDataX
        layoutParams.topMargin = offsetDataY
        layout.layoutParams = layoutParams
        return layout
    }

    private fun createVectorView(
        context: Context, node: FigmaNodeModel.Document.Children?, offsetX: Float, offsetY: Float
    ): View? {
        if (node?.type == null) return null
        // for a simple vector, using ImageView and loading a vector drawable
        val imageView = ImageView(context)
        imageView.id = View.generateViewId() // Generate a unique ID
        // Load vector drawable here, or create vector dynamically based on jsonObject data
        imageView.setBackgroundColor(Color.GRAY)

        val bounds = node.absoluteBoundingBox

        val layoutParams = LinearLayout.LayoutParams(
            bounds?.width?.toInt() ?: 0, bounds?.height?.toInt() ?: 0
        )

        val offsetDataX = ((bounds?.x?.toInt() ?: 0) - offsetX.toInt())
        val offsetDataY = ((bounds?.y?.toInt() ?: 0) - offsetY.toInt())
        Log.i(
            TAG,
            ":VIEW_DATA: createVectorView: node type:${node.type},name:${node.name}, :OFFSET_DATA: X:$offsetDataX, Y:$offsetDataY"
        )

        layoutParams.leftMargin = offsetDataX
        layoutParams.topMargin = offsetDataY
        imageView.layoutParams = layoutParams
        return imageView
    }

    private fun createBooleanOperationView(
        context: Context, node: FigmaNodeModel.Document.Children?, offsetX: Float, offsetY: Float
    ): View? {
        if (node?.type == null) return null
        // For BOOLEAN_OPERATION, we will just create a placeholder view
        val view = View(context)
        view.id = View.generateViewId() // Generate a unique ID
        view.setBackgroundColor(Color.DKGRAY)

        val bounds = node.absoluteBoundingBox

        val layoutParams = LinearLayout.LayoutParams(
            bounds?.width?.toInt() ?: 0, bounds?.height?.toInt() ?: 0
        )

        val offsetDataX = ((bounds?.x?.toInt() ?: 0) - offsetX.toInt())
        val offsetDataY = ((bounds?.y?.toInt() ?: 0) - offsetY.toInt())

        Log.i(
            TAG,
            ":VIEW_DATA: createBooleanOperationView: node type:${node.type},name:${node.name}, :OFFSET_DATA: X:$offsetDataX, Y:$offsetDataY"
        )
        layoutParams.leftMargin = offsetDataX
        layoutParams.topMargin = offsetDataY
        view.layoutParams = layoutParams
        return view
    }


    private fun calculateOffsets(
        node: FigmaNodeModel.Document.Children?
    ): Pair<Float, Float> {
        var minX = Float.MAX_VALUE
        var minY = Float.MAX_VALUE

        fun findMinCoordinates(node: FigmaNodeModel.Document.Children?) {
            val bounds = node?.absoluteBoundingBox
            if (bounds != null) {
                val x = bounds.x?.toFloat() ?: 1F
                val y = bounds.y?.toFloat() ?: 1F
                if (x < minX) minX = x
                if (y < minY) minY = y
            }
            val children = node?.children
            if (children != null) {
                for (i in children.indices) {
                    findMinCoordinates(children[i])
                }
            }
        }

        findMinCoordinates(node)
        return Pair(minX, minY)
    }

    companion object {
        const val FIGMA_SCREEN_DATA = "FIGMA_SCREEN_DATA"
    }

}
