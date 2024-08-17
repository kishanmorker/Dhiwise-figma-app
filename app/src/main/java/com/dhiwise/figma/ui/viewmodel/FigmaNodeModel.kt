package com.dhiwise.figma.ui.viewmodel

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class FigmaNodeModel(
    @SerializedName("document") var document: Document? = Document(),

    @SerializedName("styles") var styles: Styles? = Styles(), //check this one ????

    @SerializedName("name") var name: String? = "", //Not imp
    @SerializedName("linkAccess") var linkAccess: String? = "", //Not imp
    @SerializedName("componentSets") var componentSets: ComponentSets? = ComponentSets(), //Not imp
    @SerializedName("components") var components: Components? = Components(), //Not imp
    @SerializedName("editorType") var editorType: String? = "", //Not imp
    @SerializedName("lastModified") var lastModified: String? = "", //Not imp
    @SerializedName("role") var role: String? = "", //Not imp
    @SerializedName("schemaVersion") var schemaVersion: Int? = 0, //Not imp
    @SerializedName("thumbnailUrl") var thumbnailUrl: String? = "", //Not imp
    @SerializedName("version") var version: String? = "" //Not imp
) {

    @Keep
    data class Document(
        @SerializedName("children") var children: List<Children?>? = listOf(),
        @SerializedName("id") var id: String? = "",
        @SerializedName("name") var name: String? = "",
        @SerializedName("scrollBehavior") var scrollBehavior: String? = "",
        @SerializedName("type") var type: String? = ""
    ) {
        @Keep
        data class Children(
            @SerializedName("flowStartingPoints") var flowStartingPoints: List<Any?>? = listOf(),
            @SerializedName("prototypeDevice") var prototypeDevice: PrototypeDevice? = PrototypeDevice(),
            @SerializedName("prototypeStartNodeID") var prototypeStartNodeID: Any? = Any(),

            @SerializedName("absoluteBoundingBox") var absoluteBoundingBox: AbsoluteBoundingBox? = AbsoluteBoundingBox(),
            @SerializedName("absoluteRenderBounds") var absoluteRenderBounds: AbsoluteRenderBounds? = AbsoluteRenderBounds(),
            @SerializedName("background") var background: List<Background?>? = listOf(),
            @SerializedName("backgroundColor") var backgroundColor: Color? = Color(),
            @SerializedName("blendMode") var blendMode: String? = "",
            @SerializedName("children") var children: List<Children?>? = listOf(),
            @SerializedName("clipsContent") var clipsContent: Boolean? = false,
            @SerializedName("constraints") var constraints: Constraints? = Constraints(),
            @SerializedName("effects") var effects: List<Any?>? = listOf(),
            @SerializedName("exportSettings") var exportSettings: List<ExportSetting?>? = listOf(),
            @SerializedName("fills") var fills: List<Fill?>? = listOf(),
            @SerializedName("id") var id: String? = "",
            @SerializedName("name") var name: String? = "",
            @SerializedName("scrollBehavior") var scrollBehavior: String? = "",
            @SerializedName("strokeAlign") var strokeAlign: String? = "",
            @SerializedName("strokeWeight") var strokeWeight: Double? = 0.0,
            @SerializedName("strokes") var strokes: List<Any?>? = listOf(),
            @SerializedName("style") var textStyles: TextStyles? = TextStyles(),
            @SerializedName("type") var type: String? = "",
            @SerializedName("characters") var characters: String? = "",
            @SerializedName("isFixed") var isFixed: Boolean? = false,
        ) {

            @Keep
            data class AbsoluteBoundingBox(
                @SerializedName("height") var height: Double? = 0.0,
                @SerializedName("width") var width: Double? = 0.0,
                @SerializedName("x") var x: Double? = 0.0,
                @SerializedName("y") var y: Double? = 0.0
            )

            @Keep
            data class AbsoluteRenderBounds(
                @SerializedName("height") var height: Double? = 0.0,
                @SerializedName("width") var width: Double? = 0.0,
                @SerializedName("x") var x: Double? = 0.0,
                @SerializedName("y") var y: Double? = 0.0
            )

            @Keep
            data class Background(
                @SerializedName("blendMode") var blendMode: String? = "",
                @SerializedName("color") var color: Color? = Color(),
                @SerializedName("type") var type: String? = ""
            )

            @Keep
            data class Constraints(
                @SerializedName("horizontal") var horizontal: String? = "",
                @SerializedName("vertical") var vertical: String? = ""
            )

            @Keep
            data class Fill(
                @SerializedName("blendMode") var blendMode: String? = "",
                @SerializedName("color") var color: Color? = Color(),
                @SerializedName("type") var type: String? = ""
            )

            @Keep
            data class TextStyles(
                @SerializedName("fontFamily") var fontFamily: String? = "",
                @SerializedName("fontPostScriptName") var fontPostScriptName: String? = "",
                @SerializedName("fontSize") var fontSize: Double? = 0.0,
                @SerializedName("fontWeight") var fontWeight: Int? = 0,
                @SerializedName("letterSpacing") var letterSpacing: Double? = 0.0,
                @SerializedName("lineHeightPercent") var lineHeightPercent: Double? = 0.0,
                @SerializedName("lineHeightPx") var lineHeightPx: Double? = 0.0,
                @SerializedName("lineHeightUnit") var lineHeightUnit: String? = "",
                @SerializedName("textAlignHorizontal") var textAlignHorizontal: String? = "",
                @SerializedName("textAlignVertical") var textAlignVertical: String? = "",
                @SerializedName("textAutoResize") var textAutoResize: String? = ""
            )

            @Keep
            data class Color(
                @SerializedName("a") var a: Double? = 0.0,
                @SerializedName("b") var b: Double? = 0.0,
                @SerializedName("g") var g: Double? = 0.0,
                @SerializedName("r") var r: Double? = 0.0
            )


            @Keep
            data class PrototypeDevice(
                @SerializedName("rotation") var rotation: String? = "",
                @SerializedName("type") var type: String? = ""
            )

            @Keep
            data class ExportSetting(
                @SerializedName("constraint") var constraint: Constraint? = Constraint(),
                @SerializedName("format") var format: String? = "",
                @SerializedName("suffix") var suffix: String? = ""
            ) {
                @Keep
                data class Constraint(
                    @SerializedName("type") var type: String? = "",
                    @SerializedName("value") var value: Double? = 0.0
                )
            }
        }
    }

    @Keep
    data class Styles(
        @SerializedName("0:3") var x03: X03? = X03()
    ) {
        @Keep
        data class X03(
            @SerializedName("description") var description: String? = "",
            @SerializedName("key") var key: String? = "",
            @SerializedName("name") var name: String? = "",
            @SerializedName("remote") var remote: Boolean? = false,
            @SerializedName("styleType") var styleType: String? = ""
        )
    }


    @Keep
    class ComponentSets

    @Keep
    class Components
}