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
            if (from.parent.contains("/moduleTemplate/base")) {
                baseSourceCodeCreate(projectPath, from, exportPathList)
            } else {
                layerSourceCodeCreate(projectPath, from, exportPathList)
            }
        }
    }.toList()

    private fun baseSourceCodeCreate(
        projectPath: String,
        from: File,
        exportPathList: List<Path>
    ): List<() -> Unit> = exportPathList.map { to ->
        {
            val moduleName = getModuleName(to, projectPath)
            from.absolutePath
                .replace(
                    "moduleTemplate/base",
                    moduleName
                ).run(::File)
                .run { sourceCodeFilePathAdapter(this, moduleName) }
                .run {
                    createNewFile(from, this) { reader ->
                        reader.readLines().map {
                            if (!it.contains("jp.arsaga")) it
                            else moduleName
                                .replace("/", ".")
                                .run { it.replace("jp.arsaga", "jp.arsaga$this") }
                        }
                    }
                }
        }
    }

    private fun layerSourceCodeCreate(
        projectPath: String,
        from: File,
        exportPathList: List<Path>
    ): List<() -> Unit> =
        if (from.isDirectory) listOf()
        else {
            val layerName = sliceLastSlashAfter(from.parent.toString())
            exportPathList.mapNotNull { to ->
                if (!to.toString().contains("/$layerName/")) null
                else {
                    {
                        val moduleName = getModuleName(to, projectPath)
                        val domainName = sliceLastSlashAfter(moduleName)
                        from.absolutePath
                            .replace(
                                "/moduleTemplate/layer",
                                moduleName
                            ).replace(
                                """src/main/java/.+/""".toRegex(),
                                "src/main/java/"
                            ).let {
                                val templateFileName = it.substringAfterLast("/")
                                it.replace(
                                    templateFileName,
                                    toUpperCamel(domainName).plus(templateFileName)
                                )
                            }.run(::File)
                            .run { sourceCodeFilePathAdapter(this, moduleName) }
                            .run {
                                createNewFile(from, this) { reader ->
                                    reader.readLines().map {
                                        val pathString = moduleName
                                            .run { substring(indexOf(layerName)) }
                                            .replace("/", ".")
                                            .removePrefix(layerName.plus("."))
                                        it.replace("{Small}", pathString)
                                            .replace("{Large}", toUpperCamel(domainName))
                                    }
                                }
                            }
                    }
                }
            }
        }

    private fun getModuleName(to: Path, projectPath: String): String = to
        .toAbsolutePath().toString().removePrefix(projectPath)

    private fun sliceLastSlashAfter(path: String): String = path
        .substring(path.indexOfLast { it == '/' }).substring(1)

    private fun toUpperCamel(source: String) = source.let {
        (it.getOrNull(0)?.toUpperCase() ?: ' ')
            .plus(it.substring(1)).trim()
    }

    private fun sourceCodeFilePathAdapter(
        file: File,
        moduleName: String
    ): File =
        if (!file.parent.endsWith("/java")) file
        else File(file.path.replace("/java", "/java/jp/arsaga/$moduleName")).also {
            Files.createDirectories(Paths.get(it.parent))
        }

    private fun createNewFile(
        from: File,
        to: File,
        editContent: (InputStreamReader) -> List<String>
    ) {
        if (from.isFile) {
            runCatching { Files.copy(decorateTemplateFile(from.path, editContent), to.toPath()) }
                .onSuccess { println("success createNewFile::${to.toPath()}") }
                .onFailure { println("failure createNewFile::${to.toPath()}") }
        } else {
            runCatching { Files.createDirectories(to.toPath()) }
                .onSuccess { println("success createNewDirectory::${to.toPath()}") }
                .onFailure { println("failure createNewDirectory::${to.toPath()}") }
        }
    }

    private fun decorateTemplateFile(
        path: String,
        editContent: (InputStreamReader) -> List<String>
    ): InputStream = path
        .run(::FileInputStream)
        .run(::InputStreamReader)
        .use { editContent(it) }
        .joinToString("\n")
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