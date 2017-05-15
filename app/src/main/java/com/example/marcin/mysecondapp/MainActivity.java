// MainActivity.java
package com.example.marcin.mysecondapp;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {
// Deklaracje
   private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Opcje konfiguracji i widoku
        super.onCreate(savedInstanceState);
        this.initialize();
    }
    private void initialize() {
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }
    // Deklaracja menu
    @Override
      public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Opcja wyjscia z aplikacji w menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_end) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //Rozpocznij pomiar
    public void sendIntent(View view) {
        Intent intent= new Intent(this, DisplayMessageActivity.class);
        startActivity(intent);

    }
    public void strengthSignal(View view) {
        Intent intent1= new Intent(this, signalStrength.class);
        startActivity(intent1);

    }

}
