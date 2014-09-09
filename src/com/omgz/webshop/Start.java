package com.omgz.webshop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class Start extends BaseActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.start_layout);
		if (debug)
		{
			startActivity(new Intent(this, Login.class));
		}
	}
	
	@Override
	protected void onResume()
	{
		final TextView t = (TextView) findViewById(R.id.test);
		if (loggedIn)
		{
			t.setText("Logged in as: " + user);
		} else
		{
			t.setText("");
		}
		page = getResources().getString(R.string.app_name);
		super.onResume();
	}
}
