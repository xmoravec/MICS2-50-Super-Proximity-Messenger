package ui.eriks.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.nearby.connection.ConnectionInfo;

import api.eriks.ConnectionServiceImpl;
import api.eriks.listener.AdvertisingListener;

public class HostActivity extends EndpointActivity implements AdvertisingListener {

    public static void startServiceActivity(Context context) {
        Intent intent = new Intent(context, HostActivity.class);
        context.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Clients");
    }

    @Override
    public String getName() {
        return getSharedPreferences("profile", MODE_PRIVATE).getString("name", "User");
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectionService.setNearbyAdvertisingListener(this);
        connectionService.startAdvertising();
    }

    @Override
    public void onAdvertisingStarted() {
        progressText.setText("Advertising Started");
    }

    @Override
    public void onAdvertisingFailed() {
        progressText.setText("Advertising Failed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        connectionService.stopAdvertising();
        connectionService.stopAllEndpoints();
    }

    @Override
    public void onConnectionInitiated(ConnectionServiceImpl.Endpoint endpoint, ConnectionInfo connectionInfo) {
        progressText.setText("Connection Initiated with " + endpoint.getName() + "\nrequest accepting...");
        connectionService.acceptConnection(endpoint);
    }

    public void startChatRoom(View view) {
        if (connectionService.getConnectedEndpoints().size() > 0)
            HostChatRoomActivity.startHostChatRoomActivity(HostActivity.this);
    }
}
