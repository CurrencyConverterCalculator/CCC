/*
 * Copyright (c) 2021 Mustafa Ozhan. All rights reserved.
 */

package com.github.mustafaozhan.ccc.web.ui

import com.github.mustafaozhan.ccc.client.di.getDependency
import com.github.mustafaozhan.ccc.client.viewmodel.MainViewModel
import com.github.mustafaozhan.ccc.web.app.AppDependenciesContext
import com.github.mustafaozhan.logmob.kermit
import react.RProps
import react.child
import react.dom.tr
import react.functionalComponent
import react.useContext

private val mainViewModel: MainViewModel by lazy {
    useContext(AppDependenciesContext).koin.getDependency(MainViewModel::class)
}

val MainView = functionalComponent<RProps> {
    kermit.d { "MainView" }
    child(
        functionalComponent {
            if (mainViewModel.isFistRun()) {
                tr { +"First Run" }
            } else {
                tr { +"Not first run" }
            }
        }
    )
}
