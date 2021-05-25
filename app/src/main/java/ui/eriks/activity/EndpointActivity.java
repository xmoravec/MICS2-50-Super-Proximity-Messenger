package ui.eriks.activity;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.Strategy;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import api.eriks.ConnectionServiceImpl;
import api.eriks.listener.ConnectionListener;
import api.eriks.listener.ConnectionService;
import api.eriks.listener.DataListener;
import api.eriks.listener.EndpointListener;
import ui.eriks.EndpointAdapter;
import ui.eriks.ProximityMessenger;
import ui.eriks.R;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public abstract class EndpointActivity extends AppCompatActivity implements EndpointListener, ConnectionListener, ConnectionService, DataListener {
    protected EndpointAdapter endpoint;
    protected TextView progressText;
    protected ConnectionServiceImpl connectionService;
    protected FloatingActionButton fab;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private static final int REQUEST_CODE_REQUIRED_PERMISSIONS = 1;
    private Toolbar myToolbar;
    private SharedPreferences colorPreferences;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        colorPreferences = getSharedPreferences("colors", MODE_PRIVATE);
        initialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initialize() {
        connectionService = ProximityMessenger.getInstance().getConnectionServiceImpl();
        fab = findViewById(R.id.fab);
        LayoutInflater.from(this);
        progressText = findViewById(R.id.txtConnecting);
        RecyclerView deviceList = findViewById(R.id.clientList);
        endpoint = new EndpointAdapter(this, new ArrayList<ConnectionServiceImpl.Endpoint>());
        endpoint.setOnClickListener(getItemClickListener());
        deviceList.setLayoutManager(new LinearLayoutManager(this));
        deviceList.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        deviceList.setAdapter(endpoint);
        myToolbar = findViewById(R.id.service_toolbar);
        updateColors();
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateColors() {
        int color = colorPreferences.getInt("Primary", getResources().getColor(R.color.colorPrimary));
        getWindow().setStatusBarColor(color);
        myToolbar.setBackgroundColor(color);
    }

    protected View.OnClickListener getItemClickListener() {
        return null;
    }

    @Override
    public String getServiceId() {
        return getPackageName();
    }

    @Override
    public Strategy getStrategy() {
        return Strategy.P2P_STAR;
    }

    @Override
    public void onEndpointDisconnected(ConnectionServiceImpl.Endpoint endpoint) {
        this.endpoint.remove(endpoint);
    }

    @Override
    public void onEndpointConnected(ConnectionServiceImpl.Endpoint endpoint) {
        progressText.setText("Connected");
        progressText.setVisibility(View.GONE);
        this.endpoint.addEndpoint(endpoint);
    }

    @Override
    public void onEndpointDiscovered(ConnectionServiceImpl.Endpoint endpoint) {}

    @Override
    public void onConnectionFailed(ConnectionServiceImpl.Endpoint endpoint) {
        progressText.setText("Connection Failed to :: " + endpoint.getName());
    }

    @Override
    protected void onStart() {
        connectionService.setNearbyConnectionListener(this);
        connectionService.setNearbyEndpointListener(this);
        connectionService.setNearByServiceListener(this);
        connectionService.setNearbyDataListener(this);
        super.onStart();
        if (!hasPermissions(this, REQUIRED_PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_REQUIRED_PERMISSIONS);
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    @CallSuper
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_REQUIRED_PERMISSIONS) {
            for (int grantResult : grantResults) {
                if (grantResult == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this, R.string.error_missing_permissions, Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
            recreate();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onReceive(ConnectionServiceImpl.Endpoint endpoint, Payload payload) {
        this.endpoint.notifyUnreadCount(endpoint);
    }
}
