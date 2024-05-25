import org.jetbrains.changelog.Changelog
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.util.*

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.0.0"
    id("org.jetbrains.intellij") version "1.16.1"
    id("org.jetbrains.changelog") version "2.2.0"
}

group = "shop.itbug"
version = "1.0.1"

repositories {
    mavenLocal()
    mavenCentral()
}

intellij {
    version.set("241-EAP-SNAPSHOT")
    type.set("IU")
    plugins.set(listOf("org.jetbrains.kotlin","JavaScript"))
}


val changeLog = provider {
    changelog.renderItem(
        changelog
            .getOrNull(project.version as String) ?: changelog.getUnreleased()
            .withHeader(false)
            .withEmptySections(false),
        Changelog.OutputType.HTML
    )
}


println("更新日志:\n${changeLog.get()}\n")

tasks {
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
            languageVersion.set(KotlinVersion.KOTLIN_2_0)
        }
    }

    patchPluginXml {
        sinceBuild.set("232")
        changeNotes.set(changeLog)
    }

    runIde {
        autoReloadPlugins.set(true)
        jvmArgs = listOf("-XX:+AllowEnhancedClassRedefinition")
    }

    signPlugin {
        certificateChain.set(System.getenv("certificateChain".uppercase(Locale.getDefault())))
        privateKey.set(System.getenv("privateKey".uppercase(Locale.getDefault())))
        password.set(System.getenv("password".uppercase(Locale.getDefault())))
    }

    publishPlugin {
        token.set(System.getenv("PUSH_TOKEN"))
    }

    dependencies {
        implementation("cn.hutool:hutool-all:5.8.15")
        implementation("com.alibaba.fastjson2:fastjson2:2.0.25")
        implementation("com.alibaba.fastjson2:fastjson2-kotlin:2.0.25")
    }

    listProductsReleases {
    }
}

changelog {
    version = project.version as String
    path = file("CHANGELOG.md").canonicalPath
    groups.empty()
}
