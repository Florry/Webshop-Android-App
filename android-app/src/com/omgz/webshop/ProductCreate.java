package com.omgz.webshop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class ProductCreate extends BaseActivity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.product_create_layout);
		super.onCreate(savedInstanceState);
		
		new GetCategoriesFromServer().execute();
		
		final Button button = (Button) findViewById(R.id.product_createButton);
		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View createButton)
			{
				boolean valid = true;
				final CreateProductOnServer createProduct = new CreateProductOnServer();
				
				final EditText productNameInput = (EditText) findViewById(R.id.product_nameInput);
				final EditText productDescriptionInput = (EditText) findViewById(R.id.product_descriptionInput);
				final EditText productCostInput = (EditText) findViewById(R.id.product_costInput);
				final EditText productRrpInput = (EditText) findViewById(R.id.product_rrpInput);
				final EditText productStockInput = (EditText) findViewById(R.id.product_stockInput);
				final EditText productImgUrlInput = (EditText) findViewById(R.id.product_imgurlInput);
				final Spinner productCategoryInput = (Spinner) findViewById(R.id.product_categories);
				
				if (productNameInput.getText().toString().trim().equals(""))
				{
					productNameInput.setError(getResources().getString(
							R.string.product_create_name_required));
					valid = false;
				}
				if (productDescriptionInput.getText().toString().trim().equals(""))
				{
					productDescriptionInput.setError(getResources().getString(
							R.string.product_create_description_required));
					valid = false;
				}
				if (productCostInput.getText().toString().trim().equals(""))
				{
					productCostInput.setError(getResources().getString(
							R.string.product_create_cost_required));
					valid = false;
				}
				if (valid)
				{
					createProduct.setProductName(productNameInput.getText().toString());
					createProduct.setDescription(productDescriptionInput.getText().toString());
					createProduct.setCost(productCostInput.getText().toString());
					createProduct.setRrp(productRrpInput.getText().toString());
					createProduct.setStock(productStockInput.getText().toString());
					if (!productImgUrlInput.getText().toString().equals(""))
					{
						createProduct.setImgUrl(productImgUrlInput.getText().toString());
					}
					createProduct.setCategory(productCategoryInput.getSelectedItemId() + "");
					
					createProduct.execute();
				}
			}
		});
	}
	
	class CreateProductOnServer extends AsyncTask<Void, Void, Boolean>
	{
		private String name;
		private String description;
		private String cost = "0";
		private String rrp = "0";
		private String stock = "0";
		private String imgUrl = imagesArr[(int) Math.round(Math.random())];
		private String category;
		
		public void setProductName(String name)
		{
			this.name = name;
		}
		
		public void setDescription(String description)
		{
			this.description = description;
		}
		
		public void setCost(String cost)
		{
			this.cost = cost;
		}
		
		public void setRrp(String rrp)
		{
			this.rrp = rrp;
		}
		
		public void setStock(String stock)
		{
			this.stock = stock;
		}
		
		public void setImgUrl(String imgUrl)
		{
			this.imgUrl = imgUrl;
		}
		
		public void setCategory(String category)
		{
			this.category = category;
		}
		
		@Override
		protected Boolean doInBackground(Void... params)
		{
			if (cookies.size() > 0)
			{
				try
				{
					final HttpPost post = new HttpPost(url + "/api/products/");
					final DefaultHttpClient client = new DefaultHttpClient();
					
					for (final Cookie cookie : cookies)
					{
						client.getCookieStore().addCookie(cookie);
					}
					
					final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					
					nameValuePairs.add(new BasicNameValuePair("name", name));
					nameValuePairs.add(new BasicNameValuePair("description", description));
					nameValuePairs.add(new BasicNameValuePair("cost", cost));
					nameValuePairs.add(new BasicNameValuePair("rrp", rrp));
					nameValuePairs.add(new BasicNameValuePair("stock", stock));
					nameValuePairs.add(new BasicNameValuePair("url", imgUrl));
					nameValuePairs.add(new BasicNameValuePair("categories", category));
					
					post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					
					client.execute(post, new BasicResponseHandler());
					
					return true;
				} catch (final IOException e)
				{
					Log.e("error" + e.getMessage(), e.getLocalizedMessage());
					e.printStackTrace();
					return false;
				}
			} else
			{
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean success)
		{
			if (success)
			{
				final Intent intent = getIntent();
				finish();
				startActivity(intent);
				alert(name + " " + getResources().getString(R.string.was_created));
			} else
			{
				alert(name + " " + getResources().getString(R.string.could_not_be_created));
			}
		}
	}
	
	class GetCategoriesFromServer extends AsyncTask<Void, Void, JSONArray>
	{
		@Override
		protected JSONArray doInBackground(Void... params)
		{
			try
			{
				final HttpGet get = new HttpGet(url + "/api/categories/");
				final DefaultHttpClient client = new DefaultHttpClient();
				final String response = client.execute(get, new BasicResponseHandler());
				
				return new JSONArray(response);
			} catch (final Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(JSONArray result)
		{
			final Spinner spinner = (Spinner) findViewById(R.id.product_categories);
			spinner.setAdapter(new CategoryAdapter(result));
		}
	}
	
	class CategoryAdapter extends BaseAdapter
	{
		private final JSONArray categories;
		
		public CategoryAdapter(JSONArray categories)
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
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public long getItemId(int index)
		{
			try
			{
				return categories.getJSONObject(index).getInt("id");
			} catch (final JSONException e)
			{
				throw new RuntimeException(e);
			}
		}
		
		@Override
		public View getView(int index, View convertView, ViewGroup parent)
		{
			final TextView textView = new TextView(getApplicationContext());
			try
			{
				final JSONObject category = categories.getJSONObject(index);
				textView.setText(category.getString("name"));
			} catch (final JSONException e)
			{
				throw new RuntimeException(e);
			}
			textView.setTextSize(18);
			return textView;
		}
	}
	
	@Override
	protected void onResume()
	{
		page = getResources().getString(R.string.product_create_title);
		super.onResume();
	}
}
