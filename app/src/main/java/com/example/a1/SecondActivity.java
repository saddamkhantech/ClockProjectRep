package com.example.a1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class SecondActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static final String TAG = "SecondActivity";
    private ListView lv;
    private FloatingActionButton saveCitiesButton;
    private EditText editText;
    private ArrayList<CityTimeZone> cityTimeZoneArrayList;
    private TimeZoneAdapter timeZoneAdapter;
    private ICityTimeZoneDAO iCityTimeZoneDAO;
    private BroadcastReceiver broadcastReceiver;
    private LocalBroadcastManager localBroadcastManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent intent = new Intent(SecondActivity.this, TimeZoneDLService.class);
        startService(intent);

        setTitle(R.string.second_activity_title);
        iCityTimeZoneDAO = new CityTimeZoneDbDAO(this);
        lv = findViewById(R.id.second_activity_list);

        editText = findViewById(R.id.second_activity_filter);

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadDataFromDb();
            }
        };

        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter("DOWNLOAD_COMPLETED");
        localBroadcastManager.registerReceiver(broadcastReceiver, intentFilter);

        //To handle screen rotations
        if (savedInstanceState != null) {
            cityTimeZoneArrayList = savedInstanceState.getParcelableArrayList("cityTimeZone");
        }

        loadDataFromDb();

        //Returning to Main Activity after pressing save button and using finish()


        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                timeZoneAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        localBroadcastManager.unregisterReceiver(broadcastReceiver);
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("cityTimeZone", cityTimeZoneArrayList);
    }

    private void prepareResult() {
        Intent resultIntent = new Intent();
        ArrayList<CityTimeZone> checkedCities;
        checkedCities = Helper.getCheckedCities(cityTimeZoneArrayList);
        resultIntent.putExtra("result", checkedCities);
        resultIntent.putExtra("Size", checkedCities.size());
        setResult(RESULT_OK, resultIntent);
    }

    //Returning to Main Activity if Back/Return is Pressed
    @Override
    public void onBackPressed() {
        prepareResult();
        super.onBackPressed();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int pos = lv.getPositionForView(buttonView);
        if (pos != ListView.INVALID_POSITION) {
            CityTimeZone ctz = cityTimeZoneArrayList.get(pos);
            ctz.setSelected(isChecked);
        }
    }




    private void loadDataFromDb() {
        cityTimeZoneArrayList = iCityTimeZoneDAO.getCityTimeZones();
        int size = cityTimeZoneArrayList.size();
        if (size > 0) {
            timeZoneAdapter = new TimeZoneAdapter(cityTimeZoneArrayList, this);
            lv.setAdapter(timeZoneAdapter);
            showMessage(size + " Items Loaded from the database.");
        } else {
            showMessage("No Items in the database to load.");
        }
    }

    private void deleteDb() {
        int rowsDeleted = iCityTimeZoneDAO.deleteDatabase();
        cityTimeZoneArrayList.clear();
        timeZoneAdapter.notifyDataSetChanged();
        showMessage("Database dropped. Number of tuples deleted: " + rowsDeleted);
    }

    private void showMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }
}