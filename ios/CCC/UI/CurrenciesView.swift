//
//  CurrenciesView.swift
//  CCC
//
//  Created by Mustafa Ozhan on 21/01/2021.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftUI
import Client
import NavigationStack

typealias CurrenciesObservable = ObservableSEED
<CurrenciesViewModel, CurrenciesState, CurrenciesEffect, CurrenciesEvent, CurrenciesData>

struct CurrenciesView: View {

    @Environment(\.colorScheme) var colorScheme
    @EnvironmentObject private var navigationStack: NavigationStack
    @StateObject var observable: CurrenciesObservable = koin.get()

    var onBaseChange: (String) -> Void

    var body: some View {
        ZStack {
            MR.colors().background_strong.get().edgesIgnoringSafeArea(.all)

            VStack {

                CurrencyToolbarView(
                    firstRun: observable.viewModel.isFirstRun(),
                    onCloseClick: { observable.event.onCloseClick() },
                    updateAllCurrenciesState: { observable.event.updateAllCurrenciesState(state: $0) }
                )

                Form {
                    if observable.state.loading {
                        FormProgressView()
                    } else {
                        List(observable.state.currencyList, id: \.name) { currency in
                            CurrencyItemView(
                                item: currency,
                                onItemClick: { observable.event.onItemClick(currency: currency) }
                            )
                        }
                        .id(UUID())
                        .listRowBackground(MR.colors().background.get())
                    }
                }.background(MR.colors().background.get())

                if observable.viewModel.isFirstRun() {
                    SelectCurrencyView(
                        text: MR.strings().txt_select_currencies.get(),
                        buttonText: MR.strings().btn_done.get(),
                        onButtonClick: observable.event.onDoneClick
                    )
                }

            }
            .navigationBarHidden(true)
        }
        .onAppear { observable.startObserving() }
        .onDisappear { observable.stopObserving() }
        .onReceive(observable.effect) { onEffect(effect: $0) }
    }

    private func onEffect(effect: CurrenciesEffect) {
        LoggerKt.kermit.d(withMessage: {effect.description})
        switch effect {
        case is CurrenciesEffect.FewCurrency:
            showToast(text: MR.strings().choose_at_least_two_currency.get())
        case is CurrenciesEffect.OpenCalculator:
            navigationStack.push(CalculatorView())
        case is CurrenciesEffect.Back:
            self.navigationStack.pop()
        // swiftlint:disable force_cast
        case is CurrenciesEffect.ChangeBase:
            onBaseChange((effect as! CurrenciesEffect.ChangeBase).newBase)
        default:
            LoggerKt.kermit.d(withMessage: {"unknown effect"})
        }
    }
}

struct CurrencyToolbarView: View {
    var firstRun: Bool
    var onCloseClick: () -> Void
    var updateAllCurrenciesState: (Bool) -> Void

    var body: some View {
        HStack {

            if !firstRun {
                Button(
                    action: onCloseClick,
                    label: {
                        Image(systemName: "chevron.left")
                            .imageScale(.large)
                            .accentColor(MR.colors().text.get())
                            .padding(.leading, 20)
                    }
                ).padding(.trailing, 10)
            }

            Text(MR.strings().txt_currencies.get())
                .font(.title2)

            Spacer()
            Button(
                action: { updateAllCurrenciesState(true) },
                label: { Text(MR.strings().btn_select_all.get()).foregroundColor(MR.colors().text.get()) }
            ).padding(.trailing, 10)
            Button(
                action: { updateAllCurrenciesState(false) },
                label: { Text(MR.strings().btn_de_select_all.get()).foregroundColor(MR.colors().text.get()) }
            )

        }.padding(EdgeInsets(top: 20, leading: 10, bottom: 5, trailing: 20))
    }
}

struct CurrencyItemView: View {
    @Environment(\.colorScheme) var colorScheme
    @State var item: Currency

    var onItemClick: () -> Void

    var body: some View {
        HStack {

            Image(uiImage: item.name.getImage())
                .resizable()
                .frame(width: 36, height: 36, alignment: .center)
                .shadow(radius: 3)
            Text(item.name)
                .frame(width: 45)
                .foregroundColor(MR.colors().text.get())
            Text(item.longName)
                .font(.footnote)
                .foregroundColor(MR.colors().text.get())
            Text(item.symbol)
                .font(.footnote)
                .foregroundColor(MR.colors().text.get())
            Spacer()
            Image(systemName: item.isActive ? "checkmark.circle.fill" : "circle")
                .foregroundColor(MR.colors().accent.get())

        }
        .contentShape(Rectangle())
        .onTapGesture { onItemClick() }
        .lineLimit(1)
    }
}
