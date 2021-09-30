package com.github.davblain.keemuntemplate.templates

import com.android.tools.idea.wizard.template.*
import com.android.tools.idea.wizard.template.impl.defaultPackageNameParameter

private fun featureParamsName(entityName: String): String = "${entityName}FeatureParams"
private fun featureState(entityName: String): String = "${entityName}State"
private fun featureViewState(entityName: String): String = "${entityName}ViewState"
private fun featureEffect(entityName: String): String = "${entityName}Effect"
private fun featureEffectHandlerType(entityName: String): String = "${entityName}EffectHandler"
private fun featureUpdateType(entityName: String): String = "${entityName}Update"

enum class MsgType {
    External, Internal
}

@OptIn(ExperimentalStdlibApi::class)
private fun featureUpdaterConstructor(entityName: String) =
    "${entityName.replaceFirstChar { it.lowercase() }}Updater()"

@OptIn(ExperimentalStdlibApi::class)
private fun featureEffectHandlerConstructor(entityName: String) =
    featureEffectHandlerType(entityName).replaceFirstChar { it.lowercase() }.plus("()")

private fun featureMsg(entityName: String): String = "${entityName}Msg"

private fun subTypeFeatureMsg(msgType: MsgType, entityName: String): String =
    "${entityName}${msgType}Msg"

private fun updaterType(msgType: MsgType, entityName: String): String =
    "Update<${featureState(entityName)}, ${subTypeFeatureMsg(msgType, entityName)}, ${featureEffect(entityName)}>"

@OptIn(ExperimentalStdlibApi::class)
private fun subUpdaterConstruction(msgType: MsgType, entityName: String): String = """fun ${msgType.name.replaceFirstChar { it.lowercase() }}Updater() = ${updaterType(msgType, entityName)} { msg,state ->
        when(msg) {
            TODO()
        }
      } 
""".trimIndent()


@OptIn(ExperimentalStdlibApi::class)
fun featureFile(
    packageName: String,
    entityName: String
) = """
    package ${packageName}.feature
    
    import android.os.Parcelable
    import kotlinx.parcelize.Parcelize
    import family.amma.keemun.InitFeature
    import family.amma.keemun.feature.FeatureParams
    

    typealias ${featureParamsName(entityName)} = FeatureParams<${featureState(entityName)}, ${entityName}Msg, ${featureEffect(entityName)}>

    /** Feature Params. */
    fun ${featureParamsName(entityName).replaceFirstChar { it.lowercase() }}(
        effectHandler: ${featureEffectHandlerType(entityName)}
    ): ${featureParamsName(entityName)} = FeatureParams(
        init = init(),
        update = ${featureUpdaterConstructor(entityName)},
        effectHandler = effectHandler
    )
    
    private fun init() = InitFeature<${entityName}State, ${entityName}Effect> { previous ->
        val state = previous?: ${featureState(entityName)}(s = "")
        state to emptySet()
    }
  
    
    @Parcelize
    data class ${featureState(entityName)}(val s:String) : Parcelable
    
    /** Доступные сообщения. */
    sealed class ${entityName}Msg
     
""".trimIndent()

fun updaterFile(
    packageName: String,
    entityName: String
) = """
    package ${packageName}.feature
    
    import family.amma.keemun.Update
    
    typealias ${featureUpdateType(entityName)} = Update<${featureState(entityName)},${featureMsg(entityName)}, ${featureEffect(entityName)}>

    /** Update. */
    fun ${featureUpdaterConstructor(entityName)}: ${featureUpdateType(entityName)} {
        val internalUpdater = internalUpdater()
        val externalUpdater = externalUpdater()
        return ${featureUpdateType(entityName)} { msg, state ->
            when (msg) {
                is ${subTypeFeatureMsg(MsgType.External, entityName)} -> externalUpdater(msg, state)
                is ${subTypeFeatureMsg(MsgType.Internal, entityName)} -> internalUpdater(msg, state)
            }
        }
    }
    
    private ${subUpdaterConstruction(MsgType.Internal, entityName)}
    
    private ${subUpdaterConstruction(MsgType.External, entityName)}
    
    /** Сайд-эффекты. */
    sealed class ${featureEffect(entityName)}
    
    /** Внешние сообщения. */
    sealed class ${subTypeFeatureMsg(MsgType.External, entityName)}:${featureMsg(entityName)}()
    
""".trimIndent()

fun effectHandlerFile(packageName: String, entityName: String): String = """
    package ${packageName}.feature
    
    import family.amma.keemun.Dispatch
    import family.amma.keemun.EffectHandler
    
    typealias ${featureEffectHandlerType(entityName)} = EffectHandler<${featureEffect(entityName)}, ${featureMsg(entityName)}>
    
    fun ${featureEffectHandlerConstructor(entityName)} = ${featureEffectHandlerType(entityName)} { effect,dispatch ->
       when(effect) {
            TODO()
       }
    }
    
    /** Внутренние сообщения. */
    sealed class ${subTypeFeatureMsg(MsgType.Internal, entityName)}:${featureMsg(entityName)}()
    
""".trimIndent()

@OptIn(ExperimentalStdlibApi::class)
fun viewStateFile(packageName: String, entityName: String): String = """
    package ${packageName}.feature
    
    import family.amma.keemun.StateTransform
    import family.amma.keemun.feature.Feature
    
    typealias ${entityName}Feature = Feature<${featureViewState(entityName)}, ${subTypeFeatureMsg(MsgType.External,entityName)}>
    
    data class ${entityName}ViewState(val s: String)
    
    /** View State Transform. */
    fun ${entityName.replaceFirstChar { it.lowercase() }}ViewStateTransform() = StateTransform<${featureState(entityName)}, ${featureViewState(entityName)}> { state ->
        TODO()
    }
    
""".trimIndent()

@OptIn(ExperimentalStdlibApi::class)
val keemunTemplate: Template
    get() = template {
        minApi = 16
        name = "Keemun Feature"
        description = "Create new feature"
        category = Category.Other
        formFactor = FormFactor.Generic

        val entityName = stringParameter {
            name = "Feature Name"
            default = "Some"
            constraints = listOf(Constraint.NONEMPTY)
        }
        screens = listOf(
            WizardUiContext.ActivityGallery, WizardUiContext.MenuEntry,
            WizardUiContext.NewProject, WizardUiContext.NewModule
        )
        val packageName = defaultPackageNameParameter
        widgets(
            TextFieldWidget(entityName),
            PackageNameWidget(packageName)
        )

        recipe = {
            keemunSetup(
                it as ModuleTemplateData,
                entityName.value.trim().split(" ").map { it.replaceFirstChar { it.toUpperCase() } }.joinToString { it },
                packageName.value
            )
        }
    }
