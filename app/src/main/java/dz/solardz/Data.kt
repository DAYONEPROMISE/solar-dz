package dz.solardz

import androidx.room.*

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String = "",
    val wilaya: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)

@Entity(tableName = "quotes")
data class Quote(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientId: Long = 0,
    val dailyKwh: Double,
    val sunHours: Double,
    val pvKw: Double,
    val panels: Int,
    val batteryKwh: Double,
    val inverterKw: Double,
    val costDzd: Long,
    val createdAt: Long = System.currentTimeMillis(),
)

@Dao
interface SolarDao {
    @Insert suspend fun insertClient(c: Client): Long
    @Query("SELECT * FROM clients ORDER BY createdAt DESC") suspend fun clients(): List<Client>
    @Insert suspend fun insertQuote(q: Quote): Long
    @Query("SELECT * FROM quotes ORDER BY createdAt DESC") suspend fun quotes(): List<Quote>
}

@Database(entities = [Client::class, Quote::class], version = 1, exportSchema = false)
abstract class SolarDb : RoomDatabase() {
    abstract fun dao(): SolarDao
}
