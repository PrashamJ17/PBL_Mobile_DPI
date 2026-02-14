package com.example.dpi.database

import androidx.room.*
import com.example.dpi.models.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Packets
 */
@Dao
interface PacketDao {
    @Query("SELECT * FROM packets ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecentPackets(limit: Int = 1000): Flow<List<Packet>>

    @Query("SELECT * FROM packets WHERE timestamp > :since ORDER BY timestamp DESC")
    fun observePacketsSince(since: Long): Flow<List<Packet>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(packet: Packet): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(packets: List<Packet>)

    @Query("SELECT COUNT(*) FROM packets")
    suspend fun getCount(): Long

    @Query("DELETE FROM packets WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long): Int

    @Query("DELETE FROM packets")
    suspend fun deleteAll()
}

/**
 * Data Access Object for Flows
 */
@Dao
interface FlowDao {
    @Query("SELECT * FROM flows WHERE isActive = 1 ORDER BY lastSeen DESC")
    fun observeActiveFlows(): Flow<List<PacketFlow>>

    @Query("SELECT * FROM flows ORDER BY lastSeen DESC LIMIT :limit")
    fun observeRecentFlows(limit: Int = 100): Flow<List<PacketFlow>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(flow: PacketFlow): Long

    @Update
    suspend fun update(flow: PacketFlow)

    @Query("UPDATE flows SET isActive = 0 WHERE lastSeen < :before")
    suspend fun deactivateOldFlows(before: Long): Int

    @Query("SELECT COUNT(*) FROM flows WHERE isActive = 1")
    suspend fun getActiveFlowCount(): Int

    @Query("DELETE FROM flows WHERE lastSeen < :before")  // ✅ FIXED: Changed from 'timestamp' to 'lastSeen'
    suspend fun deleteOlderThan(before: Long): Int
}

/**
 * Data Access Object for Threats
 */
@Dao
interface ThreatDao {
    @Query("SELECT * FROM threats ORDER BY timestamp DESC LIMIT :limit")
    fun observeRecentThreats(limit: Int = 500): Flow<List<ThreatAlert>>

    @Query("SELECT * FROM threats WHERE severity = :severity ORDER BY timestamp DESC")
    fun observeThreatsBySeverity(severity: ThreatSeverity): Flow<List<ThreatAlert>>

    @Query("SELECT * FROM threats WHERE packetId = :packetId")
    suspend fun getThreatsForPacket(packetId: Long): List<ThreatAlert>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(threat: ThreatAlert): Long

    @Query("SELECT COUNT(*) FROM threats")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM threats WHERE severity = :severity")
    suspend fun getCountBySeverity(severity: ThreatSeverity): Int

    @Query("DELETE FROM threats WHERE timestamp < :before")
    suspend fun deleteOlderThan(before: Long): Int
}

/**
 * Room Database for DPI data
 */
@Database(
    entities = [Packet::class, PacketFlow::class, ThreatAlert::class],
    version = 1,
    exportSchema = false  // ✅ FIXED: Disabled schema export
)
@TypeConverters(Converters::class)
abstract class DpiDatabase : RoomDatabase() {
    abstract fun packetDao(): PacketDao
    abstract fun flowDao(): FlowDao
    abstract fun threatDao(): ThreatDao
}

/**
 * Type converters for Room
 */
class Converters {
    @TypeConverter
    fun fromProtocol(protocol: Protocol): String = protocol.name

    @TypeConverter
    fun toProtocol(value: String): Protocol = Protocol.valueOf(value)

    @TypeConverter
    fun fromThreatType(type: ThreatType): String = type.name

    @TypeConverter
    fun toThreatType(value: String): ThreatType = ThreatType.valueOf(value)

    @TypeConverter
    fun fromThreatSeverity(severity: ThreatSeverity): String = severity.name

    @TypeConverter
    fun toThreatSeverity(value: String): ThreatSeverity = ThreatSeverity.valueOf(value)
}