package api.eriks.listener;

import com.google.android.gms.nearby.connection.Payload;
import api.eriks.ConnectionServiceImpl;

public interface DataListener {
    void onReceive(ConnectionServiceImpl.Endpoint endpoint, Payload payload);
}
