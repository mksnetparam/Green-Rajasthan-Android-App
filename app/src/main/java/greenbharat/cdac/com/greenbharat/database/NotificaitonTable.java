package greenbharat.cdac.com.greenbharat.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by CDAC on 4/14/2017.
 */

public class NotificaitonTable {

    public static final String TABLE_NAME = "message";
    public static final String MESAAGE_ID = "_id";
    public static final String MESAAGE_TITLE = "message_title";
    public static final String MESSAGE_BODY = "message_body";
    public static final String IMAGE = "image";
    public static final String USER_ID = "user_id";
    public static final String TYPE = "type";
    public static final String is_read = "is_read";
    public static final String added_date = "added_date";

    private static final String createTableQuery = "CREATE TABLE `message` (\n" +
            "\t`_id`\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,\n" +
            "\t`message_title`\tTEXT NOT NULL ,\n" +
            "\t`message_body`\tTEXT ," +
            "\t`is_read`\tTEXT,\n" +
            "\t`image`\tTEXT, " +
            "\t`user_id`\tTEXT, " +
            "\t`type`\tTEXT ," +
            "\t`added_date`\tTEXT\n" +
            ");";


    public static void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(createTableQuery);
            Log.d("1234", "onCreate: table created");
        } catch (Exception e) {
            Log.d("1234", "create table exception " + e);
        }

    }

    public static void onUpdate(SQLiteDatabase db) {
        try {
            String query = "drop table if exists " + TABLE_NAME;
            db.execSQL(query);
            onCreate(db);
        } catch (Exception e) {
            Log.d("1234", "update table exception " + e);
        }
    }

    public static long insert(SQLiteDatabase db, ContentValues values) {
        long id = -1;
        try {
            // insert(TableName, NullColumn, ContentValues);
            id = db.insert(TABLE_NAME, null, values);
        } catch (Exception e) {
            Log.d("1234", "exception in insert " + e);
        }
        return id;
    }

    public static Cursor select(SQLiteDatabase db, String[] columns, String whereClause, String[] whereArgs, String groupBy, String having, String orderBy, String limit) {
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_NAME, columns, whereClause, whereArgs, groupBy, having, orderBy);
        } catch (Exception e) {
            Log.d("1234", "exception in select " + e);
        }
        return cursor;
    }

    public static long update(SQLiteDatabase db, ContentValues cv, String whereClause) {
        return db.update(TABLE_NAME, cv, whereClause, null);
    }

    public static long delete(SQLiteDatabase db) {
        return db.delete(TABLE_NAME, null, null);
    }

    public static Cursor selectUnreadCount(SQLiteDatabase db ,String[] coulms)
    {
        return  db.query(TABLE_NAME, coulms, is_read + " = 'no'",null,null,null,null,null);
    }
}

