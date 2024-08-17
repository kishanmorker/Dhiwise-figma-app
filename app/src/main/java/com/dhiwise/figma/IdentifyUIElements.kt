package com.dhiwise.figma

import android.util.Log
import com.dhiwise.figma.utils.toJsonString
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject

private val TAG = "IdentifyUIElements"
private var finalJsonArray = JsonArray()

//method to create custom json hierarchy of Android Views
fun createCustomJsonHierarchy(nodeElement: JsonObject): JsonObject {
    val customJson = JsonObject()
    //if element has children then parse them
    if (nodeElement.has("children") && !nodeElement.getAsJsonArray("children").isEmpty) {
        nodeElement.getAsJsonArray("children").forEach { element ->
            parseElement(element.asJsonObject) //for the root element pass null as parentType (which is Default value)
        }
    }
    //add main array to json object property:"elements"
    customJson.add("elements", finalJsonArray)
    Log.i(TAG, "createCustomJsonHierarchy: mainArray:${finalJsonArray.toJsonString()}")
    return customJson
}

fun parseElement(nodeElement: JsonObject, parentType: String? = null) {
    val type = nodeElement.get("type").asString
    val customElement = JsonObject()

    //here parent type is used to identifying the type of element. which is sent to children from it's parent.
    Log.i(TAG, "parseElement: parentType->$parentType")

    when {
        //if type is "TEXT" then check it's parent type for identifying the type of element.
        //because it could be of type: EditText,Button or simply a TextView.
        type == "TEXT" -> {
            if (parentType == null) {
                //if parentType is null then it is a TextView.
                customElement.addProperty("type", "TextView")
            } else {
                //if parentType is not null then it is a EditText or Button or TextView.
                if (parentType == "Form" || parentType == "email" || parentType == "password" || parentType.contains(
                        "Input:EditText",
                        true
                    )
                ) {
                    //if parentType is "Form" or "Input:EditText" then it is a EditText.
                    customElement.addProperty("type", "EditText")
                } else if (parentType == "Button" || parentType.contains("Button /", true)) {
                    //if parentType is "Button" or contains "Button /" then it is a Button.
                    customElement.addProperty("type", "Button")
                } else {
                    //else it is a TextView.
                    customElement.addProperty("type", "TextView")
                }
            }
        }

        type == "GROUP" && (parentType?.contains(
            "Icon /",
            true
        ) == true || parentType?.contains("Button /", true) == true) -> {
            //if type is "GROUP" and parentType is "Icon /" or "Button /" then it is a Icon.
            customElement.addProperty("type", "Icon")
        }

        type == "IMAGE" -> {
            //if type is "IMAGE" then it is a ImageView.
            customElement.addProperty("type", "Image")
        }

        else -> return //if Unknown Element found then simply discard it's data
    }

    //if element is identified then set other properties
    customElement.addProperty("id", nodeElement.get("id").asString)
    customElement.addProperty("name", nodeElement.get("name").asString)
    customElement.add("properties", parseProperties(nodeElement))

    //add element to main JsonArray
    finalJsonArray.add(customElement)

    //if element has children then parse them
    if (nodeElement.has("children") && !nodeElement.getAsJsonArray("children").isEmpty) {
        nodeElement.getAsJsonArray("children").forEach { childElement ->
            parseElement(childElement.asJsonObject, nodeElement.get("name").asString)
        }
    }
}

//method to parse properties of element
fun parseProperties(element: JsonObject): JsonObject {
    val properties = JsonObject()
    try {
        properties.addProperty("hexColorCode", getHexColor(element))
        properties.add("width", getJsonData(element, "absoluteBoundingBox", "width"))
        properties.add("height", getJsonData(element, "absoluteBoundingBox", "height"))
        properties.add("background", element.get("fills"))
        properties.add("textStyle", element.get("style") ?: JsonObject())
    } catch (e: Exception) {
        Log.e(TAG, "parseProperties: error:${e.message}")
    }
    return properties
}

//method to get hex color code of element using fills property
fun getHexColor(element: JsonObject): String {
    return if (element.has("fills") && !element.getAsJsonArray("fills").isEmpty && element.getAsJsonArray(
            "fills"
        ).get(0).asJsonObject.has("color")
    ) {
        val color = element.getAsJsonArray("fills").get(0).asJsonObject.get("color").asJsonObject
        return rgbaToHex(
            color.get("r").asDouble,
            color.get("g").asDouble,
            color.get("b").asDouble,
            color.get("a").asDouble,
        )
    } else {
        ""
    }
}

//method to convert rgba to hex
fun rgbaToHex(r: Double, g: Double, b: Double, a: Double): String {
    val updatedR = (r * 255).toInt()
    val updatedG = (g * 255).toInt()
    val updatedB = (b * 255).toInt()
    val updatedA = (a * 255).toInt()

    fun Int.toHexString(): String {
        val hex = Integer.toHexString(this)
        return if (hex.length == 1) "0$hex" else hex
    }

    return "#${updatedR.toHexString()}${updatedG.toHexString()}${updatedB.toHexString()}${updatedA.toHexString()}"
}

//method to get json data from json object
fun getJsonData(element: JsonObject, parentKey: String, childKey: String): JsonElement? {
    return if (element.has(parentKey)) {
        element.getAsJsonObject(parentKey).get(childKey)
    } else {
        null
    }
}
