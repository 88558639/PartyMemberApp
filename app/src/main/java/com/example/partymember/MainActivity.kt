package com.example.partymember

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.partymember.databinding.ActivityMainBinding
import com.example.partymember.ui.MainViewModel
import com.example.partymember.ui.PartyMemberAdapter
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var adapter: PartyMemberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupSearch()
        setupImportButton()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = PartyMemberAdapter()

        binding.rvMembers.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.onSearchQueryChanged(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private val uploadLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            viewModel.refreshData()
        }
    }

    private fun setupImportButton() {
        binding.btnImport.setOnClickListener {
            val intent = Intent(this, com.example.partymember.ui.UploadActivity::class.java)
            uploadLauncher.launch(intent)
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            viewModel.members.collect { members ->
                adapter.submitList(members)
                updateUIState(members)
            }
        }

        lifecycleScope.launch {
            viewModel.hasData.collect { hasData ->
                val currentMembers = viewModel.members.value
                updateUIState(currentMembers)
            }
        }
    }

    private fun updateUIState(members: List<com.example.partymember.data.PartyMember>) {
        val searchText = binding.etSearch.text.toString()

        if (searchText.isNotEmpty()) {
            if (members.isNotEmpty()) {
                binding.rvMembers.visibility = android.view.View.VISIBLE
                binding.llEmpty.visibility = android.view.View.GONE
            } else {
                binding.rvMembers.visibility = android.view.View.GONE
                binding.llEmpty.visibility = android.view.View.VISIBLE
                binding.tvEmptyTitle.text = "没有找到相关成员"
                binding.tvEmptySubtitle.text = "请检查搜索内容"
            }
        } else {
            val hasData = viewModel.hasData.value
            if (hasData) {
                binding.rvMembers.visibility = android.view.View.VISIBLE
                binding.llEmpty.visibility = android.view.View.GONE
            } else {
                binding.rvMembers.visibility = android.view.View.GONE
                binding.llEmpty.visibility = android.view.View.VISIBLE
                binding.tvEmptyTitle.text = resources.getString(com.example.partymember.R.string.no_data)
                binding.tvEmptySubtitle.text = "点击右上角导入数据"
            }
        }
    }
}
