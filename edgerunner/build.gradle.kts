
plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.jetbrains.kotlin.android)
    `maven-publish`
}

tasks.register("conanInstall") {
    doFirst {
        val conanExecutable = "conan"
        val buildDir = File("edgerunner/build")
        buildDir.mkdirs()
        val buildTypes = listOf("Debug", "Release", "RelWithDebInfo")
        val architectures = listOf("armv8")
        val edgerunnerOptions = "-o edgerunner/*:with_gpu=True -o edgerunner/*:with_npu=True"
        buildTypes.forEach { buildType ->
            architectures.forEach { arch ->
                val cmd =
                    "$conanExecutable install ../src/main/cpp --profile android -s build_type=$buildType -s arch=$arch --build missing $edgerunnerOptions -c tools.cmake.cmake_layout:build_folder_vars=['settings.arch']"
                println(">> $cmd \n")
                val process =
                    ProcessBuilder(cmd.split(" "))
                        .directory(buildDir)
                        .redirectOutput(ProcessBuilder.Redirect.PIPE)
                        .redirectError(ProcessBuilder.Redirect.PIPE)
                        .start()

                val sout = process.inputStream.bufferedReader()
                val serr = process.errorStream.bufferedReader()

                val outputThread = Thread {
                    sout.lines().forEach { println(it) }
                }
                val errorThread = Thread {
                    serr.lines().forEach { println(it) }
                }

                outputThread.start()
                errorThread.start()

                outputThread.join()
                errorThread.join()

                process.waitFor()
                if (process.exitValue() != 0) {
                    throw Exception("Command failed with exit code ${process.exitValue()}")
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

        aarMetadata {
            minCompileSdk = 30
        }

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
        externalNativeBuild {
            cmake {
                cppFlags += "-std=c++17"
                arguments("-DCMAKE_TOOLCHAIN_FILE=conan_android_toolchain.cmake")
            }
        }

        ndk {
            abiFilters += setOf("arm64-v8a")
        }

    }

    packagingOptions {
        jniLibs {
            useLegacyPackaging = true
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = "com.neuralize"
            artifactId = "edgerunner"
            version = "0.1.0"

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}
