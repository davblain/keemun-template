package com.github.davblain.keemuntemplate

import com.android.tools.idea.wizard.template.Template
import com.android.tools.idea.wizard.template.WizardTemplateProvider
import com.github.davblain.keemuntemplate.templates.keemunTemplate

class WizardTemplateProviderImpl : WizardTemplateProvider() {
    override fun getTemplates(): List<Template> = listOf(keemunTemplate)
}