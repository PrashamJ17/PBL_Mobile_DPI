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
import com.example.dpi.models.Packet;
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
public final class PacketDao_Impl implements PacketDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Packet> __insertionAdapterOfPacket;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfDeleteOlderThan;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public PacketDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPacket = new EntityInsertionAdapter<Packet>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `packets` (`id`,`timestamp`,`sourceIp`,`destinationIp`,`sourcePort`,`destinationPort`,`protocol`,`payloadSize`,`flags`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Packet entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getTimestamp());
        statement.bindString(3, entity.getSourceIp());
        statement.bindString(4, entity.getDestinationIp());
        if (entity.getSourcePort() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getSourcePort());
        }
        if (entity.getDestinationPort() == null) {
          statement.bindNull(6);
        } else {
          statement.bindLong(6, entity.getDestinationPort());
        }
        final String _tmp = __converters.fromProtocol(entity.getProtocol());
        statement.bindString(7, _tmp);
        statement.bindLong(8, entity.getPayloadSize());
        statement.bindLong(9, entity.getFlags());
      }
    };
    this.__preparedStmtOfDeleteOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM packets WHERE timestamp < ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM packets";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final Packet packet, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPacket.insertAndReturnId(packet);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertAll(final List<Packet> packets,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPacket.insert(packets);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
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
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Packet>> observeRecentPackets(final int limit) {
    final String _sql = "SELECT * FROM packets ORDER BY timestamp DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"packets"}, new Callable<List<Packet>>() {
      @Override
      @NonNull
      public List<Packet> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSourceIp = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceIp");
          final int _cursorIndexOfDestinationIp = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationIp");
          final int _cursorIndexOfSourcePort = CursorUtil.getColumnIndexOrThrow(_cursor, "sourcePort");
          final int _cursorIndexOfDestinationPort = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationPort");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfPayloadSize = CursorUtil.getColumnIndexOrThrow(_cursor, "payloadSize");
          final int _cursorIndexOfFlags = CursorUtil.getColumnIndexOrThrow(_cursor, "flags");
          final List<Packet> _result = new ArrayList<Packet>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Packet _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
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
            final int _tmpPayloadSize;
            _tmpPayloadSize = _cursor.getInt(_cursorIndexOfPayloadSize);
            final int _tmpFlags;
            _tmpFlags = _cursor.getInt(_cursorIndexOfFlags);
            _item = new Packet(_tmpId,_tmpTimestamp,_tmpSourceIp,_tmpDestinationIp,_tmpSourcePort,_tmpDestinationPort,_tmpProtocol,_tmpPayloadSize,_tmpFlags);
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
  public Flow<List<Packet>> observePacketsSince(final long since) {
    final String _sql = "SELECT * FROM packets WHERE timestamp > ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"packets"}, new Callable<List<Packet>>() {
      @Override
      @NonNull
      public List<Packet> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfSourceIp = CursorUtil.getColumnIndexOrThrow(_cursor, "sourceIp");
          final int _cursorIndexOfDestinationIp = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationIp");
          final int _cursorIndexOfSourcePort = CursorUtil.getColumnIndexOrThrow(_cursor, "sourcePort");
          final int _cursorIndexOfDestinationPort = CursorUtil.getColumnIndexOrThrow(_cursor, "destinationPort");
          final int _cursorIndexOfProtocol = CursorUtil.getColumnIndexOrThrow(_cursor, "protocol");
          final int _cursorIndexOfPayloadSize = CursorUtil.getColumnIndexOrThrow(_cursor, "payloadSize");
          final int _cursorIndexOfFlags = CursorUtil.getColumnIndexOrThrow(_cursor, "flags");
          final List<Packet> _result = new ArrayList<Packet>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Packet _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
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
            final int _tmpPayloadSize;
            _tmpPayloadSize = _cursor.getInt(_cursorIndexOfPayloadSize);
            final int _tmpFlags;
            _tmpFlags = _cursor.getInt(_cursorIndexOfFlags);
            _item = new Packet(_tmpId,_tmpTimestamp,_tmpSourceIp,_tmpDestinationIp,_tmpSourcePort,_tmpDestinationPort,_tmpProtocol,_tmpPayloadSize,_tmpFlags);
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
  public Object getCount(final Continuation<? super Long> $completion) {
    final String _sql = "SELECT COUNT(*) FROM packets";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Long _result;
          if (_cursor.moveToFirst()) {
            final long _tmp;
            _tmp = _cursor.getLong(0);
            _result = _tmp;
          } else {
            _result = 0L;
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
