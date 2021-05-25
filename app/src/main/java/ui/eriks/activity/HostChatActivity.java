package ui.eriks.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.nearby.connection.Payload;
import com.google.gson.Gson;

import java.util.ArrayList;

import api.eriks.ConnectionServiceImpl;
import api.eriks.Message;
import api.eriks.listener.DataListener;
import api.eriks.listener.EndpointListener;
import ui.eriks.ChatAdapter;
import ui.eriks.ProximityMessenger;
import ui.eriks.R;

public class HostChatActivity extends AppCompatActivity implements DataListener, EndpointListener {
    private ChatAdapter chat;
    private EditText messageField;
    private ConnectionServiceImpl connectionServiceImpl;

    private Toolbar myToolbar;
    private SharedPreferences colorPreferences;


    public static void startHostChatRoomActivity(Context context) {
        Intent intent = new Intent(context, HostChatActivity.class);
        context.startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        connectionServiceImpl = ProximityMessenger.getInstance().getConnectionServiceImpl();
        connectionServiceImpl.setNearbyDataListener(this);
        connectionServiceImpl.setNearbyEndpointListener(this);
        getWindow().setStatusBarColor(getSharedPreferences("colors", MODE_PRIVATE).getInt("Primary", R.color.colorPrimary));
        RecyclerView recyclerView = findViewById(R.id.chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chat = new ChatAdapter(this, new ArrayList<Message>());
        recyclerView.setAdapter(chat);
        colorPreferences = getSharedPreferences("colors", MODE_PRIVATE);

        messageField = findViewById(R.id.etMessage);
        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        myToolbar = findViewById(R.id.chat_toolbar);
        updateColors();
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(getString(R.string.host_chat_room));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSubtitle();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void updateColors() {
        int color = colorPreferences.getInt("Primary", getResources().getColor(R.color.colorPrimary));
        getWindow().setStatusBarColor(color);
        myToolbar.setBackgroundColor(color);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void sendMessage() {
        if (messageField.getText() != null) {
            String msg = messageField.getText().toString();
            if (msg.length() > 0) {
                generateAndSendMessage(msg);
            }
        }
    }

    private void generateAndSendMessage(String msg) {
        Message message = new Message(msg, true, System.currentTimeMillis());
        String json = new Gson().toJson(message);
        if (connectionServiceImpl.getConnectedEndpoints().size() > 0)
            connectionServiceImpl.send(Payload.fromBytes(json.getBytes()));
        chat.addMessage(message);
        messageField.setText("");
    }

    @Override
    public void onReceive(ConnectionServiceImpl.Endpoint endpoint, Payload payload) {
        String json = new String(payload.asBytes());
        Message message = new Gson().fromJson(json, Message.class);
        message.setMyChat(false);
        message.setEndpoint(endpoint);
        chat.addMessage(message);
    }

    private void setSubtitle() {
        StringBuilder subtitle = new StringBuilder();
        for (ConnectionServiceImpl.Endpoint endpoint : connectionServiceImpl.getConnectedEndpoints()) {
            if (!subtitle.toString().equals("")) {
                subtitle.append(", ");
            }
            subtitle.append(endpoint.getName());
        }
        getSupportActionBar().setSubtitle(subtitle.toString());
    }

    @Override
    public void onEndpointDiscovered(ConnectionServiceImpl.Endpoint endpoint) { }

    @Override
    public void onEndpointConnected(ConnectionServiceImpl.Endpoint endpoint) {
        setSubtitle();
    }

    @Override
    public void onEndpointDisconnected(ConnectionServiceImpl.Endpoint endpoint) { }
}
