package fhkl.de.orgapp.controller.groups;

import android.os.Bundle;
import fhkl.de.orgapp.R;
import fhkl.de.orgapp.util.GroupData;
import fhkl.de.orgapp.util.MenuActivity;

public class SingleGroupController extends MenuActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singlegroup);
		this.setTitle(GroupData.getGROUPNAME());
	}
}
