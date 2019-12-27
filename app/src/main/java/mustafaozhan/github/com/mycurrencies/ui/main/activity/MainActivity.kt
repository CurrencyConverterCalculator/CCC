package mustafaozhan.github.com.mycurrencies.ui.main.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.NonNull
import com.crashlytics.android.Crashlytics
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import mustafaozhan.github.com.mycurrencies.BuildConfig
import mustafaozhan.github.com.mycurrencies.R
import mustafaozhan.github.com.mycurrencies.base.activity.BaseActivity
import mustafaozhan.github.com.mycurrencies.base.fragment.BaseFragment
import mustafaozhan.github.com.mycurrencies.model.RemoteConfig
import mustafaozhan.github.com.mycurrencies.ui.main.fragment.calculator.CalculatorFragment
import mustafaozhan.github.com.mycurrencies.ui.main.fragment.settings.SettingsFragment
import java.util.concurrent.TimeUnit

@Suppress("TooManyFunctions")
class MainActivity : BaseActivity<MainViewModel>() {

    companion object {
        const val BACK_DELAY: Long = 2000
        const val CHECK_DURATION: Long = 6
        const val CHECK_INTERVAL: Long = 4200
        const val REMOTE_CONFIG = "remote_config"
        const val AD_INITIAL_DELAY: Long = 50
        const val AD_PERIOD: Long = 250
    }

    private lateinit var rewardedAd: RewardedAd
    private lateinit var adObservableInterval: Disposable
    private lateinit var mInterstitialAd: InterstitialAd
    private var adVisibility = false
    private var doubleBackToExitPressedOnce = false

    override fun getDefaultFragment(): BaseFragment<*> = CalculatorFragment.newInstance()

    override fun getLayoutResId(): Int = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadRewardedAd()
        checkUpdate()
        prepareAd()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()

        when {
            supportFragmentManager.findFragmentById(containerId) is CalculatorFragment ->
                menuInflater.inflate(R.menu.fragment_main_menu, menu)
            supportFragmentManager.findFragmentById(containerId) is SettingsFragment ->
                menuInflater.inflate(R.menu.fragment_settings_menu, menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> replaceFragment(SettingsFragment.newInstance(), true)
            R.id.feedback -> sendFeedBack()
            R.id.support -> {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.app_market_link))
                )
                intent.resolveActivity(packageManager)?.let {
                    showDialog(
                        getString(R.string.support_us),
                        getString(R.string.rate_and_support),
                        getString(R.string.rate)
                    ) {
                        startActivity(intent)
                    }
                }
            }
            R.id.removeAds -> showDialog(
                getString(R.string.remove_ads),
                getString(R.string.remove_ads_text),
                getString(R.string.watch)
            ) {
                showRewardedAd()
            }
            R.id.onGithub -> {
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(getString(R.string.github_url))
                )
                intent.resolveActivity(packageManager)?.let {
                    startActivity(intent)
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun loadRewardedAd() {
        rewardedAd = RewardedAd(this, getString(R.string.rewarded_ad_unit_id))
        rewardedAd.loadAd(AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() = Unit
            override fun onRewardedAdFailedToLoad(errorCode: Int) = Unit
        })
    }

    private fun showRewardedAd() {
        if (rewardedAd.isLoaded) {
            rewardedAd.show(this, object : RewardedAdCallback() {
                override fun onRewardedAdOpened() = Unit
                override fun onRewardedAdClosed() = loadRewardedAd()
                override fun onRewardedAdFailedToShow(errorCode: Int) = loadRewardedAd()
                override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                    viewModel.updateAdFreeActivation()
                    val intent = intent
                    finish()
                    startActivity(intent)
                }
            })
        }
    }

    private fun sendFeedBack() {
        Intent(Intent.ACTION_SEND).apply {
            type = "text/email"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail_developer)))
            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_feedback_subject))
            putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_extra_text) + "")
            startActivity(Intent.createChooser(this, getString(R.string.mail_intent_title)))
        }
    }

    private fun prepareAd() {
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.interstitial_ad_id)
        mInterstitialAd.loadAd(AdRequest.Builder().build())
    }

    private fun ad() {
        adVisibility = true
        adObservableInterval = Observable.interval(AD_INITIAL_DELAY, AD_PERIOD, TimeUnit.SECONDS)
            .debounce(0, TimeUnit.SECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                if (mInterstitialAd.isLoaded && adVisibility && viewModel.isRewardExpired()) {
                    mInterstitialAd.show()
                } else {
                    prepareAd()
                }
            }
//            .doOnError(::logException)
            .subscribe()
    }

    @Suppress("ComplexMethod")
    private fun checkUpdate() {

        val defaultMap = HashMap<String, Any>()
        defaultMap[REMOTE_CONFIG] = RemoteConfig(
            getString(R.string.remote_config_title),
            getString(R.string.remote_config_description),
            getString(R.string.app_market_link)
        )

        FirebaseRemoteConfig.getInstance().apply {
            setConfigSettingsAsync(
                FirebaseRemoteConfigSettings
                    .Builder()
                    .setMinimumFetchIntervalInSeconds(CHECK_INTERVAL)
                    .build()
            )
            setDefaultsAsync(defaultMap)
            fetch(if (BuildConfig.DEBUG) 0 else TimeUnit.HOURS.toSeconds(CHECK_DURATION))
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        activate()

                        val remoteConfigStr =
                            if (TextUtils.isEmpty(getString(REMOTE_CONFIG))) {
                                defaultMap[REMOTE_CONFIG] as? String
                            } else {
                                getString(REMOTE_CONFIG)
                            }

                        try {
                            Gson().fromJson(
                                remoteConfigStr,
                                RemoteConfig::class.java
                            ).apply {
                                val isCancelable = forceVersion <= BuildConfig.VERSION_CODE

                                if (latestVersion > BuildConfig.VERSION_CODE) {
                                    showDialog(title, description, getString(R.string.update), isCancelable) {
                                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl)))
                                    }
                                }
                            }
                        } catch (e: JsonSyntaxException) {
                            Crashlytics.logException(e)
                        }
                    }
                }
        }
    }

    override fun onResume() {
        super.onResume()
        ad()
    }

    override fun onPause() {
        super.onPause()
        adObservableInterval.dispose()
        adVisibility = false
    }

    override fun onBackPressed() {
        if (supportFragmentManager.findFragmentById(containerId) is CalculatorFragment) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed()
                return
            }

            doubleBackToExitPressedOnce = true
            snacky(getString(R.string.click_back_again_to_exit))
            Handler().postDelayed({
                doubleBackToExitPressedOnce = false
            }, BACK_DELAY)
        } else {
            super.onBackPressed()
        }
    }
}