package jp.co.tagbangers.iruca;

import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

import jp.co.tagbangers.iruca.databinding.ActivityMainBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.internal.schedulers.ScheduledAction;
import rx.plugins.RxJavaSchedulersHook;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    String[] statuses = new String[]{"在席", "離席", "外出", "休暇", "電話中", "打ち合わせ中", "退社"};

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.appBar.content.statusPicker.setMinValue(0);
        binding.appBar.content.statusPicker.setMaxValue(statuses.length - 1);
        binding.appBar.content.statusPicker.setDisplayedValues(statuses);
        binding.appBar.content.statusPicker.setWrapSelectorWheel(false);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                String id = binding.appBar.content.idValue.getText().toString();
                String name = binding.appBar.content.nameValue.getText().toString();
                if (TextUtils.isEmpty(id) || TextUtils.isEmpty(name)) {
                    showSnackbar(view, "未設定の項目があります", Color.YELLOW);
                    return;
                }

                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit();
                editor.putString("id_preference", id);
                editor.putString("name_preference", name);
                editor.apply();

                final int selected = binding.appBar.content.statusPicker.getValue();
                String status = statuses[selected];
                String message = binding.appBar.content.messageValue.getText().toString();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://iruca.co/api/rooms/12ebc2b1-695b-4291-ba21-c8c948308ad7/")
                        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                        .build();
                IrucaService service = retrofit.create(IrucaService.class);
                service.putStatus(id, name, status, message)
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Void>() {
                            @Override
                            public void onCompleted() {
                                showSnackbar(view, "complete!", Color.GREEN);

                                binding.appBar.content.messageValue.setText("");
                                switch (selected) {
                                    case 0:
                                        binding.appBar.content.statusPicker.setValue(1);
                                        break;
                                    case 1:
                                        binding.appBar.content.statusPicker.setValue(0);
                                        break;
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                e.printStackTrace();
                                showSnackbar(view, "ERROR!!!", Color.RED);
                            }

                            @Override
                            public void onNext(Void aVoid) {

                            }
                        });
            }
        });

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (preferences.contains("id_preference")) {
            binding.appBar.content.idValue.setText(preferences.getString("id_preference", null));
        }
        if (preferences.contains("name_preference")) {
            binding.appBar.content.nameValue.setText(preferences.getString("name_preference", null));
        }

    }

    private void showSnackbar(View view, String message, int color) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(color);

        TextView snackbarTextView = (TextView) snackbar.getView().findViewById(android.support.design.R.id.snackbar_text);
        snackbarTextView.setTextColor(Color.BLACK);

        snackbar.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
