//
//  File.swift
//  CCC
//
//  Created by Mustafa Ozhan on 30/01/2021.
//  Copyright © 2021 orgName. All rights reserved.
//

import SwiftMessages
import SwiftUI
import Client

func showSnackBar(text: String, butonText: String, action:@escaping () -> Void) {

    let view = MessageView.viewFromNib(layout: .cardView)
    view.configureTheme(
        backgroundColor: MR.colors().background.get(),
        foregroundColor: MR.colors().text.get(),
        iconImage: MR.images().ic_app_logo.get().resized(
            to: CGSize(width: 64, height: 64)
        )
    )

    view.configureDropShadow()

    view.configureContent(
        title: "",
        body: text,
        iconImage: MR.images().ic_app_logo.get()
            .resized(to: CGSize(width: 64, height: 64)),
        iconText: nil,
        buttonImage: nil,
        buttonTitle: butonText,
        buttonTapHandler: { _ in
            action()
    })

    view.button?.contentEdgeInsets = UIEdgeInsets(
        top: 10.0,
        left: 10.0,
        bottom: 10.0,
        right: 10.0
    )

    view.button?.backgroundColor = MR.colors().primary.get()

    var config = SwiftMessages.defaultConfig
    config.presentationStyle = .bottom
    config.presentationContext = .window(windowLevel: UIWindow.Level.normal)

    SwiftMessages.show(config: config, view: view)
}

func showAlert(
    title: String = "",
    text: String,
    buttonText: String,
    action:@escaping () -> Void = {} ,
    cancelable: Bool = true,
    hideOnAction: Bool = true
) {
    let view: MessageView = MessageView.viewFromNib(layout: .centeredView)
    view.configureBackgroundView(width: 250)
    view.configureTheme(
        backgroundColor: MR.colors().background.get(),
        foregroundColor: MR.colors().text.get()
    )
    view.configureContent(
        title: title,
        body: text,
        iconImage: MR.images().ic_app_logo.get().resized(
            to: CGSize(width: 64, height: 64)
        ),
        iconText: nil,
        buttonImage: nil,
        buttonTitle: buttonText
    ) { _ in
        action()

        if hideOnAction {
            SwiftMessages.hide()
        }
    }

    view.button?.contentEdgeInsets = UIEdgeInsets(
        top: 10.0,
        left: 10.0,
        bottom: 10.0,
        right: 10.0
    )

    view.backgroundView.backgroundColor = MR.colors().background_weak.get()
    view.backgroundView.layer.cornerRadius = 10
    view.button?.backgroundColor = MR.colors().primary.get()

    view.addSubview(UIButton(frame: CGRect(x: 30, y: 30, width: 30, height: 30)))

    var config = SwiftMessages.defaultConfig
    config.presentationStyle = .center
    config.duration = .forever
    config.dimMode = .blur(style: .dark, alpha: 1, interactive: cancelable)
    config.presentationContext  = .window(windowLevel: UIWindow.Level.statusBar)
    SwiftMessages.show(config: config, view: view)
}

extension UIImage {
    func resized(to size: CGSize) -> UIImage {
        return UIGraphicsImageRenderer(size: size).image { _ in
            draw(in: CGRect(origin: .zero, size: size))
        }
    }
}
