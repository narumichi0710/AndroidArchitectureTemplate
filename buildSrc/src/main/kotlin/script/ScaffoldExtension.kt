package script

import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
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

    fun generateNewModule(
        projectPath: String,
        increasedModuleNameList: List<String>
    ): List<() -> List<() -> Unit>> = allCopy(
        projectPath,
        getTemplateDirectoryPathList(projectPath),
        getAllNewDirectoryPathList(projectPath, increasedModuleNameList)
    )

    private fun getAllNewDirectoryPathList(
        projectPath: String,
        increasedModuleNameList: List<String>
    ): List<Path> = increasedModuleNameList.mapNotNull { moduleName ->
        runCatching {
            projectPath.plus(moduleName.replace(":", "/"))
                .run(Paths::get)
                ?.run(Files::createDirectories)
        }.getOrNull()
    }

    private fun getTemplateDirectoryPathList(projectPath: String): File = projectPath
        .plus("/moduleTemplate")
        .run(::File)

    private fun allCopy(
        projectPath: String,
        templateDirectory: File,
        exportPathList: List<Path>
    ): List<() -> List<() -> Unit>> = templateDirectory.walkTopDown().map { from ->
        {
            exportPathList.map { to ->
                {
                    val moduleName = to.toAbsolutePath().toString().removePrefix(projectPath)
                    from.absolutePath
                        .replace(
                            "moduleTemplate/base",
                            moduleName
                        ).run(::File).run {
                            if (from.isFile) {
                                runCatching {
                                    Files.copy(
                                        decoratePackagePath(moduleName, from.path),
                                        toPath()
                                    )
                                }.onSuccess { println("success createNewFile::${toPath()}") }
                                    .onFailure { println("failure createNewFile::${toPath()}") }
                            } else {
                                runCatching { Files.createDirectories(toPath()) }
                                    .onSuccess { println("success createNewDirectory::${toPath()}") }
                                    .onFailure { println("failure createNewDirectory::${toPath()}") }
                            }
                        }
                    Unit
                }
            }.toList()
        }
    }.toList()

    private fun decoratePackagePath(moduleName: String, path: String): InputStream = path
        .run(::FileInputStream)
        .run(::InputStreamReader).use { reader ->
            reader.readLines().map {
                if (!it.contains("jp.arsaga")) it
                else moduleName
                    .replace("/", ".")
                    .run { it.replace("jp.arsaga", "jp.arsaga$this") }
            }
        }.joinToString("\n")
        .byteInputStream()

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