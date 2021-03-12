//
//  EmailHelper.swift
//  CCC
//
//  Created by Mustafa Ozhan on 12/03/2021.
//  Copyright © 2021 orgName. All rights reserved.
//

import MessageUI
import UIKit
import Client

class EmailHelper: UIViewController, MFMailComposeViewControllerDelegate {

    func sendFeedback() {
        let mailComposeViewController = configureMailComposer()

        if MFMailComposeViewController.canSendMail() {
            self.present(mailComposeViewController, animated: true, completion: nil)
        } else {
            LoggerKt.kermit.d(withMessage: { "Mail services are not available" })
        }
    }

    func configureMailComposer() -> MFMailComposeViewController {

        let mailComposeVC = MFMailComposeViewController()

        mailComposeVC.mailComposeDelegate = self
        mailComposeVC.setToRecipients([MR.strings().mail_developer.get()])
        mailComposeVC.setSubject(MR.strings().mail_feedback_subject.get())
        mailComposeVC.setMessageBody(MR.strings().mail_extra_text.get(), isHTML: false)

        return mailComposeVC
    }
}
