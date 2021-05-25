package ui.eriks.activity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import ui.eriks.R;

public class UserInfoActivity extends AppCompatActivity {

    private static final String NAME_KEY = "name";
    private static final String SEX_KEY = "sex";
    private static final String BIO_KEY = "bio";

    private Toolbar myToolbar;

    private SharedPreferences preferences;
    private SharedPreferences colorPreferences;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        myToolbar = findViewById(R.id.profile_toolbar);
        colorPreferences = getSharedPreferences("colors", MODE_PRIVATE);
        updateColors();
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("About you");
        preferences = getSharedPreferences("profile", MODE_PRIVATE);
        loadData();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateColors() {
        int color = colorPreferences.getInt("Primary", getResources().getColor(R.color.colorPrimary));
        getWindow().setStatusBarColor(color);
        myToolbar.setBackgroundColor(color);
    }

    private String getSex() {
        RadioGroup sexGroup = findViewById(R.id.sex_buttons);
        int id = sexGroup.getCheckedRadioButtonId();
        switch (id) {
            case R.id.radioButtonMale:
                return "Male";
            case R.id.radioButtonFemale:
                return "Female";
            case R.id.radioButtonHidden:
                return "Hidden";
            default:
                return "";
        }
    }

    private RadioButton getSexChoiceButton(String sex) {
        switch (sex) {
            case "Male":
                return findViewById(R.id.radioButtonMale);
            case "Female":
                return findViewById(R.id.radioButtonFemale);
            case "Hidden":
                return findViewById(R.id.radioButtonHidden);
            default:
                return null;
        }
    }

    private void saveData() {
        EditText editText = findViewById(R.id.name_text_input);
        EditText bioText = findViewById(R.id.bio_text_input);

        SharedPreferences.Editor editor = preferences.edit();

        String name = editText.getText().toString();
        if (!name.isEmpty()){
            editor.putString(NAME_KEY, name);
        }
        editor.putString(SEX_KEY, getSex());
        editor.putString(BIO_KEY, bioText.getText().toString());
        editor.apply();
    }

    private void loadData() {
        EditText editText = findViewById(R.id.name_text_input);
        editText.setText(preferences.getString(NAME_KEY, "User"));

        EditText bioText = findViewById(R.id.bio_text_input);
        bioText.setText(preferences.getString(BIO_KEY, ""));

        if (preferences.contains(SEX_KEY)) {
            RadioButton sexButton = getSexChoiceButton(preferences.getString(SEX_KEY, ""));
            if (sexButton != null) {
                sexButton.setChecked(true);
            }
        }
    }

    private void resetToDefault() {
        EditText editText = findViewById(R.id.name_text_input);
        RadioGroup sexGroup = findViewById(R.id.sex_buttons);

        editText.setText("User");
        sexGroup.clearCheck();
        saveData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

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
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveData();
    }
}