package com.example.dpi.database;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DpiDatabase_Impl extends DpiDatabase {
  private volatile PacketDao _packetDao;

  private volatile FlowDao _flowDao;

  private volatile ThreatDao _threatDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `packets` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `timestamp` INTEGER NOT NULL, `sourceIp` TEXT NOT NULL, `destinationIp` TEXT NOT NULL, `sourcePort` INTEGER, `destinationPort` INTEGER, `protocol` TEXT NOT NULL, `payloadSize` INTEGER NOT NULL, `flags` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `flows` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `sourceIp` TEXT NOT NULL, `destinationIp` TEXT NOT NULL, `sourcePort` INTEGER, `destinationPort` INTEGER, `protocol` TEXT NOT NULL, `packetCount` INTEGER NOT NULL, `totalBytes` INTEGER NOT NULL, `firstSeen` INTEGER NOT NULL, `lastSeen` INTEGER NOT NULL, `isActive` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `threats` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `packetId` INTEGER NOT NULL, `flowId` INTEGER, `threatType` TEXT NOT NULL, `severity` TEXT NOT NULL, `confidence` REAL NOT NULL, `description` TEXT NOT NULL, `detectionMethod` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '5893137c7cc673ddbe63cfdcf316557b')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `packets`");
        db.execSQL("DROP TABLE IF EXISTS `flows`");
        db.execSQL("DROP TABLE IF EXISTS `threats`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsPackets = new HashMap<String, TableInfo.Column>(9);
        _columnsPackets.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPackets.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPackets.put("sourceIp", new TableInfo.Column("sourceIp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPackets.put("destinationIp", new TableInfo.Column("destinationIp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPackets.put("sourcePort", new TableInfo.Column("sourcePort", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPackets.put("destinationPort", new TableInfo.Column("destinationPort", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPackets.put("protocol", new TableInfo.Column("protocol", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPackets.put("payloadSize", new TableInfo.Column("payloadSize", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPackets.put("flags", new TableInfo.Column("flags", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPackets = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPackets = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPackets = new TableInfo("packets", _columnsPackets, _foreignKeysPackets, _indicesPackets);
        final TableInfo _existingPackets = TableInfo.read(db, "packets");
        if (!_infoPackets.equals(_existingPackets)) {
          return new RoomOpenHelper.ValidationResult(false, "packets(com.example.dpi.models.Packet).\n"
                  + " Expected:\n" + _infoPackets + "\n"
                  + " Found:\n" + _existingPackets);
        }
        final HashMap<String, TableInfo.Column> _columnsFlows = new HashMap<String, TableInfo.Column>(11);
        _columnsFlows.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlows.put("sourceIp", new TableInfo.Column("sourceIp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlows.put("destinationIp", new TableInfo.Column("destinationIp", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlows.put("sourcePort", new TableInfo.Column("sourcePort", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlows.put("destinationPort", new TableInfo.Column("destinationPort", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlows.put("protocol", new TableInfo.Column("protocol", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlows.put("packetCount", new TableInfo.Column("packetCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlows.put("totalBytes", new TableInfo.Column("totalBytes", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlows.put("firstSeen", new TableInfo.Column("firstSeen", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlows.put("lastSeen", new TableInfo.Column("lastSeen", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsFlows.put("isActive", new TableInfo.Column("isActive", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFlows = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFlows = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFlows = new TableInfo("flows", _columnsFlows, _foreignKeysFlows, _indicesFlows);
        final TableInfo _existingFlows = TableInfo.read(db, "flows");
        if (!_infoFlows.equals(_existingFlows)) {
          return new RoomOpenHelper.ValidationResult(false, "flows(com.example.dpi.models.PacketFlow).\n"
                  + " Expected:\n" + _infoFlows + "\n"
                  + " Found:\n" + _existingFlows);
        }
        final HashMap<String, TableInfo.Column> _columnsThreats = new HashMap<String, TableInfo.Column>(9);
        _columnsThreats.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsThreats.put("packetId", new TableInfo.Column("packetId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsThreats.put("flowId", new TableInfo.Column("flowId", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsThreats.put("threatType", new TableInfo.Column("threatType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsThreats.put("severity", new TableInfo.Column("severity", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsThreats.put("confidence", new TableInfo.Column("confidence", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsThreats.put("description", new TableInfo.Column("description", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsThreats.put("detectionMethod", new TableInfo.Column("detectionMethod", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsThreats.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysThreats = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesThreats = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoThreats = new TableInfo("threats", _columnsThreats, _foreignKeysThreats, _indicesThreats);
        final TableInfo _existingThreats = TableInfo.read(db, "threats");
        if (!_infoThreats.equals(_existingThreats)) {
          return new RoomOpenHelper.ValidationResult(false, "threats(com.example.dpi.models.ThreatAlert).\n"
                  + " Expected:\n" + _infoThreats + "\n"
                  + " Found:\n" + _existingThreats);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "5893137c7cc673ddbe63cfdcf316557b", "391074e2a37c0b7e1059b3224fbff9c9");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "packets","flows","threats");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `packets`");
      _db.execSQL("DELETE FROM `flows`");
      _db.execSQL("DELETE FROM `threats`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(PacketDao.class, PacketDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(FlowDao.class, FlowDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(ThreatDao.class, ThreatDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public PacketDao packetDao() {
    if (_packetDao != null) {
      return _packetDao;
    } else {
      synchronized(this) {
        if(_packetDao == null) {
          _packetDao = new PacketDao_Impl(this);
        }
        return _packetDao;
      }
    }
  }

  @Override
  public FlowDao flowDao() {
    if (_flowDao != null) {
      return _flowDao;
    } else {
      synchronized(this) {
        if(_flowDao == null) {
          _flowDao = new FlowDao_Impl(this);
        }
        return _flowDao;
      }
    }
  }

  @Override
  public ThreatDao threatDao() {
    if (_threatDao != null) {
      return _threatDao;
    } else {
      synchronized(this) {
        if(_threatDao == null) {
          _threatDao = new ThreatDao_Impl(this);
        }
        return _threatDao;
      }
    }
  }
}
