package nicolasalbert.org.couchbase_lite_replication_memory;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.replicator.Replication;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements Replication.ChangeListener {
    final long cap = 20000;

    Runtime runtime = Runtime.getRuntime();
    Replication replication;

    long tick = 0;
    long shift = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            Manager manager = new Manager(new AndroidContext(this), Manager.DEFAULT_OPTIONS);
            Database db = manager.getDatabase("hugebase");
            db.delete();
            db = manager.getDatabase("hugebase");
            replication = db.createPullReplication(new URL("http://54.171.110.0:5959/hugebase"));
            Log.i("app", "db created");
            replication.addChangeListener(this);
            replication.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void changed(Replication.ChangeEvent event) {
        long now = System.currentTimeMillis();
        if (now > tick) {
            Log.i("app", event.getStatus().name() + " [docs / mo] " + (shift + event.getCompletedChangeCount()) + " / " + (runtime.totalMemory() - runtime.freeMemory()) / 1048576L);
            tick = now + 2500;

            if (event.getCompletedChangeCount() >= cap) {
                Log.i("app", "cap " + cap + " reached, stop");
                shift += event.getCompletedChangeCount();
                replication.stop();
                replication.removeChangeListener(this);
                replication = replication.getLocalDatabase().createPullReplication(replication.getRemoteUrl());
                replication.addChangeListener(this);
                replication.start();
            }
        }
    }
}
