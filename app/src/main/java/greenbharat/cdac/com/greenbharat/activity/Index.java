package greenbharat.cdac.com.greenbharat.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import greenbharat.cdac.com.greenbharat.R;



public class Index extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_index);

        getSupportActionBar().setTitle("Index");
    }
}
