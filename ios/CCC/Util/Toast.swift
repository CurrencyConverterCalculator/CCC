//
//  Toast.swift
//  CCC
//
//  Created by Mustafa Ozhan on 11/03/2021.
//  Copyright © 2021 orgName. All rights reserved.
//

import Loaf
import Client

func showToast(text: String) {
    Loaf(
        text,
        state: .custom(
            .init(
                backgroundColor: MR.colors().text.get(),
                textColor: MR.colors().background_weak.get(),
                tintColor: MR.colors().background_weak.get(),
                icon: UIImage(systemName: "info.circle.fill"),
                textAlignment: .center,
                iconAlignment: .left,
                width: .screenPercentage(0.75)
            )
        ),
        sender: UIApplication.shared.windows.first(where: \.isKeyWindow)!.rootViewController!
    ).show()
}
