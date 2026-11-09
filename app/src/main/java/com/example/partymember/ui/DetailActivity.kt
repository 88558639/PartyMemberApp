package com.example.partymember.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.partymember.R
import com.example.partymember.databinding.ActivityDetailBinding
import com.example.partymember.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val memberId = intent.getLongExtra("MEMBER_ID", -1L)
        if (memberId == -1L) {
            finish()
            return
        }

        setupUI()
        loadMemberData(memberId)
    }

    private fun setupUI() {
        binding.btnBack.setOnClickListener {
            finish()
        }
    }

    private fun loadMemberData(memberId: Long) {
        lifecycleScope.launch {
            try {
                val member = withContext(Dispatchers.IO) {
                    AppDatabase.getDatabase(applicationContext)
                        .partyMemberDao()
                        .getMemberById(memberId)
                }

                member?.let {
                    displayMember(it)
                } ?: run {
                    finish()
                }
            } catch (e: Exception) {
                finish()
            }
        }
    }

    private fun displayMember(member: com.example.partymember.data.PartyMember) {
        binding.txtName.text = member.name
        binding.txtGender.text = member.gender
        binding.txtAddress.text = member.address
        binding.txtPhone.text = member.phone
        binding.txtPartyDues.text = member.partyDues
        binding.txtPartyBranch.text = member.partyBranch
        binding.txtIdCard.text = member.idCard

        // 年龄显示，超过80岁红色
        if (member.age.isNotEmpty()) {
            val ageValue = member.age.toIntOrNull()
            binding.txtAge.text = "${member.age} 岁"
            if (ageValue != null && ageValue > 80) {
                binding.txtAge.setTextColor(android.graphics.Color.parseColor("#FF0000"))
            } else {
                binding.txtAge.setTextColor(getColor(R.color.text_primary))
            }
        } else {
            binding.txtAge.text = "未知"
            binding.txtAge.setTextColor(getColor(R.color.text_primary))
        }
    }
}
