package databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;


public class chatEntryDataSource {

    // Database fields
    private SQLiteDatabase chatdatabase;
    private chatSQLiteHelper chatdbHelper;
    private String[] allColumns = { chatSQLiteHelper.COLUMN_ID, chatSQLiteHelper.COLUMN_CHAT_ID,
            chatSQLiteHelper.COLUMN_CHAT_SENDER_ID, chatSQLiteHelper.COLUMN_CHAT_RECIEVER_ID, chatSQLiteHelper.COLUMN_CHAT_MESSAGE, chatSQLiteHelper.COLUMN_CHAT_READ, chatSQLiteHelper.COLUMN_CHAT_DATE};

    public chatEntryDataSource(Context context) {
        chatdbHelper = new chatSQLiteHelper(context);
    }

    public void open() throws SQLException {
        chatdatabase = chatdbHelper.getWritableDatabase();
    }

    public void close() {
        chatdbHelper.close();
    }

    public chatDbEntry createChatEntry(long CHAT_ID, long CHAT_SENDER_ID,long CHAT_RECIEVER_ID,String CHAT_MESSAGE, String CHAT_READ, String CHAT_DATE) {
        ContentValues values = new ContentValues();
        values.put(chatSQLiteHelper.COLUMN_CHAT_ID, CHAT_ID);
        values.put(chatSQLiteHelper.COLUMN_CHAT_SENDER_ID, CHAT_SENDER_ID);
        values.put(chatSQLiteHelper.COLUMN_CHAT_RECIEVER_ID, CHAT_RECIEVER_ID);
        values.put(chatSQLiteHelper.COLUMN_CHAT_MESSAGE, CHAT_MESSAGE);
        values.put(chatSQLiteHelper.COLUMN_CHAT_READ, CHAT_READ);
        values.put(chatSQLiteHelper.COLUMN_CHAT_DATE, CHAT_DATE);

        long insertId = chatdatabase.insert(chatSQLiteHelper.TABLE_CHAT, null, values);
        Cursor cursor = chatdatabase.query(chatSQLiteHelper.TABLE_CHAT,
                allColumns, chatSQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null, null);
        cursor.moveToFirst();
        chatDbEntry newDbEntry = cursorToEntry(cursor);
        cursor.close();
        return newDbEntry;
    }

    public void deleteEntry(chatDbEntry id) {
        chatdatabase.delete(chatSQLiteHelper.TABLE_CHAT, chatSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<chatDbEntry> getAllChatEntries() {
        List<chatDbEntry> entries = new ArrayList<chatDbEntry>();

        Cursor cursor = chatdatabase.query(chatSQLiteHelper.TABLE_CHAT,
                allColumns, null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            chatDbEntry entry = cursorToEntry(cursor);
            entries.add(entry);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return entries;
    }

    private chatDbEntry cursorToEntry(Cursor cursor) {
        chatDbEntry entry = new chatDbEntry();
        entry.setId(cursor.getLong(0));
        entry.setCHAT_ID(cursor.getLong(1));
        entry.setCHAT_SENDER_ID(cursor.getLong(2));
        entry.setCHAT_RECIEVER_ID(cursor.getLong(3));
        entry.setCHAT_MESSAGE(cursor.getString(4));
        entry.setCHAT_READ(cursor.getString(5));
        entry.setCHAT_DATE(cursor.getString(6));
        return entry;
    }
}