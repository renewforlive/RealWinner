package com.peter.realwinner.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.peter.realwinner.R
import com.peter.realwinner.model.db.entity.EventEntity
import com.peter.realwinner.ui.adapter.EventAdapter
import com.peter.realwinner.ui.callback.EventNotificationListener
import com.peter.realwinner.ui.dialog.EventAddDialog
import com.peter.realwinner.viewmodel.EventViewModel
import kotlinx.android.synthetic.main.fragment_event.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent.inject

class EventFragment : BaseFragment() {

    companion object {
        fun newInstance() : EventFragment {
            return EventFragment()
        }
    }

    private val viewModel by viewModel<EventViewModel>()
    private val adapter by inject(EventAdapter::class.java)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_event, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpObserver()
        initListener()
    }

    private fun initView() {
        val layoutManager = LinearLayoutManager(context)
        event_recycler_view.layoutManager = layoutManager
        event_recycler_view.adapter = adapter
    }

    private fun setUpObserver() {
        refreshList()
    }

    private fun initListener() {
        event_tool_bar.menu.getItem(0).setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add_event -> {
                    context?.apply {
                        showEventAddDialog()
                    }
                    true
                }
                else -> false
            }
        }
        event_calendar_view.setOnDateChangeListener { _, year, month, dayOfMonth ->
            viewModel.setTime(year, month, dayOfMonth)
            refreshList()
        }
        adapter.setListener(object : EventNotificationListener {
            override fun onNotification(isChecked: Boolean, eventEntity: EventEntity) {
                viewModel.updateEvent(isChecked, eventEntity)
            }
        })
    }

    private fun refreshList() {
        viewModel.getList(viewModel.getCurrentTime()).observe(viewLifecycleOwner, Observer {
            adapter.submitList(it)
        })
    }

    private fun showEventAddDialog() {
        val dialog = EventAddDialog.newInstance()
        dialog.show(childFragmentManager, EventAddDialog.TAG)
    }
}