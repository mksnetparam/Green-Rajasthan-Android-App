package greenbharat.cdac.com.greenbharat.activity;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import greenbharat.cdac.com.greenbharat.R;
import greenbharat.cdac.com.greenbharat.adapter.Notification_adapter;
import greenbharat.cdac.com.greenbharat.database.MySqliteOpenHelper;
import greenbharat.cdac.com.greenbharat.database.NotificaitonTable;
import greenbharat.cdac.com.greenbharat.helper.Helper;
import greenbharat.cdac.com.greenbharat.helper.MyPreferenceManager;
import greenbharat.cdac.com.greenbharat.pojo.Notification_pojo;


public class Notification extends AppCompatActivity {

    private ArrayList<Notification_pojo> notification_pojos = new ArrayList<>();
    private MyPreferenceManager myPreferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_notification);
        getSupportActionBar().setTitle("Notification");

        myPreferenceManager = new MyPreferenceManager(this);

        final MySqliteOpenHelper mySqliteOpenHelper = new MySqliteOpenHelper(this);
        SQLiteDatabase db = mySqliteOpenHelper.getReadableDatabase();
        String orderBy = NotificaitonTable.MESAAGE_ID + " DESC";
        String where = NotificaitonTable.USER_ID + " = " + myPreferenceManager.getUserPojo().getUser_id();
        Cursor cursor = NotificaitonTable.select(db, null, where, null, null, null, orderBy, null);

        if (cursor != null) {
            if (cursor.getCount() <= 0)
                Snackbar.make(findViewById(R.id.llNoti), "No Notification Found", Snackbar.LENGTH_LONG).show();
            while (cursor.moveToNext()) {
                Notification_pojo pojo = new Notification_pojo();
                pojo.setId("" + cursor.getInt(0));
                pojo.setTitle(cursor.getString(1));
                pojo.setMessage(cursor.getString(2));
                pojo.setDate(cursor.getString(7));
                pojo.setIs_read(cursor.getString(3));
                notification_pojos.add(pojo);
            }
        } else {
            Snackbar.make(findViewById(R.id.llNoti), "No Notification Found", Snackbar.LENGTH_LONG).show();
        }
        cursor.close();
        db.close();
        mySqliteOpenHelper.close();

        final Notification_adapter adapter = new Notification_adapter(this, notification_pojos);
        final ListView listView = (ListView) findViewById(R.id.list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (notification_pojos.get(i).getIs_read().equals("no")) {
                    MySqliteOpenHelper mySqliteOpenHelper = new MySqliteOpenHelper(Notification.this);
                    SQLiteDatabase db = mySqliteOpenHelper.getWritableDatabase();
                    ContentValues cv = new ContentValues();
                    cv.put(NotificaitonTable.is_read, "yes");
                    String whereClause = NotificaitonTable.MESAAGE_ID + " = '" + notification_pojos.get(i).getId() + "'";
                    NotificaitonTable.update(db, cv, whereClause);
                    db.close();
                    mySqliteOpenHelper.close();

                    notification_pojos.get(i).setIs_read("yes");
                    adapter.notifyDataSetChanged();
                }

                Notification_pojo pojo = notification_pojos.get(i);
                Helper.showAlertDialog(Notification.this, pojo.getTitle(), pojo.getMessage() + "\n\n" + pojo.getDate());
            }
        });
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.noti_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_clear_all:
                clearAllNoti();
                break;
            default:
                setResult(RESULT_OK);
                finish();

        }
        return super.onOptionsItemSelected(item);
    }

    private void clearAllNoti() {
        MySqliteOpenHelper mySqliteOpenHelper = new MySqliteOpenHelper(Notification.this);
        SQLiteDatabase db = mySqliteOpenHelper.getWritableDatabase();


        // code for clear notification by - CDAC
        NotificationManager notifManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
        Log.d("1234", "clearAllNoti: ");

        if (NotificaitonTable.delete(db) > 0) {
            myPreferenceManager.setNotificationCount(0);
            startActivity(new Intent(this, Notification.class));
            finish();
        } else Toast.makeText(this, "Can'nt be deleted", Toast.LENGTH_SHORT).show();
    }
}



