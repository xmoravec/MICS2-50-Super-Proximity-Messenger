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
import ui.eriks.ChatAdapter;
import ui.eriks.ProximityMessenger;
import ui.eriks.R;

public class ClientChatRoomActivity extends AppCompatActivity implements DataListener {
    private ChatAdapter chat;
    private EditText messageInput;
    private ConnectionServiceImpl.Endpoint endpoint;
    private ConnectionServiceImpl connectionServiceImpl;

    private Toolbar myToolbar;
    private SharedPreferences colorPreferences;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        connectionServiceImpl = ProximityMessenger.getInstance().getConnectionServiceImpl();
        connectionServiceImpl.setNearbyDataListener(this);
        getWindow().setStatusBarColor(getSharedPreferences("colors", MODE_PRIVATE).getInt("Primary", R.color.colorPrimary));
        RecyclerView recyclerView = findViewById(R.id.chatList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        chat = new ChatAdapter(this, new ArrayList<Message>());
        recyclerView.setAdapter(chat);
        colorPreferences = getSharedPreferences("colors", MODE_PRIVATE);
        if (getIntent().hasExtra("endpoint")) {
            endpoint = (ConnectionServiceImpl.Endpoint) getIntent().getSerializableExtra("endpoint");
            setTitle(endpoint.getName());
        }
        messageInput = findViewById(R.id.etMessage);
        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
        myToolbar = findViewById(R.id.chat_toolbar);
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

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onReceive(ConnectionServiceImpl.Endpoint endpoint, Payload payload) {
        String json = new String(payload.asBytes());
        Message message = new Gson().fromJson(json, Message.class);
        message.setMyChat(false);
        message.setEndpoint(endpoint);
        chat.addMessage(message);
    }

    private void sendMessage() {
        if (messageInput.getText() != null) {
            String msg = messageInput.getText().toString();
            if (msg.length() > 0)
                generateAndSendMessage(msg);
        }
    }

    private void generateAndSendMessage(String msg) {
        Message message = new Message(endpoint, msg, true, System.currentTimeMillis());
        String json = new Gson().toJson(message);
        if (connectionServiceImpl.getConnectedEndpoints().size() > 0)
            connectionServiceImpl.send(Payload.fromBytes(json.getBytes()));
        chat.addMessage(message);
        messageInput.setText("");
    }

    public static void startChatActivity(Context context, ConnectionServiceImpl.Endpoint endpoint) {
        Intent intent = new Intent(context, ClientChatRoomActivity.class);
        intent.putExtra("endpoint", endpoint);
        context.startActivity(intent);
    }
}
