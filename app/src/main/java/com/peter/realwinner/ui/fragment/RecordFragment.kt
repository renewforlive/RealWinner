package com.peter.realwinner.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.peter.realwinner.R
import com.peter.realwinner.constant.BundleType
import com.peter.realwinner.model.db.entity.RecordEntity
import com.peter.realwinner.ui.activity.RecordDetailActivity
import com.peter.realwinner.ui.adapter.RecordAdapter
import com.peter.realwinner.viewmodel.RecordViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_record.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.KoinJavaComponent.inject

class RecordFragment : BaseFragment() {

    companion object {
        fun newInstance() : RecordFragment {
            return RecordFragment()
        }
    }

    private val viewModel by viewModel(RecordViewModel::class)
    private val adapter by inject(RecordAdapter::class.java)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setUpObserver()
        initListener()
    }

    private fun initView() {
        val layoutManager = GridLayoutManager(context, 2)
        record_recycler_view.layoutManager = layoutManager
        record_recycler_view.adapter = adapter
    }

    private fun initListener() {
        record_tool_bar.menu.getItem(0).setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.add_record -> {
                    context?.apply {
                        val intent = Intent(this, RecordDetailActivity::class.java)
                        this@RecordFragment.startActivity(intent)
                    }
                    true
                }
                else -> false
            }
        }
        adapter.setListener(object : RecordAdapter.RecordItemCallback {
            override fun onClickRecord(recordEntity: RecordEntity) {
                context?.apply {
                    val intent = Intent(this, RecordDetailActivity::class.java)
                    intent.putExtra(BundleType.RECORD_ID, recordEntity.recordId)
                    this@RecordFragment.startActivity(intent)
                }
            }
        })
    }

    private fun setUpObserver() {
        viewModel.getPagedList().observe(viewLifecycleOwner, Observer {
            if (it.size > 0) {
                displayEmpty(false)
            } else {
                displayEmpty(true)
            }
            adapter.submitList(it)
        })
    }

    private fun displayEmpty(isEmpty: Boolean) {
        if (isEmpty) {
            record_recycler_view.visibility = View.GONE
            record_empty_view.visibility = View.VISIBLE
        } else {
            record_recycler_view.visibility = View.VISIBLE
            record_empty_view.visibility = View.GONE
        }
    }
}