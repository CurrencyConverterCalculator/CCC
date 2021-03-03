/*
 * Copyright (c) 2021 Mustafa Ozhan. All rights reserved.
 */

plugins {
    with(Plugins) {
        kotlin(platformJvm)
        kotlin(serializationPlugin)
    }
    application
}

dependencies {
    with(Dependencies.JVM) {
        implementation(ktorCore)
        implementation(ktorNetty)
        implementation(ktorSerialization)
        implementation(logBack)
    }

    with(Dependencies.Common) {
        implementation(koinCore)
    }

    with(Modules) {
        implementation(project(common))
        implementation(project(logmob))
    }
}

application {
    mainClass.set("${ProjectSettings.packageName}.backend.BackendAppKt")
}
