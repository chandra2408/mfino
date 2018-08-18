package io.scal.ambi.ui.home.notifications

import android.databinding.ViewDataBinding
import android.support.annotation.CallSuper
import com.ambi.work.BR
import io.scal.ambi.ui.global.base.adapter.AdapterDelegateBase
import io.scal.ambi.ui.notebooks.list.INotebookListViewModel

internal abstract class NotebookManageDetailDelegateBase<in Binding : ViewDataBinding>(private val viewModel: INotebookListViewModel) :
    AdapterDelegateBase<Binding, List<Any>>() {

    @CallSuper
    override fun onBindViewHolder(items: List<Any>, position: Int, binding: Binding, payloads: MutableList<Any>) {
        binding.setVariable(BR.viewModel, viewModel)
        binding.executePendingBindings()
    }
}