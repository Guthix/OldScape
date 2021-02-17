/*
 * Copyright 2018-2021 Guthix
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.guthix.oldscape

import io.guthix.js5.Js5Cache
import io.guthix.js5.container.disk.Js5DiskStore
import io.guthix.oldscape.cache.ConfigArchive
import io.guthix.oldscape.cache.config.LocationConfig
import io.guthix.oldscape.cache.config.EnumConfig
import io.guthix.oldscape.cache.config.ObjectConfig
import io.guthix.oldscape.codegen.writeNamedConfigTemplates
import io.guthix.oldscape.codegen.writeEnumTemplates
import org.gradle.api.Project
import org.gradle.api.plugins.BasePlugin
import java.io.File

class ServerContextGenerator : CodeGenerator() {
    override fun apply(target: Project) {
        super.apply(target)
        val contextGenTask = target.task("generateTemplateContext") {
            doFirst {
                val resourceDir = "${target.rootDir}/${IdGenerator.cacheDir}"
                Js5Cache(Js5DiskStore.open(File(resourceDir).toPath())).use { cache ->
                    val configArchive = cache.readArchive(ConfigArchive.id)
                    val locs = LocationConfig.load(configArchive.readGroup(LocationConfig.id))
                    val objs = ObjectConfig.load(configArchive.readGroup(ObjectConfig.id))
                    val enums = EnumConfig.load(configArchive.readGroup(EnumConfig.id))
                    target.writeEnumTemplates(enums, objs, locs)
                }
            }
        }
        val classesTask = target.tasks.getByName("compileKotlin")
        contextGenTask.group = BasePlugin.BUILD_GROUP
        classesTask.dependsOn(contextGenTask)
    }


    companion object {
        const val constantPrefix: String = "    const val "
        const val packageName: String = "io.guthix.oldscape.server.template"
    }
}