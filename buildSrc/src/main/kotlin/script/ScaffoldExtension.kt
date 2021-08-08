package script

import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.PrintWriter
import kotlin.streams.toList

object ScaffoldExtension {

    private val moduleLayerCategory = mapOf(
        "_presentation" to listOf(
            ModuleStructure.LayerType.view,
            ModuleStructure.LayerType.viewModel,
        ),
        "_dataStore" to listOf(
            ModuleStructure.LayerType.repository,
        ),
        "_domain" to listOf(
            ModuleStructure.LayerType.useCase,
            ModuleStructure.LayerType.entity,
        )
    ).entries

    fun settingModuleNameList(projectPath: String) = projectPath
        .run(::FileReader).buffered().use { reader ->
            reader.lines()?.filter { it.contains("include") }
                ?.map { it.removeSurrounding("include(\"", "\")") }
                ?.toList()
        }

    fun updateSettingModule(
        projectPath: String,
        increasedModuleNameList: List<String>
    ) {
        FileOutputStream(File(projectPath), true).bufferedWriter().use { writer ->
            increasedModuleNameList.forEach {
                writer.append("include(\"$it\")")
                writer.newLine()
            }
        }
    }

    fun updateProjectModuleType(
        projectModuleTypeDirectoryPath: String
    ) {
        val newProjectModuleContents = projectModuleTypeDirectoryPath
            .run(::FileReader).buffered().use { reader ->
                reader.readLines().toMutableList().also { codeList ->
                    codeList.removeIf { it.contains("//autoGen") }
                    codeList.indexOfFirst { it.contains(";") }
                        .run { codeList.addAll(this, needProjectModuleList()) }
                }
            }
        PrintWriter(File(projectModuleTypeDirectoryPath)).use { writer ->
            writer.print(newProjectModuleContents.joinToString("\n"))
        }
    }

    fun missingModuleNameList(
        settingGradleModuleNameList: List<String>
    ): List<String> = needModuleNameList()
        .map { it.name.replace("_", ":") }
        .filter { it !in settingGradleModuleNameList }

    private fun needProjectModuleList() = needModuleNameList().map {
        StringBuilder()
            .append("        ")
            .append(it.name)
            .append("(")
            .append(it.layerType.javaClass.name + "." + it.layerType.name)
            .append(", ")
            .append(it.domainType.javaClass.name + "." + it.domainType.name)
            .append("),")
            .append(" //autoGen")
            .toString()
            .replace("$", ".")
    }

    private fun needModuleNameList(): List<ProjectModule.Entity> =
        ModuleStructure.DomainType.values().flatMap { domain ->
            ModuleStructure.LayerType.values().mapNotNull { layer ->
                moduleLayerCategory.find { it.value.contains(layer) }?.let {
                    StringBuilder()
                        .append(it.key)
                        .append("_")
                        .append(layer.name)
                        .append("_")
                        .append(domain.name)
                        .toString()
                }?.let {
                    ProjectModule.Entity(it, layer, domain)
                }
            }
        }
}