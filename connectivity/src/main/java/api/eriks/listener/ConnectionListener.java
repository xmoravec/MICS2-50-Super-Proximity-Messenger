package api.eriks.listener;

import com.google.android.gms.nearby.connection.ConnectionInfo;

import api.eriks.ConnectionServiceImpl;

public interface ConnectionListener {
    void onConnectionInitiated(ConnectionServiceImpl.Endpoint endpoint, ConnectionInfo connectionInfo);
    void onConnectionFailed(ConnectionServiceImpl.Endpoint endpoint);
}
