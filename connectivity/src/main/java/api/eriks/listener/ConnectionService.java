package api.eriks.listener;

import com.google.android.gms.nearby.connection.Strategy;

public interface ConnectionService {
    String getName();
    Strategy getStrategy();
    String getServiceId();
}
