package dk.soerensen.garbagev1.data

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dk.soerensen.garbagev1.domain.BinRepository
import dk.soerensen.garbagev1.domain.ItemRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Provides
    @Singleton
    fun provideItemRepository(
        @ApplicationContext context: Context
    ): ItemRepository = ItemRepositoryImpl(context)

    @Provides
    @Singleton
    fun provideBinRepository(): BinRepository = BinRepositoryImpl()
}