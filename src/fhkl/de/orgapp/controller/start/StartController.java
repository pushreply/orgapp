package fhkl.de.orgapp.controller.start;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.login.LoginController;
import fhkl.de.orgapp.controller.registration.RegisterController;
import fhkl.de.orgapp.util.IMessages;

public class StartController extends Activity {
	private Button bLogin;
	private Button bRegister;
	boolean isConnected;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start);

		addListenerOnButton();

	}

	public void addListenerOnButton() {

		// check internet connection
		Context ctx = this;
		ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();

		bLogin = (Button) findViewById(R.id.LOGIN);
		bRegister = (Button) findViewById(R.id.REGISTER);

		bLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isConnected == true) {
					Intent i = new Intent(StartController.this, LoginController.class);
					startActivity(i);
				} else {
					Toast.makeText(getApplicationContext(), IMessages.NO_INTERNET_CONNECTION, Toast.LENGTH_LONG).show();
				}
			}
		});

		bRegister.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (isConnected == true) {
					Intent i = new Intent(StartController.this, RegisterController.class);
					startActivity(i);
				} else {
					Toast.makeText(getApplicationContext(), IMessages.NO_INTERNET_CONNECTION, Toast.LENGTH_LONG).show();
				}
			}
		});
	}
}