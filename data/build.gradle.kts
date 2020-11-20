/*
 Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */
plugins {
    with(Plugins) {
        id(library)
        kotlin(android)
        kotlin(kapt)
    }
}

android {
    with(ProjectSettings) {
        compileSdkVersion(projectCompileSdkVersion)

        defaultConfig {
            minSdkVersion(projectMinSdkVersion)
            targetSdkVersion(projectTargetSdkVersion)

            versionCode = getVersionCode(project)
            versionName = getVersionName(project)

            android {
                javaCompileOptions {
                    annotationProcessorOptions {
                        argument("room.schemaLocation", "$projectDir/schemas")
                    }
                }
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    with(Dependencies.Android) {
        implementation(moshi)
        implementation(moshiConverter)
        implementation(retrofit)
        implementation(roomKtx)

        testImplementation(jUnit)
    }

    with(Annotations) {
        kapt(moshi)
        kapt(room)
    }

    with(Modules) {
        implementation(project(common))
        implementation(project(scopemob))
    }
}
