package io.scal.ambi.ui.home.classes

import android.databinding.ViewDataBinding
import android.support.annotation.CallSuper
import com.ambi.work.BR
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateBase

internal abstract class ClassesAdapterDelegateBase<in Binding : ViewDataBinding>(private val viewModel: IClassesViewModel) :
    AdapterDelegateBase<Binding, List<Any>>() {

    @CallSuper
    override fun onBindViewHolder(items: List<Any>, position: Int, binding: Binding, payloads: MutableList<Any>) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()
    }
}