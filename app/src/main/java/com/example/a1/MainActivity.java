package com.example.a1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import android.view.MenuItem;
import android.view.View;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView bottomNavigationView;

    private static final String TAG = "MainActivity";
    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;


    private FloatingActionButton openSecondActivity;
    private ArrayList<CityTimeZone> cityTimeZoneArrayList;
    private SelectedCityTimeZoneAdapter selectedCityTimeZoneAdapter;

    private Thread thread;
    private int delay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.main_activity_title);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.person);



        delay = 1000; //1000 ms = 1 sec
        selectedCityTimeZoneAdapter = new SelectedCityTimeZoneAdapter(cityTimeZoneArrayList, this);
        //To handle screen rotations
        if (savedInstanceState != null) {
            selectedCityTimeZoneAdapter = new SelectedCityTimeZoneAdapter(cityTimeZoneArrayList, this);
            cityTimeZoneArrayList = savedInstanceState.getParcelableArrayList("cityTimeZone");


        } else {
            cityTimeZoneArrayList = new ArrayList<>();
        }

        //Updating Times
        thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(delay);
                        updateTime();
                        runOnUiThread(() -> selectedCityTimeZoneAdapter.notifyDataSetChanged());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();



    }

    FirstFragment firstFragment = new FirstFragment();
    SecondFragment secondFragment = new SecondFragment();
    ThirdFragment thirdFragment = new ThirdFragment();
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.person:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, firstFragment).commit();
                return true;

            case R.id.home:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, secondFragment).commit();
                return true;

            case R.id.settings:
                getSupportFragmentManager().beginTransaction().replace(R.id.container, thirdFragment).commit();
                return true;
        }
        return false;
    }
    public void addCity(View view)
    {
        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE);
    }





    //updates time of selected cities using timezone info
    private void updateTime() {
        int size = cityTimeZoneArrayList.size();
        for (int i = 0; i < size; i++) {
            String timeZone = cityTimeZoneArrayList.get(i).getName();
            cityTimeZoneArrayList.get(i).setTime(Helper.convertTimeZoneToTime(timeZone));
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("cityTimeZone", cityTimeZoneArrayList);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        Thread.interrupted();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Load items from database when the app is resumed

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                int size = data.getIntExtra("Size", 0);
                if (size == 0) {
                    showMessage("0 Cities Selected!");
                } else {
                    if (cityTimeZoneArrayList.size() == 0) {
                        cityTimeZoneArrayList = data.getParcelableArrayListExtra("result");
                        selectedCityTimeZoneAdapter = new SelectedCityTimeZoneAdapter(cityTimeZoneArrayList, this);

                    } else {
                        ArrayList<CityTimeZone> temp = data.getParcelableArrayListExtra("result");
                        for (int i = 0; i < temp.size(); i++) {
                            if (cityTimeZoneArrayList.contains(temp.get(i))) {
                                showMessage("The City has already been selected.");
                            } else {
                                cityTimeZoneArrayList.add(temp.get(i));
                                selectedCityTimeZoneAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
            if (resultCode == RESULT_CANCELED) {
                showMessage("Failure!");
            }
        }
    }


    private void showMessage(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }











}