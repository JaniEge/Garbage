package dk.soerensen.garbagev1.data.injection

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dk.soerensen.garbagev1.data.database.ItemDao
import dk.soerensen.garbagev1.data.database.ShopDao
import dk.soerensen.garbagev1.data.database.ShoppingDatabase
import javax.inject.Provider
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideShoppingDatabase(
        @ApplicationContext context: Context,
        itemDaoProvider: Provider<ItemDao>,
        shopDaoProvider: Provider<ShopDao>
    ): ShoppingDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = ShoppingDatabase::class.java,
            name = "shopping_database"
        )
            .addCallback(callback = ShoppingDatabase.Callback(itemDaoProvider, shopDaoProvider))
            .build()
    }

    @Provides
    @Singleton
    fun provideItemDao(database: ShoppingDatabase): ItemDao {
        return database.itemDao()
    }

    @Provides
    @Singleton
    fun provideShopDao(database: ShoppingDatabase): ShopDao {
        return database.shopDao()
    }
}
