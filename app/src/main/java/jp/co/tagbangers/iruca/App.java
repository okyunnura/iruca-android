package jp.co.tagbangers.iruca;

import com.deploygate.sdk.DeployGate;

public class App extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        DeployGate.install(this);
    }
}
