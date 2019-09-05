package e.user.ibus.View;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import butterknife.ButterKnife;
import butterknife.InjectView;
import e.user.ibus.R;

public class MainActivity extends AppCompatActivity {

	@InjectView(R.id.toolbar1)
	Toolbar toolbar;
	@InjectView(R.id.bn_bus_route)
	Button  bn_bus_route;
	@InjectView(R.id.bn_bus_stop)
	Button  bn_bus_stop;

	//test 的 APPKey
	//public final static String APPID = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";
	//public final static String APPKey = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";

	// real 的 APPKey
	public final static String APPID = "ec95e32b647c4584ae27590ab720c0a0";
	public final static String APPKey = "fxyejxv8VeRpjPWmTbfQYgx8rcE";
	public final static String Authentication = "9b37a3a7b1b3fffe32a341e12a0287e2d9bb7244";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mainactivity_layout);
		ButterKnife.inject(this);
		initView();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
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

	private void initView(){
		int PERMISSION_ALL = 4;
		String[] PERMISSIONS = {
				android.Manifest.permission.ACCESS_NETWORK_STATE,
				android.Manifest.permission.ACCESS_FINE_LOCATION,
				android.Manifest.permission.INTERNET,
				android.Manifest.permission.ACCESS_COARSE_LOCATION
		};
		if(!hasPermissions(this, PERMISSIONS)){
			ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
		}

		setSupportActionBar(toolbar);
		getSupportActionBar().setDisplayShowTitleEnabled(false);

		bn_bus_route.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,BusRoute_menu.class));
			}
		});
		bn_bus_stop.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity( new Intent(MainActivity.this, NearbyBusStop.class));
			}
		});
	}
	public static boolean hasPermissions(Context context, String... permissions) {
		if (context != null && permissions != null) {
			for (String permission : permissions) {
				if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
					return false;
				}
			}
		}
		return true;
	}
}
