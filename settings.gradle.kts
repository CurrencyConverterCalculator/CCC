/*
 * Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */

enableFeaturePreview("GRADLE_METADATA")

include(
    ":android",
    ":backend",
    ":web",
    ":desktop",
    ":client",
    ":common",
    ":basemob", ":scopemob", ":logmob", ":parsermob"
)

project(":basemob").projectDir = file("basemob/basemob")
project(":scopemob").projectDir = file("scopemob/scopemob")
project(":logmob").projectDir = file("logmob/logmob")
project(":parsermob").projectDir = file("parsermob/parsermob")
