rootProject.name = "CloudstreamPlugins"

// Bu kısım mevcut proje inclusion mantığını koruyor
val disabled = listOf<String>("__Temel")

File(rootDir, ".").eachDir { dir ->
    if (!disabled.contains(dir.name) && File(dir, "build.gradle.kts").exists()) {
        include(dir.name)
    }
}

fun File.eachDir(block: (File) -> Unit) {
    listFiles()?.filter { it.isDirectory }?.forEach { block(it) }
}

// -------------------------------
// 🔥 JADB snapshot fix
// Burada dependencyResolutionManagement ekleniyor
// -------------------------------
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
    }

    // JADB master-SNAPSHOT -> v1.2.1 yönlendirmesi
    components.all {
        if (id.group == "com.github.vidstige" && id.module == "jadb") {
            withModule<Any> {
                // Jitpack'te sadece v1.2.1 var, SNAPSHOT kaldırıldı
                replacedBy("com.github.vidstige:jadb", "v1.2.1")
            }
        }
    }
}

// Eğer sadece tek bir projeyi denemek istersen, yukarıdakileri yoruma alıp aşağıyı aç
// include("PluginName")
