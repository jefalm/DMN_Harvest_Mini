package com.example.dmnharvestmini;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class ThoughtDao_Impl implements ThoughtDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Thought> __insertionAdapterOfThought;

  public ThoughtDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfThought = new EntityInsertionAdapter<Thought>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `thoughts` (`id`,`content`,`timestamp`,`dateString`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Thought entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getContent());
        statement.bindLong(3, entity.getTimestamp());
        statement.bindString(4, entity.getDateString());
      }
    };
  }

  @Override
  public Object insert(final Thought thought, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfThought.insert(thought);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object getThoughtsByDate(final String date,
      final Continuation<? super List<Thought>> $completion) {
    final String _sql = "SELECT * FROM thoughts WHERE dateString = ? ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, date);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Thought>>() {
      @Override
      @NonNull
      public List<Thought> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDateString = CursorUtil.getColumnIndexOrThrow(_cursor, "dateString");
          final List<Thought> _result = new ArrayList<Thought>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Thought _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpDateString;
            _tmpDateString = _cursor.getString(_cursorIndexOfDateString);
            _item = new Thought(_tmpId,_tmpContent,_tmpTimestamp,_tmpDateString);
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
  public Object getAllThoughts(final Continuation<? super List<Thought>> $completion) {
    final String _sql = "SELECT * FROM thoughts ORDER BY timestamp DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Thought>>() {
      @Override
      @NonNull
      public List<Thought> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfContent = CursorUtil.getColumnIndexOrThrow(_cursor, "content");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final int _cursorIndexOfDateString = CursorUtil.getColumnIndexOrThrow(_cursor, "dateString");
          final List<Thought> _result = new ArrayList<Thought>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Thought _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpContent;
            _tmpContent = _cursor.getString(_cursorIndexOfContent);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            final String _tmpDateString;
            _tmpDateString = _cursor.getString(_cursorIndexOfDateString);
            _item = new Thought(_tmpId,_tmpContent,_tmpTimestamp,_tmpDateString);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
