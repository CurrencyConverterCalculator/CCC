/*
 Copyright (c) 2020 Mustafa Ozhan. All rights reserved.
 */
package mustafaozhan.github.com.mycurrencies.util.extension

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.jaredrummler.materialspinner.MaterialSpinner
import mustafaozhan.github.com.mycurrencies.R
import timber.log.Timber
import java.io.FileNotFoundException
import java.util.Locale

fun ImageView.setBackgroundByName(name: String) =
    setImageResource(context.getImageResourceByName(name))

fun Context.getImageResourceByName(name: String): Int =
    try {
        resources.getIdentifier(
            name.toLowerCase(Locale.getDefault()).replace("try", "tryy"),
            "drawable",
            packageName
        )
    } catch (e: FileNotFoundException) {
        Timber.w(e)
        R.drawable.transparent
    }

fun AdView.checkAd(id: String, isExpired: Boolean) =
    if (isExpired) {
        MobileAds.initialize(context, id)
        val adRequest = AdRequest.Builder().build()
        loadAd(adRequest)
    } else {
        isEnabled = false
        visibility = View.GONE
    }

fun MaterialSpinner.tryToSelect(base: String) = try {
    getItems<String>()?.indexOf(base)?.let {
        selectedIndex = it
    }
} catch (exception: IllegalArgumentException) {
    expand()
    Timber.w(exception, "try to select failed for index $base")
}