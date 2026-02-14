package com.example.dpi.database;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.example.dpi.models.PacketFlow;
import com.example.dpi.models.Protocol;
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
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class FlowDao_Impl implements FlowDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PacketFlow> __insertionAdapterOfPacketFlow;

  private final Converters __converters = new Converters();

  private final EntityDeletionOrUpdateAdapter<PacketFlow> __updateAdapterOfPacketFlow;

  private final SharedSQLiteStatement __preparedStmtOfDeactivateOldFlows;

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  public FlowDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPacketFlow = new EntityInsertionAdapter<PacketFlow>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `flows` (`id`,`sourceIp`,`destinationIp`,`sourcePort`,`destinationPort`,`protocol`,`packetCount`,`totalBytes`,`firstSeen`,`lastSeen`,`isActive`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PacketFlow entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getSourceIp());
        statement.bindString(3, entity.getDestinationIp());
        if (entity.getSourcePort() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getSourcePort());
        }
        if (entity.getDestinationPort() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getDestinationPort());
        }
        final String _tmp = __converters.fromProtocol(entity.getProtocol());
        statement.bindString(6, _tmp);
        statement.bindLong(7, entity.getPacketCount());
        statement.bindLong(8, entity.getTotalBytes());
        statement.bindLong(9, entity.getFirstSeen());
        statement.bindLong(10, entity.getLastSeen());
        final int _tmp_1 = entity.isActive() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
      }
    };
    this.__updateAdapterOfPacketFlow = new EntityDeletionOrUpdateAdapter<PacketFlow>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `flows` SET `id` = ?,`sourceIp` = ?,`destinationIp` = ?,`sourcePort` = ?,`destinationPort` = ?,`protocol` = ?,`packetCount` = ?,`totalBytes` = ?,`firstSeen` = ?,`lastSeen` = ?,`isActive` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PacketFlow entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getSourceIp());
        statement.bindString(3, entity.getDestinationIp());
        if (entity.getSourcePort() == null) {
          statement.bindNull(4);
        } else {
          statement.bindLong(4, entity.getSourcePort());
        }
        if (entity.getDestinationPort() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getDestinationPort());
        }
        final String _tmp = __converters.fromProtocol(entity.getProtocol());
        statement.bindString(6, _tmp);
        statement.bindLong(7, entity.getPacketCount());
        statement.bindLong(8, entity.getTotalBytes());
        statement.bindLong(9, entity.getFirstSeen());
        statement.bindLong(10, entity.getLastSeen());
        final int _tmp_1 = entity.isActive() ? 1 : 0;
        statement.bindLong(11, _tmp_1);
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfDeactivateOldFlows = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE flows SET isActive = 0 WHERE lastSeen < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM flows WHERE lastSeen < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final PacketFlow flow, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPacketFlow.insertAndReturnId(flow);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object update(final PacketFlow flow, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfPacketFlow.handle(flow);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deactivateOldFlows(final long before,
      final Continuation<? super Integer> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeactivateOldFlows.acquire();
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
          __preparedStmtOfDeactivateOldFlows.release(_stmt);
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
  public Flow<List<PacketFlow>> observeActiveFlows() {
    final String _sql = "SELECT * FROM flows WHERE isActive = 1 ORDER BY lastSeen DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flows"}, new Callable<List<PacketFlow>>() {
      @Override
      @NonNull
      public List<PacketFlow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSourceIp = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceIp");
          final int _cursorIndexOfDestinationIp = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationIp");
          final int _cursorIndexOfSourcePort = CursorUtil.getColumnIndexOrThrow(_cursor, "sourcePort");
          final int _cursorIndexOfDestinationPort = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationPort");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfPacketCount = CursorUtil.getColumnIndexOrThrow(_cursor, "packetCount");
          final int _cursorIndexOfTotalBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalBytes");
          final int _cursorIndexOfFirstSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "firstSeen");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<PacketFlow> _result = new ArrayList<PacketFlow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PacketFlow _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSourceIp;
            _tmpSourceIp = _cursor.getString(_cursorIndexOfSourceIp);
            final String _tmpDestinationIp;
            _tmpDestinationIp = _cursor.getString(_cursorIndexOfDestinationIp);
            final Integer _tmpSourcePort;
            if (_cursor.isNull(_cursorIndexOfSourcePort)) {
              _tmpSourcePort = null;
            } else {
              _tmpSourcePort = _cursor.getInt(_cursorIndexOfSourcePort);
            }
            final Integer _tmpDestinationPort;
            if (_cursor.isNull(_cursorIndexOfDestinationPort)) {
              _tmpDestinationPort = null;
            } else {
              _tmpDestinationPort = _cursor.getInt(_cursorIndexOfDestinationPort);
            }
            final Protocol _tmpProtocol;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfProtocol);
            _tmpProtocol = __converters.toProtocol(_tmp);
            final int _tmpPacketCount;
            _tmpPacketCount = _cursor.getInt(_cursorIndexOfPacketCount);
            final long _tmpTotalBytes;
            _tmpTotalBytes = _cursor.getLong(_cursorIndexOfTotalBytes);
            final long _tmpFirstSeen;
            _tmpFirstSeen = _cursor.getLong(_cursorIndexOfFirstSeen);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final boolean _tmpIsActive;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_1 != 0;
            _item = new PacketFlow(_tmpId,_tmpSourceIp,_tmpDestinationIp,_tmpSourcePort,_tmpDestinationPort,_tmpProtocol,_tmpPacketCount,_tmpTotalBytes,_tmpFirstSeen,_tmpLastSeen,_tmpIsActive);
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
  public Flow<List<PacketFlow>> observeRecentFlows(final int limit) {
    final String _sql = "SELECT * FROM flows ORDER BY lastSeen DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"flows"}, new Callable<List<PacketFlow>>() {
      @Override
      @NonNull
      public List<PacketFlow> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfSourceIp = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceIp");
          final int _cursorIndexOfDestinationIp = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationIp");
          final int _cursorIndexOfSourcePort = CursorUtil.getColumnIndexOrThrow(_cursor, "sourcePort");
          final int _cursorIndexOfDestinationPort = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationPort");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfPacketCount = CursorUtil.getColumnIndexOrThrow(_cursor, "packetCount");
          final int _cursorIndexOfTotalBytes = CursorUtil.getColumnIndexOrThrow(_cursor, "totalBytes");
          final int _cursorIndexOfFirstSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "firstSeen");
          final int _cursorIndexOfLastSeen = CursorUtil.getColumnIndexOrThrow(_cursor, "lastSeen");
          final int _cursorIndexOfIsActive = CursorUtil.getColumnIndexOrThrow(_cursor, "isActive");
          final List<PacketFlow> _result = new ArrayList<PacketFlow>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PacketFlow _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpSourceIp;
            _tmpSourceIp = _cursor.getString(_cursorIndexOfSourceIp);
            final String _tmpDestinationIp;
            _tmpDestinationIp = _cursor.getString(_cursorIndexOfDestinationIp);
            final Integer _tmpSourcePort;
            if (_cursor.isNull(_cursorIndexOfSourcePort)) {
              _tmpSourcePort = null;
            } else {
              _tmpSourcePort = _cursor.getInt(_cursorIndexOfSourcePort);
            }
            final Integer _tmpDestinationPort;
            if (_cursor.isNull(_cursorIndexOfDestinationPort)) {
              _tmpDestinationPort = null;
            } else {
              _tmpDestinationPort = _cursor.getInt(_cursorIndexOfDestinationPort);
            }
            final Protocol _tmpProtocol;
            final String _tmp;
            _tmp = _cursor.getString(_cursorIndexOfProtocol);
            _tmpProtocol = __converters.toProtocol(_tmp);
            final int _tmpPacketCount;
            _tmpPacketCount = _cursor.getInt(_cursorIndexOfPacketCount);
            final long _tmpTotalBytes;
            _tmpTotalBytes = _cursor.getLong(_cursorIndexOfTotalBytes);
            final long _tmpFirstSeen;
            _tmpFirstSeen = _cursor.getLong(_cursorIndexOfFirstSeen);
            final long _tmpLastSeen;
            _tmpLastSeen = _cursor.getLong(_cursorIndexOfLastSeen);
            final boolean _tmpIsActive;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsActive);
            _tmpIsActive = _tmp_1 != 0;
            _item = new PacketFlow(_tmpId,_tmpSourceIp,_tmpDestinationIp,_tmpSourcePort,_tmpDestinationPort,_tmpProtocol,_tmpPacketCount,_tmpTotalBytes,_tmpFirstSeen,_tmpLastSeen,_tmpIsActive);
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
  public Object getActiveFlowCount(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM flows WHERE isActive = 1";
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
