package com.example.partymember.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.partymember.R
import com.example.partymember.data.PartyMember

class PartyMemberAdapter : RecyclerView.Adapter<PartyMemberAdapter.MemberViewHolder>() {

    private var members = listOf<PartyMember>()

    inner class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtName: TextView = itemView.findViewById(R.id.txtName)
        val txtGender: TextView = itemView.findViewById(R.id.txtGender)
        val txtAddress: TextView = itemView.findViewById(R.id.txtAddress)
        val txtPhone: TextView = itemView.findViewById(R.id.txtPhone)
        val txtPartyFee: TextView = itemView.findViewById(R.id.txtPartyFee)
        val txtPartyBranch: TextView = itemView.findViewById(R.id.txtPartyBranch)
        val txtIdCard: TextView = itemView.findViewById(R.id.txtIdCard)
        val txtAge: TextView = itemView.findViewById(R.id.txtAge)
        val btnToggleIdCard: ImageView = itemView.findViewById(R.id.btnToggleIdCard)

        private var isIdCardVisible = false

        fun bind(member: PartyMember) {
            txtName.text = member.name
            txtGender.text = member.gender
            txtAddress.text = "地　　址：${member.address}"
            txtPhone.text = "电　　话：${member.phone}"
            txtPartyFee.text = "应缴党费：${member.partyDues} 元"
            txtPartyBranch.text = "所属支部：${member.partyBranch}"

            // 身份证号显示
            isIdCardVisible = false
            showIdCard(member, false)

            // 小眼睛点击切换
            btnToggleIdCard.setOnClickListener {
                isIdCardVisible = !isIdCardVisible
                showIdCard(member, isIdCardVisible)
                btnToggleIdCard.setImageResource(
                    if (isIdCardVisible) R.drawable.ic_eye_open else R.drawable.ic_eye_closed
                )
            }

            // 年龄显示，超过80岁红色高亮
            if (member.age.isNotEmpty()) {
                val ageValue = member.age.toIntOrNull()
                txtAge.text = "年　　龄：${member.age} 岁"
                if (ageValue != null && ageValue > 80) {
                    txtAge.setTextColor(Color.parseColor("#FF0000"))
                    txtAge.textSize = 16f
                } else {
                    txtAge.setTextColor(itemView.resources.getColor(R.color.text_primary, null))
                    txtAge.textSize = 14f
                }
            } else {
                txtAge.text = "年　　龄：未知"
                txtAge.setTextColor(itemView.resources.getColor(R.color.text_primary, null))
            }
        }

        private fun showIdCard(member: PartyMember, visible: Boolean) {
            txtIdCard.text = if (visible) {
                "身份证号：${member.idCard}"
            } else {
                if (member.idCard.length >= 8) {
                    // 用等宽圆点替换中间部分，保持与完整身份证号相同的显示宽度
                    val prefix = member.idCard.substring(0, 3)
                    val suffix = member.idCard.substring(member.idCard.length - 4)
                    val maskLen = member.idCard.length - 3 - 4
                    val mask = "\u2022".repeat(maskLen) // bullet字符，宽度接近数字
                    "身份证号：$prefix$mask$suffix"
                } else {
                    "身份证号：${member.idCard}"
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_party_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        holder.bind(members[position])
    }

    override fun getItemCount(): Int = members.size

    fun submitList(newMembers: List<PartyMember>) {
        val diffCallback = MemberDiffCallback(members, newMembers)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        members = newMembers
        diffResult.dispatchUpdatesTo(this)
    }

    private class MemberDiffCallback(
        private val oldList: List<PartyMember>,
        private val newList: List<PartyMember>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int = oldList.size
        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean {
            return oldList[oldPos].id == newList[newPos].id
        }

        override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean {
            return oldList[oldPos] == newList[newPos]
        }
    }
}
