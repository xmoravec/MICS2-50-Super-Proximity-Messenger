package ui.eriks.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.pes.androidmaterialcolorpickerdialog.ColorPicker;

import ui.eriks.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences colorPreferences;
    private int color;
    private ColorPicker cp;
    private Toolbar toolbar;
    private Button pickButton;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        cp = new ColorPicker(this, 255, 255, 255);
        toolbar = findViewById(R.id.profile_toolbar);
        pickButton = findViewById(R.id.pick_button);
        colorPreferences = getSharedPreferences("colors", MODE_PRIVATE);
        color = colorPreferences.getInt("Primary", getResources().getColor(R.color.colorPrimary));
        updateColors();
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Settings");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateColors() {
        getWindow().setStatusBarColor(color);
        toolbar.setBackgroundColor(color);
        pickButton.setBackgroundColor(color);
    }

    public void pickColor(View view) {
        cp.show();
        Button okColor = cp.findViewById(R.id.okColorButton);
        okColor.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                color = cp.getColor();
                cp.dismiss();
                updateColors();
                saveData();
            }
        });
    }

    private void saveData() {
        SharedPreferences.Editor editor = colorPreferences.edit();
        editor.putInt("Primary", color);
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void resetToDefault() {
        color = getResources().getColor(R.color.colorPrimary);
        saveData();
        updateColors();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset_default:
                resetToDefault();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }

}