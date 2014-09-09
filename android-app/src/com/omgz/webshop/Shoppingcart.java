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

import com.squareup.picasso.Picasso;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class Shoppingcart extends BaseActivity
{
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.shoppingcart_layout);
		super.onCreate(savedInstanceState);
		
		new GetProducts().execute();
		
		final Button button = (Button) findViewById(R.id.shoppingcart_place_order_button);
		button.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View createButton)
			{
				new PlaceOrder().execute();
			}
		});
		
	}
	
	class GetProducts extends AsyncTask<Void, Void, JSONArray>
	{
		@Override
		protected JSONArray doInBackground(Void... params)
		{
			String response = "";
			try
			{
				
				final DefaultHttpClient client = new DefaultHttpClient();
				
				if (cookies != null)
				{
					for (final Cookie cookie : cookies)
					{
						client.getCookieStore().addCookie(cookie);
					}
				}
				response = client.execute(new HttpGet(url + "/api/shoppingcart/"),
						new BasicResponseHandler());
				return new JSONArray(response);
				
			} catch (IOException | JSONException e)
			{
				System.out.println("There was an error reading products! - " + e.getMessage());
			}
			response = "[{'description' : 'There was an error reading products', 'cost' : '', 'name': 'ERROR'}]";
			try
			{
				return new JSONArray(response);
			} catch (final JSONException e)
			{
				System.out
						.println("Somehow there was an error when saving the error message to JSON! - "
								+ e.getMessage());
			}
			return new JSONArray();
		}
		
		@Override
		protected void onPostExecute(JSONArray result)
		{
			final Button button = (Button) findViewById(R.id.shoppingcart_place_order_button);
			final TextView text = (TextView) findViewById(R.id.shoppingcart_no_products);
			if (result.length() == 0)
			{
				button.setVisibility(View.INVISIBLE);
				text.setVisibility(View.VISIBLE);
			} else
			{
				button.setVisibility(View.VISIBLE);
				text.setVisibility(View.INVISIBLE);
			}
			final ListView listView = (ListView) findViewById(R.id.shoppingcart_list_view);
			listView.setAdapter(new ListProductsAdapter(result));
		}
	}
	
	class ListProductsAdapter extends BaseAdapter
	{
		private final JSONArray products;
		
		public ListProductsAdapter(JSONArray products)
		{
			this.products = products;
		}
		
		@Override
		public int getCount()
		{
			return products.length();
		}
		
		@Override
		public Object getItem(int index)
		{
			try
			{
				return products.getJSONObject(index);
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
		public View getView(final int index, final View convertView, final ViewGroup parent)
		{
			try
			{
				final JSONObject product = products.getJSONObject(index);
				final int productId = Integer.parseInt(product.getJSONObject("product").getString(
						"id"));
				
				final View productListItem = getLayoutInflater().inflate(
						R.layout.shoppingcart_list_item, parent, false);
				final TextView productName = (TextView) productListItem
						.findViewById(R.id.shoppingcart_list_item_name);
				final TextView productCost = (TextView) productListItem
						.findViewById(R.id.shoppingcart_list_item_cost);
				final TextView productQuantity = (TextView) productListItem
						.findViewById(R.id.shoppingcart_list_item_quantity);
				final ImageView productImage = (ImageView) productListItem
						.findViewById(R.id.shoppingcart_list_item_thumbnail);
				
				productName.setText(product.getJSONObject("product").getString("name"));
				productCost.setText(product.getJSONObject("product").getString("cost") + "$");
				productQuantity.setText(product.getJSONObject("quantity").getString("quantity")
						+ " in cart");
				
				Picasso.with(getBaseContext())
						.load(product.getJSONObject("product").getString("imgUrl")).resize(50, 50)
						.centerCrop().into(productImage);
				
				final Button button = (Button) productListItem
						.findViewById(R.id.shoppingcart_list_remove_button);
				button.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View createButton)
					{
						final RemoveFromShoppingCart removeFromShoppingcart = new RemoveFromShoppingCart();
						removeFromShoppingcart.setId(productId);
						removeFromShoppingcart.execute();
					}
				});
				
				final Button addButton = (Button) productListItem
						.findViewById(R.id.shoppingcart_list_add_quantity);
				addButton.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View createButton)
					{
						try
						{
							final UpdateQuantity updateQuantity = new UpdateQuantity();
							updateQuantity.setId(productId);
							updateQuantity.setType(updateQuantity.QUANTITY_ADD);
							updateQuantity.execute();
						} catch (final NumberFormatException e)
						{
							e.printStackTrace();
						}
					}
				});
				
				final Button removeButton = (Button) productListItem
						.findViewById(R.id.shoppingcart_list_remove_quantity);
				removeButton.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View createButton)
					{
						try
						{
							final UpdateQuantity updateQuantity = new UpdateQuantity();
							updateQuantity.setId(productId);
							updateQuantity.setType(updateQuantity.QUANTITY_REMOVE);
							updateQuantity.execute();
						} catch (final NumberFormatException e)
						{
							e.printStackTrace();
						}
					}
				});
				return productListItem;
			} catch (final JSONException e)
			{
				e.printStackTrace();
			}
			return null;
		}
	}
	
	class PlaceOrder extends AsyncTask<Void, Void, Boolean>
	{
		
		@Override
		protected Boolean doInBackground(Void... arg0)
		{
			try
			{
				final HttpPost post = new HttpPost(url + "/api/order/place/" + user);
				final DefaultHttpClient client = new DefaultHttpClient();
				
				if (cookies != null)
				{
					for (final Cookie cookie : cookies)
					{
						client.getCookieStore().addCookie(cookie);
					}
				}
				client.execute(post, new BasicResponseHandler());
				return true;
			} catch (final IOException e)
			{
				e.printStackTrace();
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result)
		{
			if (result)
			{
				final Intent intent = getIntent();
				finish();
				startActivity(intent);
				alert("Order was placed!");
			} else
			{
				alert("Order could not be placed at this time!");
			}
			super.onPostExecute(result);
		}
	}
	
	class UpdateQuantity extends AsyncTask<Void, Void, Boolean>
	{
		private int id;
		private boolean type;
		public boolean QUANTITY_ADD = true;
		public boolean QUANTITY_REMOVE = false;
		
		public void setType(boolean type)
		{
			this.type = type;
		}
		
		public void setId(int id)
		{
			this.id = id;
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0)
		{
			try
			{
				HttpPost post = null;
				if (type == QUANTITY_ADD)
				{
					post = new HttpPost(url + "/shoppingcart/quantity/add/" + user);
				} else if (type == QUANTITY_REMOVE)
				{
					post = new HttpPost(url + "/shoppingcart/quantity/remove/" + user);
				}
				final DefaultHttpClient client = new DefaultHttpClient();
				
				if (cookies != null)
				{
					for (final Cookie cookie : cookies)
					{
						client.getCookieStore().addCookie(cookie);
					}
				}
				final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				
				nameValuePairs.add(new BasicNameValuePair("id", id + ""));
				nameValuePairs.add(new BasicNameValuePair("app", "true"));
				
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				client.execute(post, new BasicResponseHandler());
				return true;
			} catch (final IOException e)
			{
				e.printStackTrace();
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result)
		{
			if (result)
			{
				final Intent intent = getIntent();
				finish();
				startActivity(intent);
				shortAlert("The quantity was updated!");
			} else
			{
				shortAlert("Quantity could not be updated at this time!");
			}
			super.onPostExecute(result);
		}
	}
	
	class RemoveFromShoppingCart extends AsyncTask<Void, Void, Boolean>
	{
		private int id;
		
		public void setId(int id)
		{
			this.id = id;
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0)
		{
			try
			{
				final HttpPost post = new HttpPost(url + "/shoppingcart/remove/" + user);
				final DefaultHttpClient client = new DefaultHttpClient();
				
				if (cookies != null)
				{
					for (final Cookie cookie : cookies)
					{
						client.getCookieStore().addCookie(cookie);
					}
				}
				
				final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				
				nameValuePairs.add(new BasicNameValuePair("id", id + ""));
				nameValuePairs.add(new BasicNameValuePair("app", "true"));
				
				post.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				client.execute(post, new BasicResponseHandler());
				
				return true;
			} catch (final IOException e)
			{
				e.printStackTrace();
			}
			return false;
		}
		
		@Override
		protected void onPostExecute(Boolean result)
		{
			if (result)
			{
				final Intent intent = getIntent();
				finish();
				startActivity(intent);
				shortAlert("Product was removed!");
			} else
			{
				shortAlert("Product could not be removed!");
			}
			super.onPostExecute(result);
		}
	}
	
	@Override
	protected void onResume()
	{
		page = getResources().getString(R.string.shoppingcart_title);
		super.onResume();
	}
}
