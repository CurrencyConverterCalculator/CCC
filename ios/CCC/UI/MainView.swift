//
//  MainView.swift
//  CCC
//
//  Created by Mustafa Ozhan on 28/01/2021.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI
import NavigationStack
import Client

typealias MainObservable = ObservableSEED<MainViewModel, BaseState, MainEffect, MainEvent, MainData>

struct MainView: View {

    @StateObject var observable: MainObservable = koin.get()

    @Environment(\.scenePhase) var scenePhase

    var body: some View {

        NavigationStackView(
            transitionType: .default,
            easing: Animation.easeInOut(duration: 0.5)
        ) {
            if observable.viewModel.isFistRun() {
                SliderView()
            } else {
                CalculatorView()
            }
        }
        .onAppear { observable.startObserving() }
        .onDisappear { observable.stopObserving() }
        .onReceive(observable.effect) { onEffect(effect: $0) }
        .onChange(of: scenePhase) { phase in
            switch phase {
            case .background:
                LoggerKt.kermit.d(withMessage: {"App is in background"})
            case .active:
                LoggerKt.kermit.d(withMessage: {"App is Active"})
                observable.event.onResume()
            case .inactive:
                LoggerKt.kermit.d(withMessage: {"App is Inactive"})
                observable.event.onPause()
            @unknown default:
                LoggerKt.kermit.d(withMessage: {"New App state not yet introduced"})
            }
        }
    }

    private func onEffect(effect: MainEffect) {
        LoggerKt.kermit.d(withMessage: {effect.description})
        switch effect {
        default:
            LoggerKt.kermit.d(withMessage: {"MainView unknown effect"})
        }
    }
}
