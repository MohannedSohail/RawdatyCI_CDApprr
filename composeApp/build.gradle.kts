import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.composeHotReload)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    jvm("desktop")

    sourceSets {
        androidMain.dependencies {
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.android)
            implementation(libs.koin.android)
            implementation(libs.coroutines.android)
            implementation(libs.datastore.prefs)
        }
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.ui)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)

            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.components.resources)

            // Voyager Navigation
            implementation(libs.voyager.navigator)
            implementation(libs.voyager.screenmodel)
            implementation(libs.voyager.koin)
            implementation(libs.voyager.transitions)

            // Koin DI
            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            // Ktor Network
            implementation(libs.ktor.core)
            implementation(libs.ktor.content.neg)
            implementation(libs.ktor.json)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.auth)

            // DataStore
            implementation(libs.datastore.prefs)

            // Coil Images
            implementation(libs.coil.compose)
            implementation(libs.coil.network)

            // Kotlinx
            implementation(libs.serialization.json)
            implementation(libs.coroutines.core)
            implementation(libs.datetime)
        }


        iosMain.dependencies {
            implementation(libs.ktor.darwin)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            implementation(libs.kotlinx.coroutinesSwing)
            implementation(compose.desktop.currentOs)
            implementation(libs.ktor.java)
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.kotlinx.coroutinesSwing)
                implementation(compose.desktop.currentOs)
                implementation(libs.ktor.java)
            }
        }

    }
}

android {
    namespace = "org.mohanned.rawdatyci_cdapp"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.mohanned.rawdatyci_cdapp"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
}

compose.desktop {
    application {
        mainClass = "org.mohanned.rawdatyci_cdapp.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "org.mohanned.rawdatyci_cdapp"
            packageVersion = "1.0.0"
        }
    }
}