package ui.eriks.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.google.android.gms.nearby.connection.ConnectionInfo;

import api.eriks.ConnectionServiceImpl.Endpoint;
import api.eriks.listener.DiscoveryListener;

public class ClientActivity extends EndpointActivity implements DiscoveryListener {

    public static void startClientActivity(Context context) {
        Intent intent = new Intent(context, ClientActivity.class);
        context.startActivity(intent);
    }

    @Override
    public String getName() {
        return getSharedPreferences("profile", MODE_PRIVATE).getString("name", "User");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("Hosts");
    }

    @Override
    protected void onStart() {
        super.onStart();
        connectionService.setNearbyDiscoveringListener(this);
        connectionService.startDiscovering();
        fab.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onConnectionInitiated(Endpoint endpoint, ConnectionInfo connectionInfo) {
        progressText.setText("Connection Initiated with " + endpoint.getName() + "\n" + "request accepting..");
        connectionService.acceptConnection(endpoint);
    }

    @Override
    public void onDiscoveryStarted() {
        progressText.setText("Discovery Started");
    }

    @Override
    public void onDiscoveryFailed() {
        progressText.setText("Discovery Failed");
    }

    @Override
    public void onEndpointDiscovered(Endpoint endpoint) {
        progressText.setText("Endpoint Discovered: " + endpoint.getName());
        connectionService.connectToEndpoint(endpoint);
    }

    public View.OnClickListener getItemClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Endpoint endpoint = (Endpoint) view.getTag();
                ClientChatActivity.startChatActivity(ClientActivity.this, endpoint);
            }
        };
    }

    public void startChatRoom(View view){
    }
}
