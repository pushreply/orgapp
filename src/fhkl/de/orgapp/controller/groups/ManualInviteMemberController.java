package fhkl.de.orgapp.controller.groups;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.GridLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.controller.start.StartController;
import fhkl.de.orgapp.util.MenuActivity;

public class ManualInviteMemberController extends MenuActivity {

	LinearLayout containerLayout;
	GridLayout textLayout;
	private String personIdLoggedPerson;

	private ImageButton bAdd;
	private Button bSubmit;
	private Button bCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.invite_member_manual);

		getIntent().putExtra("cnt", "0");

		personIdLoggedPerson = getIntent().getStringExtra("UserId");
		containerLayout = (LinearLayout) findViewById(R.id.LinearLayout);
		textLayout = new GridLayout(ManualInviteMemberController.this);
		LayoutParams params = new LayoutParams();
		params.width = LayoutParams.MATCH_PARENT;
		params.height = LayoutParams.WRAP_CONTENT;
		textLayout.setLayoutParams(params);
		textLayout.setOrientation(GridLayout.HORIZONTAL);
		textLayout.setColumnCount(4);

		containerLayout.addView(textLayout);
		bAdd = (ImageButton) findViewById(R.id.ADD);
		bSubmit = (Button) findViewById(R.id.SUBMIT);
		bCancel = (Button) findViewById(R.id.CANCEL);

		bAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				Integer tmpCnt = Integer.valueOf(getIntent().getStringExtra("cnt")
						.toString());

				EditText editText = new EditText(ManualInviteMemberController.this);
				editText.setId(tmpCnt);
				editText.setInputType(InputType.TYPE_CLASS_TEXT
						| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
				editText.setHint(R.string.EMAIL);

				tmpCnt++;

				ImageButton imageButton = new ImageButton(
						ManualInviteMemberController.this);
				imageButton.setId(tmpCnt);
				imageButton.setImageResource(R.drawable.ic_action_remove);

				tmpCnt++;
				getIntent().putExtra("cnt", tmpCnt.toString());

				textLayout.addView(editText);
				LayoutParams layoutParams = (GridLayout.LayoutParams) editText
						.getLayoutParams();
				layoutParams.columnSpec = GridLayout.spec(0, 2);
				layoutParams.setGravity(Gravity.FILL);
				editText.setLayoutParams(layoutParams);

				textLayout.addView(imageButton);
				layoutParams = (GridLayout.LayoutParams) imageButton.getLayoutParams();
				layoutParams.columnSpec = GridLayout.spec(3);
				layoutParams.width = LayoutParams.WRAP_CONTENT;
				layoutParams.height = LayoutParams.WRAP_CONTENT;
				imageButton.setLayoutParams(layoutParams);

			}
		});

		bSubmit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				int length = textLayout.getChildCount();
				String[] editTextArray = new String[length];
				for (int i = 0; i < length; i++) {
					if (i % 2 == 0) {
						EditText tmp = (EditText) textLayout.getChildAt(i);
						editTextArray[i] = tmp.getText().toString();
					}
				}
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