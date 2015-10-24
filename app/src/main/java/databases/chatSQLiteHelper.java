package databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class chatSQLiteHelper extends SQLiteOpenHelper {


    public static final String TABLE_CHAT = "chatlist";
    public static final String COLUMN_ID = "ID";
    public static final String COLUMN_CHAT_ID = "CHAT_ID";
    public static final String COLUMN_CHAT_SENDER_ID = "CHAT_SENDER_ID";
    public static final String COLUMN_CHAT_RECIEVER_ID = "CHAT_RECIEVER_ID";
    public static final String COLUMN_CHAT_MESSAGE = "CHAT_MESSAGE";
    public static final String COLUMN_CHAT_READ = "CHAT_READ";
    public static final String COLUMN_CHAT_DATE = "CHAT_DATE";


    public static final String DATABASE_NAME = "chat.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_CHAT + " ( " + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_CHAT_ID
            + " integer not null, " + COLUMN_CHAT_SENDER_ID
            + " integer not null, " + COLUMN_CHAT_RECIEVER_ID
            + " integer not null, " + COLUMN_CHAT_MESSAGE
            + " text not null, " + COLUMN_CHAT_READ
            + " text not null, " + COLUMN_CHAT_DATE
            + " text not null);";

    public chatSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static void create(SQLiteDatabase database){
        new chatSQLiteHelper(null).onCreate(database);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void cleanChatTable(SQLiteDatabase database){
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        create(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(chatSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHAT);
        onCreate(db);
    }
}