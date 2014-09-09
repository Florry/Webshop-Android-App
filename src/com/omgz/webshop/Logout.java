package com.omgz.webshop;

import android.content.Intent;
import android.os.Bundle;

public class Logout extends BaseActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		user = "";
		rights = 0;
		loggedIn = false;
		
		final Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(
				getBaseContext().getPackageName());
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
		
		alert("You were logged out!");
		
		super.onCreate(savedInstanceState);
	}
}
