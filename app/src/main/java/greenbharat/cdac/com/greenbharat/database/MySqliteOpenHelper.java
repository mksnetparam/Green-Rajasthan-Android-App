package greenbharat.cdac.com.greenbharat.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by CDAC on 4/14/2017.
 */

public class MySqliteOpenHelper extends SQLiteOpenHelper {

    public static final String DB_Name = "gb_new.db";
    public static final int version = 1;

    public MySqliteOpenHelper(Context context) {
        super(context, DB_Name, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        NotificaitonTable.onCreate(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        NotificaitonTable.onUpdate(sqLiteDatabase);
    }
}
