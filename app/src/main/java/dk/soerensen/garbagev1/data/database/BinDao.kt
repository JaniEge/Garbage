package dk.soerensen.garbagev1.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BinDao {

    @Query("SELECT * FROM bins ORDER BY title")
    fun getBins(): Flow<List<BinEntity>>

    @Query("SELECT * FROM bins WHERE id = :id")
    fun getBin(id: String): Flow<BinEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bin: BinEntity)
}