import com.android.build.gradle.internal.tasks.factory.dependsOn

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
}

tasks.register("conanInstall") {
    doFirst {
        val conanExecutable = "conan"
        val buildDir = File("edgerunner/build")
        buildDir.mkdirs()
        val buildTypes = listOf("Debug", "Release")
        val architectures = listOf("armv7", "armv8", "x86", "x86_64")
        buildTypes.forEach { buildType ->
            architectures.forEach { arch ->
                val cmd =
                    "$conanExecutable install ../src/main/cpp --profile android -s build_type=$buildType -s arch=$arch --build missing -c tools.cmake.cmake_layout:build_folder_vars=['settings.arch']"
                println(">> $cmd \n")
                val process =
                    ProcessBuilder(cmd.split(" "))
                        .directory(buildDir)
                        .redirectOutput(ProcessBuilder.Redirect.PIPE)
                        .redirectError(ProcessBuilder.Redirect.PIPE)
                        .start()
                val sout = process.inputStream.bufferedReader().readText()
                val serr = process.errorStream.bufferedReader().readText()
                process.waitFor()
                println("$sout $serr")
                if (process.exitValue() != 0) {
                    throw Exception("out> $sout err> $serr\nCommand: $cmd")
                }
            }
        }
    }
}

android {
    namespace = "com.neuralize.edgerunner"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
                arguments("-DCMAKE_TOOLCHAIN_FILE=conan_android_toolchain.cmake")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    externalNativeBuild {
        cmake {
            path = file("src/main/cpp/CMakeLists.txt")
            version = "3.22.1"
        }
    }

    project.tasks.preBuild.dependsOn("conanInstall")
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}