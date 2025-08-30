rootProject.name = "CloudstreamPlugins"

// Bu listeye yazılan klasörler build'e dahil edilmeyecek
val disabled = listOf("__Temel")

File(rootDir, ".").eachDir { dir ->
    if (!disabled.contains(dir.name) && File(dir, "build.gradle.kts").exists()) {
        include(dir.name)
    }
}

fun File.eachDir(block: (File) -> Unit) {
    listFiles()?.filter { it.isDirectory }?.forEach { block(it) }
}

// Eğer sadece tek bir projeyi build etmek istersen:
// include("PluginName")
