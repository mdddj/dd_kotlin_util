
import org.jetbrains.changelog.Changelog
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import java.util.*


plugins {
    kotlin("jvm") version "2.0.20"
    id("org.jetbrains.changelog") version "2.2.1"
    id("org.jetbrains.intellij.platform") version "2.0.1"
}

group = "shop.itbug"
version = "1.0.9"

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
        untilBuild.set("243.*")
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

}

intellijPlatform {
    pluginVerification {

        ides {
            local(file("/Applications/IntelliJ IDEA Ultimate 2024.2.2.app"))
        }
    }
}


changelog {
    version = project.version as String
    path = file("CHANGELOG.md").canonicalPath
    groups.empty()
}

