package databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import java.util.ArrayList;
import java.util.List;


public class userEntryDataSource {

    // Database fields
    private SQLiteDatabase userdatabase;
    private userSQLiteHelper userdbHelper;
    private String[] allColumns = { userSQLiteHelper.COLUMN_ID, userSQLiteHelper.COLUMN_USER_ID,
            userSQLiteHelper.COLUMN_USER_NAME, userSQLiteHelper.COLUMN_USER_EMAIL, userSQLiteHelper.COLUMN_USER_PHONENUMBER, userSQLiteHelper.COLUMN_USER_PUBLICKEY};

    public userEntryDataSource(Context context) {
        userdbHelper = new userSQLiteHelper(context);
    }

    public void open() throws SQLException {
        userdatabase = userdbHelper.getWritableDatabase();
    }

    public void close() {
        userdbHelper.close();
    }

    public userDbEntry createUserEntry(long USER_ID, String USER_NAME,String USER_EMAIL,String USER_PHONENUMBER, String USER_PUBLICKEY) {
        ContentValues values = new ContentValues();
        values.put(userSQLiteHelper.COLUMN_USER_ID, USER_ID);
        values.put(userSQLiteHelper.COLUMN_USER_NAME, USER_NAME);
        values.put(userSQLiteHelper.COLUMN_USER_EMAIL, USER_EMAIL);
        values.put(userSQLiteHelper.COLUMN_USER_PHONENUMBER, USER_PHONENUMBER);
        values.put(userSQLiteHelper.COLUMN_USER_PUBLICKEY, USER_PUBLICKEY);

        long insertId = userdatabase.insert(userSQLiteHelper.TABLE_USER, null, values);
        Cursor cursor = userdatabase.query(userSQLiteHelper.TABLE_USER,
                allColumns, userSQLiteHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null, null);
        cursor.moveToFirst();
        userDbEntry newDbEntry = cursorToEntry(cursor);
        cursor.close();
        return newDbEntry;
    }

    public void deleteEntry(userDbEntry id) {
        userdatabase.delete(userSQLiteHelper.TABLE_USER, userSQLiteHelper.COLUMN_ID
                + " = " + id, null);
    }

    public List<userDbEntry> getAllUserEntries() {
        List<userDbEntry> entries = new ArrayList<userDbEntry>();

        Cursor cursor = userdatabase.query(userSQLiteHelper.TABLE_USER,
                allColumns, null, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            userDbEntry entry = cursorToEntry(cursor);
            entries.add(entry);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return entries;
    }

    private userDbEntry cursorToEntry(Cursor cursor) {
        userDbEntry entry = new userDbEntry();
        entry.setId(cursor.getLong(0));
        entry.setUSER_ID(cursor.getLong(1));
        entry.setUSER_NAME(cursor.getString(2));
        entry.setUSER_EMAIL(cursor.getString(3));
        entry.setUSER_PHONENUMBER(cursor.getString(4));
        entry.setUSER_PUBLICKEY(cursor.getString(5));
        return entry;
    }
}
