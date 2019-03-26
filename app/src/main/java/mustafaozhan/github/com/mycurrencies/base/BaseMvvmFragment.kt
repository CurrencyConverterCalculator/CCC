package mustafaozhan.github.com.mycurrencies.base

import android.arch.lifecycle.ViewModelProviders
import io.reactivex.disposables.CompositeDisposable

/**
 * Created by Mustafa Ozhan on 7/10/18 at 9:41 PM on Arch Linux wit Love <3.
 */
abstract class BaseMvvmFragment<VM : BaseViewModel> : BaseFragment() {

    protected val compositeDisposable = CompositeDisposable()

    protected abstract fun getViewModelClass(): Class<VM>

    protected val viewModel: VM by lazy { ViewModelProviders.of(this).get(getViewModelClass()) }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}