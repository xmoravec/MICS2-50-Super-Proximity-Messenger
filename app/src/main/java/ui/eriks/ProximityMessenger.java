package ui.eriks;

import api.eriks.AppController;

public class ProximityMessenger extends AppController {
    private static ProximityMessenger instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static ProximityMessenger getInstance() {
        return instance;
    }
}
