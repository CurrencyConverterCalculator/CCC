/*
 * Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */
package com.github.mustafaozhan.ccc.android.ui.main

import androidx.lifecycle.ViewModel
import com.github.mustafaozhan.ccc.android.util.isDayPassed
import com.github.mustafaozhan.ccc.android.util.isWeekPassed
import com.github.mustafaozhan.ccc.client.repo.SettingsRepository

class MainViewModel(private val settingsRepository: SettingsRepository) : ViewModel() {

    fun shouldShowReview() = settingsRepository.lastReviewRequest.isWeekPassed()

    fun setLastReview() {
        settingsRepository.lastReviewRequest = System.currentTimeMillis()
    }

    fun isFistRun() = settingsRepository.firstRun

    fun getAppTheme() = settingsRepository.appTheme

    fun isRewardExpired() = settingsRepository.adFreeActivatedDate.isDayPassed()
}
