package com.example.myfirstapp.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import java.util.Date

@Dao
interface LibraryDao {
    @Query("SELECT * FROM library_items ORDER BY name ASC LIMIT :limit OFFSET :offset")
    suspend fun getItemsSortedByName(offset: Int, limit: Int): List<LibraryItemEntity>

    @Query("SELECT * FROM library_items ORDER BY created ASC LIMIT :limit OFFSET :offset")
    suspend fun getItemsSortedByDate(offset: Int, limit: Int): List<LibraryItemEntity>

    @Query("SELECT COUNT(*) FROM library_items")
    suspend fun getItemsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: LibraryItemEntity) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<LibraryItemEntity>)

    @Update
    suspend fun update(item: LibraryItemEntity)

    @Delete
    suspend fun delete(item: LibraryItemEntity)

    @Query("DELETE FROM library_items WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("SELECT COUNT(*) FROM library_items WHERE name < :name")
    suspend fun getPositionByName(name: String): Int

    @Query("SELECT COUNT(*) FROM library_items WHERE created < :created")
    suspend fun getPositionByDate(created: Date): Int

    @Query("SELECT EXISTS(SELECT 1 FROM library_items WHERE id = :id LIMIT 1)")
    suspend fun isIdExists(id: Int): Boolean

    @Query("SELECT * FROM library_items WHERE id = :itemId")
    suspend fun getItemById(itemId: Int): LibraryItemEntity?
}