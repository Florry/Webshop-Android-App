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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CategoryCreate extends BaseActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.category_create_layout);
		super.onCreate(savedInstanceState);
		
		final Button button = (Button) findViewById(R.id.category_create_button);
		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View createButton)
			{
				final CreateCategoryOnServer createCategory = new CreateCategoryOnServer();
				final EditText categoryNameText = (EditText) findViewById(R.id.category_name_input);
				final String categoryName = categoryNameText.getText().toString();
				if (categoryNameText.getText().toString().trim().equals(""))
				{
					categoryNameText.setError(getResources().getString(
							R.string.category_create_name_required));
				} else
				{
					createCategory.setCategoryName(categoryName);
					createCategory.execute();
				}
				
			}
		});
	}
	
	class CreateCategoryOnServer extends AsyncTask<Void, Void, Boolean>
	{
		String name = "";
		String categoryName = "";
		
		public void setCategoryName(String name)
		{
			this.categoryName = name;
			this.name = name;
		}
		
		@Override
		protected Boolean doInBackground(Void... params)
		{
			if (cookies.size() > 0)
			{
				try
				{
					final HttpPost post = new HttpPost(url + "/api/categories/");
					final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					final DefaultHttpClient client = new DefaultHttpClient();
					
					for (final Cookie cookie : cookies)
					{
						client.getCookieStore().addCookie(cookie);
					}
					
					nameValuePairs.add(new BasicNameValuePair("name", categoryName));
					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					
					client.execute(post, new BasicResponseHandler());
					return true;
				} catch (final IOException e)
				{
					Log.e("error", e.getMessage());
					return false;
				}
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean success)
		{
			final String categoryName = name;
			if (success)
			{
				final Intent intent = getIntent();
				finish();
				startActivity(intent);
				alert(categoryName + " " + getResources().getString(R.string.was_created));
			} else
			{
				alert(categoryName + " " + getResources().getString(R.string.could_not_be_created));
			}
		}
	}
	
	@Override
	protected void onResume()
	{
		page = getResources().getString(R.string.category_create_title);
		super.onResume();
	}
}
