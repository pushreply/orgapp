package fhkl.de.orgapp.controller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.example.orgapp.R;

public class StartController extends Activity {
  private Button bLogin;
  private Button bRegister;
  
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.start);

    addListenerOnButton();

  }

  public void addListenerOnButton() {

    bLogin = (Button) findViewById(R.id.LOGIN);
    bRegister = (Button) findViewById(R.id.REGISTER);

    bLogin.setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {

        Intent i = new Intent(StartController.this, LoginController.class);
        startActivity(i);
      }
    });
    
    bRegister.setOnClickListener(new OnClickListener() {

      @Override
      public void onClick(View v) {

        Intent i = new Intent(StartController.this, RegisterController.class);
        startActivity(i);
      }
    });
  }
}