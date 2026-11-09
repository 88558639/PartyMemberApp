package com.example.partymember.data

import kotlinx.coroutines.flow.Flow

class PartyMemberRepository(private val dao: PartyMemberDao) {

    fun searchMembers(query: String): Flow<List<PartyMember>> {
        return dao.searchMembers("$query%")
    }

    fun getInitials(): Flow<List<String>> {
        return dao.getInitials()
    }

    suspend fun getMemberById(id: Long): PartyMember? {
        return dao.getMemberById(id)
    }

    suspend fun insertAll(members: List<PartyMember>) {
        dao.insertAll(members)
    }

    suspend fun getCount(): Int {
        return dao.getCount()
    }

    suspend fun deleteAll() {
        dao.deleteAll()
    }
}
