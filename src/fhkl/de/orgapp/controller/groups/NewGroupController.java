package fhkl.de.orgapp.controller.groups;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.IMessages;
import fhkl.de.orgapp.util.MenuActivity;

public class NewGroupController extends MenuActivity {

	AlertDialog member_question;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_group);

		Button bSubmit = (Button) findViewById(R.id.SUBMIT);
		Button bCancel = (Button) findViewById(R.id.CANCEL);

		// button click event
		bSubmit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				// creating new person in background thread
				AlertDialog.Builder builder = new AlertDialog.Builder(
						NewGroupController.this);
				builder.setMessage(IMessages.MEMBER_QUESTION);
				builder.setPositiveButton(IMessages.MANUALLY, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(NewGroupController.this,
								ManualInviteMemberController.class);
						startActivity(i);
					}
				});

				builder.setNegativeButton(IMessages.LIST, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(NewGroupController.this,
								ListInviteMemberController.class);
						startActivity(i);
					}

				});

				builder.create().show();
			}
		});

		bCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(NewGroupController.this, GroupController.class);
				startActivity(i);
			}
		});
	}
}
