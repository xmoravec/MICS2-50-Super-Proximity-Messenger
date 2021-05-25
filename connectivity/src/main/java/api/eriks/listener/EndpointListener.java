package api.eriks.listener;

import api.eriks.ConnectionServiceImpl;

public interface EndpointListener {
    void onEndpointDiscovered(ConnectionServiceImpl.Endpoint endpoint);

    void onEndpointConnected(ConnectionServiceImpl.Endpoint endpoint);

    void onEndpointDisconnected(ConnectionServiceImpl.Endpoint endpoint);
}
