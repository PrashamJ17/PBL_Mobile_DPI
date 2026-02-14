package com.example.dpi.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.dpi.models.ThreatAlert;
import com.example.dpi.models.ThreatSeverity;
import com.example.dpi.models.ThreatType;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ThreatDao_Impl implements ThreatDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<ThreatAlert> __insertionAdapterOfThreatAlert;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  public ThreatDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfThreatAlert = new EntityInsertionAdapter<ThreatAlert>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `threats` (`id`,`packetId`,`flowId`,`threatType`,`severity`,`confidence`,`description`,`detectionMethod`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final ThreatAlert entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getPacketId());
        if (entity.getFlowId() == null) {
          statement.bindNull(3);
        } else {
          statement.bindLong(3, entity.getFlowId());
        }
        final String _tmp = __converters.fromThreatType(entity.getThreatType());
        statement.bindString(4, _tmp);
        final String _tmp_1 = __converters.fromThreatSeverity(entity.getSeverity());
        statement.bindString(5, _tmp_1);
        statement.bindDouble(6, entity.getConfidence());
        statement.bindString(7, entity.getDescription());
        statement.bindString(8, entity.getDetectionMethod());
        statement.bindLong(9, entity.getTimestamp());
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM threats WHERE timestamp < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final ThreatAlert threat, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfThreatAlert.insertAndReturnId(threat);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteOlderThan(final long before,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteOlderThan.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, before);
        try {
          __db.beginTransaction();
          try {
            final Integer _result = _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteOlderThan.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<ThreatAlert>> observeRecentThreats(final int limit) {
    final String _sql = "SELECT * FROM threats ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"threats"}, new Callable<List<ThreatAlert>>() {
      @Override
      @NonNull
      public List<ThreatAlert> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPacketId = CursorUtil.getColumnIndexOrThrow(_cursor, "packetId");
          final int _cursorIndexOfFlowId = CursorUtil.getColumnIndexOrThrow(_cursor, "flowId");
          final int _cursorIndexOfThreatType = CursorUtil.getColumnIndexOrThrow(_cursor, "threatType");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfDetectionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "detectionMethod");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<ThreatAlert> _result = new ArrayList<ThreatAlert>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ThreatAlert _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPacketId;
            _tmpPacketId = _cursor.getLong(_cursorIndexOfPacketId);
            final Long _tmpFlowId;
            if (_cursor.isNull(_cursorIndexOfFlowId)) {
              _tmpFlowId = null;
            } else {
              _tmpFlowId = _cursor.getLong(_cursorIndexOfFlowId);
            }
            final ThreatType _tmpThreatType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfThreatType);
            _tmpThreatType = __converters.toThreatType(_tmp);
            final ThreatSeverity _tmpSeverity;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSeverity);
            _tmpSeverity = __converters.toThreatSeverity(_tmp_1);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpDetectionMethod;
            _tmpDetectionMethod = _cursor.getString(_cursorIndexOfDetectionMethod);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new ThreatAlert(_tmpId,_tmpPacketId,_tmpFlowId,_tmpThreatType,_tmpSeverity,_tmpConfidence,_tmpDescription,_tmpDetectionMethod,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<ThreatAlert>> observeThreatsBySeverity(final ThreatSeverity severity) {
    final String _sql = "SELECT * FROM threats WHERE severity = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromThreatSeverity(severity);
    _statement.bindString(_argIndex, _tmp);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"threats"}, new Callable<List<ThreatAlert>>() {
      @Override
      @NonNull
      public List<ThreatAlert> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPacketId = CursorUtil.getColumnIndexOrThrow(_cursor, "packetId");
          final int _cursorIndexOfFlowId = CursorUtil.getColumnIndexOrThrow(_cursor, "flowId");
          final int _cursorIndexOfThreatType = CursorUtil.getColumnIndexOrThrow(_cursor, "threatType");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfDetectionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "detectionMethod");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<ThreatAlert> _result = new ArrayList<ThreatAlert>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ThreatAlert _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPacketId;
            _tmpPacketId = _cursor.getLong(_cursorIndexOfPacketId);
            final Long _tmpFlowId;
            if (_cursor.isNull(_cursorIndexOfFlowId)) {
              _tmpFlowId = null;
            } else {
              _tmpFlowId = _cursor.getLong(_cursorIndexOfFlowId);
            }
            final ThreatType _tmpThreatType;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfThreatType);
            _tmpThreatType = __converters.toThreatType(_tmp_1);
            final ThreatSeverity _tmpSeverity;
            final String _tmp_2;
            _tmp_2 = _cursor.getString(_cursorIndexOfSeverity);
            _tmpSeverity = __converters.toThreatSeverity(_tmp_2);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpDetectionMethod;
            _tmpDetectionMethod = _cursor.getString(_cursorIndexOfDetectionMethod);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new ThreatAlert(_tmpId,_tmpPacketId,_tmpFlowId,_tmpThreatType,_tmpSeverity,_tmpConfidence,_tmpDescription,_tmpDetectionMethod,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getThreatsForPacket(final long packetId,
      final Continuation<? super List<ThreatAlert>> $completion) {
    final String _sql = "SELECT * FROM threats WHERE packetId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, packetId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<ThreatAlert>>() {
      @Override
      @NonNull
      public List<ThreatAlert> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfPacketId = CursorUtil.getColumnIndexOrThrow(_cursor, "packetId");
          final int _cursorIndexOfFlowId = CursorUtil.getColumnIndexOrThrow(_cursor, "flowId");
          final int _cursorIndexOfThreatType = CursorUtil.getColumnIndexOrThrow(_cursor, "threatType");
          final int _cursorIndexOfSeverity = CursorUtil.getColumnIndexOrThrow(_cursor, "severity");
          final int _cursorIndexOfConfidence = CursorUtil.getColumnIndexOrThrow(_cursor, "confidence");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfDetectionMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "detectionMethod");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<ThreatAlert> _result = new ArrayList<ThreatAlert>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final ThreatAlert _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpPacketId;
            _tmpPacketId = _cursor.getLong(_cursorIndexOfPacketId);
            final Long _tmpFlowId;
            if (_cursor.isNull(_cursorIndexOfFlowId)) {
              _tmpFlowId = null;
            } else {
              _tmpFlowId = _cursor.getLong(_cursorIndexOfFlowId);
            }
            final ThreatType _tmpThreatType;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfThreatType);
            _tmpThreatType = __converters.toThreatType(_tmp);
            final ThreatSeverity _tmpSeverity;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfSeverity);
            _tmpSeverity = __converters.toThreatSeverity(_tmp_1);
            final float _tmpConfidence;
            _tmpConfidence = _cursor.getFloat(_cursorIndexOfConfidence);
            final String _tmpDescription;
            _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            final String _tmpDetectionMethod;
            _tmpDetectionMethod = _cursor.getString(_cursorIndexOfDetectionMethod);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new ThreatAlert(_tmpId,_tmpPacketId,_tmpFlowId,_tmpThreatType,_tmpSeverity,_tmpConfidence,_tmpDescription,_tmpDetectionMethod,_tmpTimestamp);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM threats";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getCountBySeverity(final ThreatSeverity severity,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM threats WHERE severity = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    final String _tmp = __converters.fromThreatSeverity(severity);
    _statement.bindString(_argIndex, _tmp);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(0);
            _result = _tmp_1;
          } else {
            _result = 0;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
