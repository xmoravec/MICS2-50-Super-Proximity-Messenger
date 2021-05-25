package ui.eriks.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.jetbrains.annotations.Nullable;

import ui.eriks.R;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences colorPreferences;
    private Toolbar myToolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myToolbar = findViewById(R.id.contacts_toolbar);
        colorPreferences = getSharedPreferences("colors", MODE_PRIVATE);
        updateColors();
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateColors() {
        int color = colorPreferences.getInt("Primary", getResources().getColor(R.color.colorPrimary));
        getWindow().setStatusBarColor(color);
        myToolbar.setBackgroundColor(color);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void startService(View view) {
        HostActivity.startServiceActivity(this);
    }

    public void startClient(View view) {
        ClientActivity.startClientActivity(this);
    }

    public void beginActivity(Class activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile_info:
                beginActivity(UserInfoActivity.class);
                return true;
            case R.id.self_message:
                beginActivity(ClientChatActivity.class);
                return true;
            case R.id.action_settings:
                beginActivity(SettingsActivity.class);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
