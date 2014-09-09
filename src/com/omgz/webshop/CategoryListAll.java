package com.omgz.webshop;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CategoryListAll extends BaseActivity
{
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		page = getResources().getString(R.string.category_all_title);
		
		setContentView(R.layout.categories_list_all_layout);
		super.onCreate(savedInstanceState);
		
		new GetCategories().execute();
		
	}
	
	class GetCategories extends AsyncTask<Void, Void, JSONArray>
	{
		@Override
		protected JSONArray doInBackground(Void... params)
		{
			String myResponse = "";
			try
			{
				myResponse = new DefaultHttpClient().execute(new HttpGet(url + "/api/categories/"),
						new BasicResponseHandler());
				System.out.println("this is from categories: " + myResponse);
				return new JSONArray(myResponse);
				
			} catch (IOException | JSONException e)
			{
				System.out.println("There was an error reading categories! - " + e.getMessage());
			}
			
			return new JSONArray();
		}
		
		@Override
		protected void onPostExecute(JSONArray result)
		{
			final ListView listView = (ListView) findViewById(R.id.categories_list_view);
			listView.setAdapter(new ListCategoriesAdapter(result));
		}
	}
	
	class ListCategoriesAdapter extends BaseAdapter
	{
		private final JSONArray categories;
		
		public ListCategoriesAdapter(JSONArray categories)
		{
			this.categories = categories;
		}
		
		@Override
		public int getCount()
		{
			return categories.length();
		}
		
		@Override
		public Object getItem(int index)
		{
			try
			{
				return categories.getJSONObject(index);
			} catch (final JSONException e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		public long getItemId(int index)
		{
			return index;
		}
		
		@Override
		public View getView(int index, View convertView, ViewGroup parent)
		{
			JSONObject category = null;
			try
			{
				category = categories.getJSONObject(index);
			} catch (final JSONException e)
			{
				e.printStackTrace();
			}
			final View categoryListItem = getLayoutInflater().inflate(R.layout.category_list_item,
					parent, false);
			final TextView categoryName = (TextView) categoryListItem
					.findViewById(R.id.category_list_item_name);
			try
			{
				categoryName.setText(category.getString("name"));
			} catch (final JSONException e)
			{
				e.printStackTrace();
			}
			return categoryListItem;
		}
	}
	
	@Override
	protected void onResume()
	{
		page = getResources().getString(R.string.category_all_title);
		super.onResume();
	}
	
}
