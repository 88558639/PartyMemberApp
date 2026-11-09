package com.example.partymember.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PartyMemberDao {
    @Query("SELECT * FROM party_members WHERE name LIKE :searchQuery ORDER BY name ASC")
    fun searchMembers(searchQuery: String): Flow<List<PartyMember>>

    @Query("SELECT DISTINCT SUBSTR(name, 1, 1) as initial FROM party_members ORDER BY initial ASC")
    fun getInitials(): Flow<List<String>>

    @Query("SELECT * FROM party_members WHERE id = :id")
    suspend fun getMemberById(id: Long): PartyMember?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(members: List<PartyMember>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(member: PartyMember): Long

    @Update
    suspend fun update(member: PartyMember)

    @Delete
    suspend fun delete(member: PartyMember)

    @Query("DELETE FROM party_members")
    suspend fun deleteAll()

    @Query("SELECT COUNT(*) FROM party_members")
    suspend fun getCount(): Int
}
