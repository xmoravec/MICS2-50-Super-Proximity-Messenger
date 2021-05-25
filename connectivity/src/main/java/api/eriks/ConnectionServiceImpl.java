package api.eriks;

import android.content.Context;
import android.util.Log;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.connection.AdvertisingOptions;
import com.google.android.gms.nearby.connection.ConnectionInfo;
import com.google.android.gms.nearby.connection.ConnectionLifecycleCallback;
import com.google.android.gms.nearby.connection.ConnectionResolution;
import com.google.android.gms.nearby.connection.ConnectionsClient;
import com.google.android.gms.nearby.connection.DiscoveredEndpointInfo;
import com.google.android.gms.nearby.connection.DiscoveryOptions;
import com.google.android.gms.nearby.connection.EndpointDiscoveryCallback;
import com.google.android.gms.nearby.connection.Payload;
import com.google.android.gms.nearby.connection.PayloadCallback;
import com.google.android.gms.nearby.connection.PayloadTransferUpdate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import api.eriks.listener.AdvertisingListener;
import api.eriks.listener.ConnectionListener;
import api.eriks.listener.ConnectionService;
import api.eriks.listener.DataListener;
import api.eriks.listener.DiscoveryListener;
import api.eriks.listener.EndpointListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ConnectionServiceImpl {
    private static final String TAG = ConnectionServiceImpl.class.getSimpleName();
    private final ConnectionsClient connectionsClient;
    private final Map<String, Endpoint> endpoints = new HashMap<>();
    private final Map<String, Endpoint> pendingConnections = new HashMap<>();
    private final Map<String, Endpoint> establishedConnections = new HashMap<>();

    private AdvertisingListener advertisingListener;
    private DiscoveryListener discoveryListener;
    private DataListener dataListener;
    private EndpointListener endpointListener;
    private ConnectionListener connectionListener;
    private ConnectionService connectionService;
    private Context context;

    public static ConnectionServiceImpl getInstance(Context context) {
        synchronized (ConnectionServiceImpl.class) {
            return new ConnectionServiceImpl(context);
        }
    }

    private ConnectionServiceImpl(Context context) {
        this.context = context;
        connectionsClient = Nearby.getConnectionsClient(context);
    }

    private final ConnectionLifecycleCallback connectionLifecycleCallback = new ConnectionLifecycleCallback() {
        @Override
        public void onConnectionInitiated(String endpointId, ConnectionInfo connectionInfo) {
            Endpoint endpoint = new Endpoint(endpointId, connectionInfo.getEndpointName());
            pendingConnections.put(endpointId, endpoint);
            connectionListener.onConnectionInitiated(endpoint, connectionInfo);
        }

        @Override
        public void onConnectionResult(String endpointId, ConnectionResolution result) {
            if (result.getStatus().isSuccess()) {
                connectedToEndpoint(pendingConnections.remove(endpointId));
            } else {
                connectionListener.onConnectionFailed(pendingConnections.remove(endpointId));
            }
        }

        @Override
        public void onDisconnected(String endpointId) {
            if (establishedConnections.containsKey(endpointId))
                disconnectedFromEndpoint(establishedConnections.get(endpointId));
        }
    };

    private final PayloadCallback payloadCallback = new PayloadCallback() {
        @Override
        public void onPayloadReceived(String endpointId, Payload payload) {
            dataListener.onReceive(establishedConnections.get(endpointId), payload);
        }

        @Override
        public void onPayloadTransferUpdate(String endpointId, PayloadTransferUpdate update) {}
    };

    public void startAdvertising() {
        final String localEndpointName = connectionService.getName();
        Task<Void> voidTask = connectionsClient.startAdvertising(localEndpointName, connectionService.getServiceId(), connectionLifecycleCallback,
                new AdvertisingOptions(connectionService.getStrategy()));

        voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unusedResult) {
                advertisingListener.onAdvertisingStarted();
            }
        });

        voidTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                advertisingListener.onAdvertisingFailed();
            }
        });
    }

    public void stopAdvertising() {
        connectionsClient.stopAdvertising();
    }

    public void acceptConnection(final Endpoint endpoint) {
        Task<Void> voidTask = connectionsClient.acceptConnection(endpoint.getId(), payloadCallback);
        voidTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) { }
        });
    }

    public void startDiscovering() {
        endpoints.clear();
        Task<Void> voidTask = connectionsClient.startDiscovery(connectionService.getServiceId(), endpointDiscoveryCallback,
                new DiscoveryOptions(connectionService.getStrategy()));
        voidTask.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unusedResult) {
                discoveryListener.onDiscoveryStarted();
            }
        });

        voidTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                discoveryListener.onDiscoveryFailed();
            }
        });
    }

    private final EndpointDiscoveryCallback endpointDiscoveryCallback = new EndpointDiscoveryCallback() {
        @Override
        public void onEndpointFound(String endpointId, DiscoveredEndpointInfo info) {
            if (connectionService.getServiceId().equals(info.getServiceId())) {
                Endpoint endpoint = new Endpoint(endpointId, info.getEndpointName());
                endpoints.put(endpointId, endpoint);
                endpointListener.onEndpointDiscovered(endpoint);
            }
        }

        @Override
        public void onEndpointLost(String endpointId) { }
    };

    public void stopAllEndpoints() {
        connectionsClient.stopAllEndpoints();
        endpoints.clear();
        pendingConnections.clear();
        establishedConnections.clear();
    }

    public void connectToEndpoint(final Endpoint endpoint) {
        Task<Void> voidTask = connectionsClient.requestConnection(connectionService.getName(), endpoint.getId(), connectionLifecycleCallback);
        voidTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                connectionListener.onConnectionFailed(endpoint);
            }
        });
    }

    private void connectedToEndpoint(Endpoint endpoint) {
        establishedConnections.put(endpoint.getId(), endpoint);
        endpointListener.onEndpointConnected(endpoint);
    }

    private void disconnectedFromEndpoint(Endpoint endpoint) {
        establishedConnections.remove(endpoint.getId());
        endpointListener.onEndpointDisconnected(endpoint);
    }

    public Set<Endpoint> getConnectedEndpoints() {
        return new HashSet<>(establishedConnections.values());
    }

    public void send(Payload payload) {
        send(payload, establishedConnections.keySet());
    }

    private void send(Payload payload, Set<String> endpoints) {
        Task<Void> voidTask = connectionsClient.sendPayload(new ArrayList<>(endpoints), payload);
        voidTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                logW("sendPayload() failed.", e);
            }
        });
    }

    public void setGoogleApiClient() {
    }

    public void setNearbyAdvertisingListener(AdvertisingListener advertisingListener) {
        this.advertisingListener = advertisingListener;
    }

    public void setNearbyDiscoveringListener(DiscoveryListener mDiscoveryListener) {
        this.discoveryListener = mDiscoveryListener;
    }

    public void setNearbyDataListener(DataListener mDataListener) {
        this.dataListener = mDataListener;
    }

    public void setNearbyEndpointListener(EndpointListener mEndpointListener) {
        this.endpointListener = mEndpointListener;
    }

    public void setNearbyConnectionListener(ConnectionListener mConnectionListener) {
        this.connectionListener = mConnectionListener;
    }

    public void setNearByServiceListener(ConnectionService mConnectionService) {
        this.connectionService = mConnectionService;
    }

    @CallSuper
    public void logW(String msg, Throwable e) {
        Log.w(TAG, msg, e);
    }

    public static class Endpoint implements Serializable {
        @NonNull
        private final String id;
        @NonNull
        private final String name;
        private int unreadCount;

        private Endpoint(@NonNull String id, @NonNull String name) {
            this.id = id;
            this.name = name;
        }

        @NonNull
        public String getId() {
            return id;
        }

        @NonNull
        public String getName() {
            return name;
        }

        public int getUnreadCount() {
            return unreadCount;
        }

        public void setUnreadCount(int unreadCount) {
            this.unreadCount = unreadCount;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof Endpoint && id.equals(((Endpoint) other).id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }

        @Override
        public String toString() {
            return String.format("Endpoint{id=%s, name=%s}", id, name);
        }
    }
}
