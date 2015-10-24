package databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class userSQLiteHelper extends SQLiteOpenHelper {


    public static final String TABLE_USER = "userlist";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USER_NAME = "user_name";
    public static final String COLUMN_USER_EMAIL = "user_email";
    public static final String COLUMN_USER_PHONENUMBER = "user_phonenumber";
    public static final String COLUMN_USER_PUBLICKEY = "user_publickey";


    public static final String DATABASE_NAME = "user.db";
    private static final int DATABASE_VERSION = 2;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_USER + " ( " + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_USER_ID
            + " integer not null, " + COLUMN_USER_NAME
            + " text not null, " + COLUMN_USER_EMAIL
            + " text not null, " + COLUMN_USER_PHONENUMBER
            + " text not null, " + COLUMN_USER_PUBLICKEY
            + " text not null);";

    public userSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    private static void create(SQLiteDatabase database){
        new userSQLiteHelper(null).onCreate(database);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void cleanUserTable(SQLiteDatabase database){
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        create(database);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(userSQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        onCreate(db);
    }
}
