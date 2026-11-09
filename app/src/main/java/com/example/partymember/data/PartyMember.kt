package com.example.partymember.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "party_members")
data class PartyMember(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val gender: String,
    val address: String,
    val phone: String,
    val partyDues: String,
    val partyBranch: String,
    val idCard: String = "",
    val age: String = ""
)
