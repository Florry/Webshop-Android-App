package com.omgz.webshop;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class OrderListAll extends BaseActivity
{
	BroadcastReceiver receiver;
	GetOrders getOrders;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.order_list_all_layout);
		super.onCreate(savedInstanceState);
		
		getOrders = new GetOrders();
		getOrders.execute();
		
		if (rights > 0)
		{
			startService(new Intent(this, OrderNotificationService.class));
		}
		
		receiver = new BroadcastReceiver()
		{
			@Override
			public void onReceive(Context context, Intent intent)
			{
				// Not a very good solution
				if (page.equals(getResources().getString(R.string.order_all_title)))
				{
					final Intent i = new Intent(getBaseContext(), OrderListAll.class);
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}
				
			}
		};
	}
	
	@Override
	public boolean isCurrentIntent(Object obj)
	{
		if (getIntent().toString().equals(
				new Intent(getApplicationContext(), (Class<?>) obj).toString()))
		
		{
			return true;
		}
		return false;
	}
	
	@Override
	protected void onStart()
	{
		super.onStart();
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver,
				new IntentFilter(OrderNotificationService.UPDATE_MESSAGE));
	}
	
	class GetOrders extends AsyncTask<Void, Void, JSONArray>
	{
		@Override
		protected JSONArray doInBackground(Void... params)
		{
			try
			{
				String response = "";
				
				final DefaultHttpClient client = new DefaultHttpClient();
				if (cookies != null)
				{
					for (final Cookie cookie : cookies)
					{
						client.getCookieStore().addCookie(cookie);
					}
				}
				response = client.execute(new HttpGet(url + "/api/orders/"),
						new BasicResponseHandler());
				return new JSONArray(response);
				
			} catch (IOException | JSONException e)
			{
				e.printStackTrace();
			}
			return new JSONArray();
		}
		
		@Override
		protected void onPostExecute(JSONArray result)
		{
			final ListView listView = (ListView) findViewById(R.id.order_list_view);
			listView.setAdapter(new ListOrdersAdapter(result));
		}
	}
	
	class ListOrdersAdapter extends BaseAdapter
	{
		private final JSONArray orders;
		
		public ListOrdersAdapter(JSONArray orders)
		{
			this.orders = orders;
		}
		
		@Override
		public int getCount()
		{
			return orders.length();
		}
		
		@Override
		public Object getItem(int index)
		{
			try
			{
				return orders.getJSONObject(index);
			} catch (final JSONException e)
			{
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		public long getItemId(int index)
		{
			try
			{
				return Integer.parseInt(orders.getJSONObject(index).getString("id"));
			} catch (NumberFormatException | JSONException e)
			{
				return index;
			}
		}
		
		@Override
		public View getView(int index, View convertView, ViewGroup parent)
		{
			try
			{
				final JSONObject order = orders.getJSONObject(orders.length() - 1 - index);
				
				final View orderListItem = getLayoutInflater().inflate(R.layout.order_list_item,
						parent, false);
				final TextView user = (TextView) orderListItem.findViewById(R.id.order_list_user);
				final TextView firstname = (TextView) orderListItem
						.findViewById(R.id.order_list_name);
				final TextView address = (TextView) orderListItem
						.findViewById(R.id.order_list_address);
				final TextView totalPrice = (TextView) orderListItem
						.findViewById(R.id.order_total_price);
				final TextView products = (TextView) orderListItem
						.findViewById(R.id.order_products);
				
				final String id = order.getString("id");
				final StringBuilder stringBuilder = new StringBuilder();
				
				stringBuilder.append("order: ");
				stringBuilder.append(id);
				stringBuilder.append("\n");
				
				for (int i = 0; i < order.getJSONArray("products").length(); i++)
				{
					final JSONObject product = order.getJSONArray("products").getJSONObject(i);
					final String name = product.getJSONObject("product").getString("name");
					final int cost = (int) Double.parseDouble(product.getJSONObject("product")
							.getString("cost"));
					final int quantity = (int) Double.parseDouble(product.getJSONObject("quantity")
							.getString("quantity"));
					final int totalProductsCost = cost * quantity;
					
					stringBuilder.append(name);
					stringBuilder.append(" x");
					stringBuilder.append(quantity);
					stringBuilder.append(" = ");
					stringBuilder.append(totalProductsCost);
					stringBuilder.append("$");
					stringBuilder.append("\n");
				}
				stringBuilder.append("\n");
				products.setText(stringBuilder.toString());
				user.setText(order.getString("user"));
				totalPrice.setText("Total price: " + order.getString("totalPrice") + "$");
				
				new GetUsers(firstname, address, order.getString("user")).execute();
				
				return orderListItem;
			} catch (final JSONException e)
			{
				e.printStackTrace();
			}
			
			return null;
		}
	}
	
	class GetUsers extends AsyncTask<Void, Void, JSONObject>
	{
		TextView firstname;
		TextView address;
		String email;
		
		public GetUsers(TextView firstname, TextView address, String email)
		{
			this.firstname = firstname;
			this.address = address;
			this.email = email;
		}
		
		@Override
		protected JSONObject doInBackground(Void... arg0)
		{
			try
			{
				String response = "";
				
				final DefaultHttpClient client = new DefaultHttpClient();
				if (cookies != null)
				{
					for (final Cookie cookie : cookies)
					{
						client.getCookieStore().addCookie(cookie);
					}
				}
				response = client.execute(new HttpGet(url + "/api/users/" + email),
						new BasicResponseHandler());
				return new JSONObject(response);
				
			} catch (IOException | JSONException e)
			{
				e.printStackTrace();
			}
			return new JSONObject();
		}
		
		@Override
		protected void onPostExecute(JSONObject result)
		{
			try
			{
				firstname.setText(result.getString("firstname") + " "
						+ result.getString("lastname"));
				address.setText(result.getString("address1") + " " + result.getString("address2"));
			} catch (final JSONException e)
			{
				e.printStackTrace();
			}
			super.onPostExecute(result);
		}
	}
	
	@Override
	protected void onResume()
	{
		page = getResources().getString(R.string.order_all_title);
		super.onResume();
	}
}
