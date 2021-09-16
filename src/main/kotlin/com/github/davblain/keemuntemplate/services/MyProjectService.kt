package com.github.davblain.keemuntemplate.services

import com.intellij.openapi.project.Project
import com.github.davblain.keemuntemplate.MyBundle

class MyProjectService(project: Project) {

    init {
        println(MyBundle.message("projectService", project.name))
    }
}
