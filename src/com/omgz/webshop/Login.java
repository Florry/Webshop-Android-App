package com.omgz.webshop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Login extends BaseActivity
{
	private EditText usernameInput;
	private EditText passwordInput;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.login_layout);
		
		final Button button = (Button) findViewById(R.id.login_login_button);
		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View createButton)
			{
				boolean valid = true;
				usernameInput = (EditText) findViewById(R.id.login_username);
				passwordInput = (EditText) findViewById(R.id.login_password);
				final LoginTask login = new LoginTask();
				
				// Debug ----------------
				if (debug)
				{
					usernameInput.setText("admin");
					passwordInput.setText("admin");
				}
				// ----------------------
				
				if (usernameInput.getText().toString().trim().equals(""))
				{
					usernameInput.setError(getResources().getString(R.string.login_email_required));
					valid = false;
				}
				if (passwordInput.getText().toString().trim().equals(""))
				{
					passwordInput.setError(getResources().getString(
							R.string.login_password_required));
					valid = false;
				}
				if (valid)
				{
					login.setEmail(usernameInput.getText().toString());
					login.setPassword(passwordInput.getText().toString());
					
					login.execute();
				}
			}
			
		});
		
		super.onCreate(savedInstanceState);
		
		// Debug ----------------
		if (debug)
		{
			button.callOnClick();
		}
		// ----------------------
	}
	
	class LoginTask extends AsyncTask<Void, Void, String>
	{
		private String email = "";
		private String password = "";
		
		public void setEmail(String email)
		{
			this.email = email;
		}
		
		public void setPassword(String password)
		{
			this.password = password;
		}
		
		@Override
		protected String doInBackground(Void... params)
		{
			final HttpPost post = new HttpPost(url + "/api/login/");
			final DefaultHttpClient client = new DefaultHttpClient();
			String response = "";
			final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("email", email));
			nameValuePairs.add(new BasicNameValuePair("password", password));
			
			try
			{
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				response = client.execute(post, new BasicResponseHandler());
			} catch (final IOException e)
			{
				e.printStackTrace();
			}
			
			cookies = client.getCookieStore().getCookies();
			
			return response;
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			if (result == null)
			{
				alert("there was an error contacting the server");
			} else
			{
				
				for (final Cookie cookie : cookies)
				{
					if ("PLAY_SESSION".equals(cookie.getName()))
					{
						loggedIn = true;
						user = email;
						
						final int start = cookie.getValue().lastIndexOf("=");
						final int end = start + 2;
						final String rightsString = cookie.getValue().substring(start + 1, end);
						rights = Integer.parseInt(rightsString);
						
						alert(result);
						finish();
						return;
					}
				}
				usernameInput.setError(null);
				passwordInput.setError(null);
				alert("Email or password invalid!");
			}
		}
		
	}
	
	@Override
	protected void onResume()
	{
		page = getResources().getString(R.string.login_title);
		super.onResume();
	}
}
