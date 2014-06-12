package fhkl.de.orgapp.controller.groups;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.start.StartController;
import fhkl.de.orgapp.util.MenuActivity;

public class ManualInviteMemberController extends MenuActivity {

	LinearLayout containerLayout;
	private String personIdLoggedPerson;

	private ImageButton bAdd;
	private Button bSubmit;
	private Button bCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_member_manual);
		containerLayout = (LinearLayout) findViewById(R.id.LinearLayout);

		personIdLoggedPerson = getIntent().getStringExtra("UserId");

		bAdd = (ImageButton) findViewById(R.id.ADD);
		bSubmit = (Button) findViewById(R.id.SUBMIT);
		bCancel = (Button) findViewById(R.id.CANCEL);

		bAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText editText = new EditText(ManualInviteMemberController.this);
				editText.setId(435349689);
				editText.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				editText.setHint(R.string.EMAIL);
				editText.setGravity(Gravity.TOP);
				containerLayout.addView(editText);
				LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) editText
						.getLayoutParams();
				layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
				editText.setLayoutParams(layoutParams);
			}
		});

		bSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});

		bCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ToDo: StartController to single group controller
				Intent intent = new Intent(ManualInviteMemberController.this,
						StartController.class);
				intent.putExtra("UserId", personIdLoggedPerson);
				startActivity(intent);
			}
		});
	}
}