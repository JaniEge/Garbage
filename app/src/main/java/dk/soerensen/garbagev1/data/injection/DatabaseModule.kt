package dk.soerensen.garbagev1.data.injection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dk.soerensen.garbagev1.data.database.BinDao
import dk.soerensen.garbagev1.data.database.GarbageDatabase
import dk.soerensen.garbagev1.data.database.ItemDao
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideGarbageDatabase(
        @ApplicationContext context: Context,
        itemDaoProvider: Provider<ItemDao>,
        binDaoProvider: Provider<BinDao>
    ): GarbageDatabase {
        return Room.databaseBuilder(
            context,
            GarbageDatabase::class.java,
            "garbage_database"
        )
            .addCallback(GarbageDatabase.Callback(itemDaoProvider, binDaoProvider))
            .build()
    }

    @Provides
    @Singleton
    fun provideItemDao(database: GarbageDatabase): ItemDao {
        return database.itemDao()
    }

    @Provides
    @Singleton
    fun provideBinDao(database: GarbageDatabase): BinDao {
        return database.binDao()
    }
}