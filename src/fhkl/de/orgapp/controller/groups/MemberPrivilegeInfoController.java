package fhkl.de.orgapp.controller.groups;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.GroupData;
import fhkl.de.orgapp.util.MenuActivity;
import fhkl.de.orgapp.util.UserData;

public class MemberPrivilegeInfoController extends MenuActivity {

	Button bSave, bCancel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.member_privilege_info);

		if (GroupData.getPERSONID().equals(UserData.getPERSONID())) {
			LinearLayout privilege_options = (LinearLayout) findViewById(R.id.PRIVILEGE_OPTIONS);
			privilege_options.setVisibility(View.VISIBLE);
		}

		bSave = (Button) findViewById(R.id.SAVE_PRIVILEGES);
		bCancel = (Button) findViewById(R.id.CANCEL);
	}

}
