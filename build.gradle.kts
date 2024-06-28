
import org.jetbrains.changelog.Changelog
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.util.*


plugins {
    kotlin("jvm") version "2.0.0"
    id("org.jetbrains.changelog") version "2.2.0"
    id("org.jetbrains.intellij.platform") version "2.0.0-beta7"

}

group = "shop.itbug"
version = "1.0.5"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
        releases()
        marketplace()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaUltimate("2024.1.4")
        bundledPlugins("org.jetbrains.kotlin", "JavaScript")
        pluginVerifier()
        zipSigner()
        instrumentationTools()
    }
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
        jvmArgs = listOf("-XX:+AllowEnhancedClassRedefinition")
    }

    signPlugin {
        certificateChain.set(System.getenv("certificateChain".uppercase(Locale.getDefault())))
        privateKey.set(System.getenv("privateKey".uppercase(Locale.getDefault())))
        password.set(System.getenv("password".uppercase(Locale.getDefault())))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    dependencies {
        implementation("cn.hutool:hutool-all:5.8.15")
        implementation("com.alibaba.fastjson2:fastjson2:2.0.25")
        implementation("com.alibaba.fastjson2:fastjson2-kotlin:2.0.25")
    }

}

intellijPlatform {
    verifyPlugin {
        ides {
//            recommended()
            local(file("/Applications/IntelliJ IDEA Ultimate.app"))
            local(file("/Users/ldd/Applications/IntelliJ IDEA Ultimate.app"))
//            select {
//                types = listOf(IntelliJPlatformType.IntellijIdeaUltimate)
//                channels = listOf(ProductRelease.Channel.RELEASE, ProductRelease.Channel.EAP)
//                sinceBuild = "232"
//            }
        }
    }
}


changelog {
    version = project.version as String
    path = file("CHANGELOG.md").canonicalPath
    groups.empty()
}

