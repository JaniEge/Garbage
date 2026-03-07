package dk.soerensen.garbagev1.data

import dk.soerensen.garbagev1.data.database.BinDao
import dk.soerensen.garbagev1.domain.Bin
import dk.soerensen.garbagev1.domain.BinRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BinRepositoryImpl @Inject constructor(
    private val binDao: BinDao
) : BinRepository {

    override fun getBins(): Flow<List<Bin>> {
        return binDao.getBins().map { entityList ->
            entityList.map { Bin(id = it.id, title = it.title, description = it.description, imageUrl = it.imageUrl) }
        }
    }

    override fun getBin(id: String): Flow<Bin?> {
        return binDao.getBin(id = id).map { it?.let {
            Bin(id = it.id, title = it.title, description = it.description, imageUrl = it.imageUrl)
        }}
    }
}