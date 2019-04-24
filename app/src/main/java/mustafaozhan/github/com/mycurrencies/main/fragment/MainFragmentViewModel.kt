package mustafaozhan.github.com.mycurrencies.main.fragment

import android.arch.lifecycle.MutableLiveData
import com.crashlytics.android.Crashlytics
import mustafaozhan.github.com.mycurrencies.base.BaseViewModel
import mustafaozhan.github.com.mycurrencies.extensions.calculateResultByCurrency
import mustafaozhan.github.com.mycurrencies.extensions.getFormatted
import mustafaozhan.github.com.mycurrencies.extensions.getRates
import mustafaozhan.github.com.mycurrencies.extensions.getThroughReflection
import mustafaozhan.github.com.mycurrencies.extensions.insertInitialCurrencies
import mustafaozhan.github.com.mycurrencies.extensions.replaceCommas
import mustafaozhan.github.com.mycurrencies.extensions.toOfflineRates
import mustafaozhan.github.com.mycurrencies.main.fragment.model.CurrencyResponse
import mustafaozhan.github.com.mycurrencies.main.fragment.model.Rates
import mustafaozhan.github.com.mycurrencies.room.dao.CurrencyDao
import mustafaozhan.github.com.mycurrencies.room.dao.OfflineRatesDao
import mustafaozhan.github.com.mycurrencies.room.model.Currency
import mustafaozhan.github.com.mycurrencies.tools.Currencies
import org.mariuszgromada.math.mxparser.Expression
import javax.inject.Inject

/**
 * Created by Mustafa Ozhan on 2018-07-12.
 */
@Suppress("TooManyFunctions")
class MainFragmentViewModel : BaseViewModel() {

    override fun inject() {
        viewModelComponent.inject(this)
    }

    @Inject
    lateinit var currencyDao: CurrencyDao

    @Inject
    lateinit var offlineRatesDao: OfflineRatesDao

    val ratesLiveData: MutableLiveData<Rates> = MutableLiveData()
    var currencyListLiveData: MutableLiveData<MutableList<Currency>> = MutableLiveData()
    var rates: Rates? = null
    var output: String = "0.0"

    fun refreshData() {
        loadPreferences()
        currencyListLiveData.value?.clear()
        if (mainData.firstRun) {
            insertInitialCurrencies()
            for (i in 0 until Currencies.values().size - 1) {
                subscribeService(dataManager.backendGetAllOnBase(Currencies.values()[i]),
                    ::offlineRateAllSuccess, ::offlineRateAllFail)
            }
            mainData.firstRun = false
        }

        currencyListLiveData.postValue(currencyDao.getActiveCurrencies())
    }

    fun insertInitialCurrencies() {
        loadPreferences()
        currencyDao.insertInitialCurrencies()
    }

    fun getCurrencies() {
        loadPreferences()
        if (rates != null) {
            rates.let { rates ->
                currencyListLiveData.value?.forEach { currency ->
                    currency.rate = calculateResultByCurrency(currency.name, output, rates)
                }
                ratesLiveData.postValue(rates)
            }
        } else {
            subscribeService(dataManager.backendGetAllOnBase(mainData.currentBase),
                ::rateDownloadSuccess,
                ::backendRateDownloadFail)
        }
    }

    private fun backendRateDownloadFail(t: Throwable) {
        Crashlytics.logException(t)
        if (mainData.currentBase == Currencies.BTC) {
            subscribeService(dataManager.exchangesRatesGetAllOnBase(Currencies.CRYPTO_BTC),
                ::rateDownloadSuccess,
                ::rateDownloadFail)
        } else {
            subscribeService(dataManager.exchangesRatesGetAllOnBase(mainData.currentBase),
                ::rateDownloadSuccess,
                ::rateDownloadFail)
        }
    }

    private fun rateDownloadSuccess(currencyResponse: CurrencyResponse) {
        ratesLiveData.postValue(currencyResponse.rates)
        rates = currencyResponse.rates
        currencyResponse.toOfflineRates().let {
            offlineRatesDao.insertOfflineRates(it)
        }
    }

    private fun rateDownloadFail(t: Throwable) {
        Crashlytics.logException(t)
        offlineRatesDao.getOfflineRatesOnBase(mainData.currentBase.toString()).let { offlineRates ->
            ratesLiveData.postValue(offlineRates?.getRates())
        }
    }

    fun calculateOutput(text: String) {
        val calculation = Expression(
            text.replaceCommas()
                .replace("%", "/100*")
        ).calculate()

        output = if (calculation.isNaN()) {
            ""
        } else {
            calculation.getFormatted()
        }
    }

    fun updateCurrentBase(currency: String?) {
        rates = null
        setCurrentBase(currency)
    }

    fun loadResetData() = dataManager.loadResetData()

    fun persistResetData(resetData: Boolean) = dataManager.persistResetData(resetData)

    fun getClickedItemRate(name: String): String {
        return "1 ${mainData.currentBase.name} = ${rates?.getThroughReflection<Double>(name)}"
    }

    private fun offlineRateAllFail(throwable: Throwable) {
        Crashlytics.logException(throwable)
    }

    private fun offlineRateAllSuccess(currencyResponse: CurrencyResponse) {
        currencyResponse.toOfflineRates().let { offlineRatesDao.insertOfflineRates(it) }
    }

    fun getCurrencyByName(name: String) = currencyDao.getCurrencyByName(name)

    fun verifyCurrentBase() {
        if (mainData.currentBase == Currencies.NULL) {
            updateCurrentBase(currencyListLiveData.value?.firstOrNull { it.isActive == 1 }?.name)
        }
    }
}