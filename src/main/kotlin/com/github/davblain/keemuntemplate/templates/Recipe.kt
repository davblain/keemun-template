package com.github.davblain.keemuntemplate.templates

import com.android.tools.idea.wizard.template.ModuleTemplateData
import com.android.tools.idea.wizard.template.ProjectTemplateData
import com.android.tools.idea.wizard.template.RecipeExecutor
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.IdeActions
import java.io.File
import com.intellij.openapi.project.Project
fun RecipeExecutor.keemunSetup(
    moduleData: ModuleTemplateData,
    entityName: String,
    packageName: String
) {

    val srcOut = File(moduleData.srcDir.absolutePath.replace("java","kotlin"))
    save(
        featureFile(packageName, entityName),
        srcOut.resolve("feature/${entityName}Feature.kt"),
    )
    save(
        updaterFile(packageName, entityName),
        srcOut.resolve("feature/${entityName}Updater.kt")
    )
    save(
        effectHandlerFile(packageName, entityName),
        srcOut.resolve("feature/${entityName}EffectHandler.kt")
    )
    save(
        viewStateFile(packageName, entityName),
        srcOut.resolve("feature/${entityName}ViewState.kt")
    )

}
