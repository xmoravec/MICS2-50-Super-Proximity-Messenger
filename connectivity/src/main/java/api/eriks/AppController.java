package api.eriks;

import android.app.Application;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;

public class AppController extends Application {
    private ConnectionServiceImpl connectionServiceImpl;

    @Override
    public void onCreate() {
        super.onCreate();
        new GoogleApiClient.Builder(this).addApi(Nearby.CONNECTIONS_API).build();
        connectionServiceImpl = ConnectionServiceImpl.getInstance(this);
        connectionServiceImpl.setGoogleApiClient();
    }

    public ConnectionServiceImpl getConnectionServiceImpl() {
        return connectionServiceImpl;
    }
}
