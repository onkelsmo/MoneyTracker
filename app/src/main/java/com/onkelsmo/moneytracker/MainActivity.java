package com.onkelsmo.moneytracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private String moneyInput;
    private TextView tvLimitValue;
    private TextView tvRemainingValue;
    private float defaultMoneyValue = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                */
                showInputBox();
            }
        });

        // DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        //        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        //drawer.setDrawerListener(toggle);
        //toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        initializeContent();
    }

    private void showInputBox() {
        final EditText editTextMoneyInput = new EditText(this);
        editTextMoneyInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        editTextMoneyInput.setHint("12,34");

        new AlertDialog.Builder(this)
                .setTitle("Spend money")
                .setView(editTextMoneyInput)
                .setPositiveButton("spend", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        moneyInput = editTextMoneyInput.getText().toString();
                        if (moneyInput.equals("")) {
                            displayFeedbackMessage(R.string.emptyValue);
                            return;
                        }
                        float remaining = Float.valueOf(tvRemainingValue.getText().toString());
                        float input = Float.valueOf(moneyInput);
                        float newValue = remaining - input;
                        if (newValue < 0) {
                            displayFeedbackMessage(R.string.notEnoughMoney);
                            return;
                        }
                        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putFloat(getString(R.string.saved_remaining_money), newValue);
                        editor.apply();

                        tvRemainingValue = (TextView)findViewById(R.id.tvRemainingValue);
                        tvRemainingValue.setText(String.format("%s",newValue));
                    }
                    // TODO: 19.06.2016 reset button!!!
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .show();
    }

    private void displayFeedbackMessage(int emptyValue) {
        Context context = getApplicationContext();
        CharSequence text = getString(emptyValue);
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    private void initializeContent() {

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putFloat(getString(R.string.saved_limit_value), defaultMoneyValue);
        editor.apply();

        sharedPref = getPreferences(Context.MODE_PRIVATE);
        float savedRemainingMoney = sharedPref.getFloat(getString(R.string.saved_remaining_money), defaultMoneyValue);

        tvLimitValue = (TextView)findViewById(R.id.tvLimitValue);
        tvLimitValue.setText(String.format("%s",defaultMoneyValue));
        tvRemainingValue = (TextView)findViewById(R.id.tvRemainingValue);
        tvRemainingValue.setText(String.format("%s",savedRemainingMoney));
    }

    @Override
    public void onBackPressed() {
        /*
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        */
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_reset) {
            showResetBox();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showResetBox() {
        new AlertDialog.Builder(this)
                .setTitle("Reset Limit")
                .setPositiveButton("reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
                        float savedLimit = sharedPref.getFloat(getString(R.string.saved_limit_value), defaultMoneyValue);
                        tvLimitValue = (TextView)findViewById(R.id.tvLimitValue);
                        tvLimitValue.setText(String.format("%s",savedLimit));

                        sharedPref = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putFloat(getString(R.string.saved_remaining_money), savedLimit);
                        editor.apply();
                        tvRemainingValue = (TextView)findViewById(R.id.tvRemainingValue);
                        tvRemainingValue.setText(String.format("%s",savedLimit));
                        displayFeedbackMessage(R.string.limitReseted);
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {}
                })
                .show();
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        /*
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        */
        return true;
    }
}
