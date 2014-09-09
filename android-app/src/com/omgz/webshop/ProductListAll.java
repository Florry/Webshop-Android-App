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

public class ProductListAll extends BaseActivity
{
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		setContentView(R.layout.product_list_all_layout);
		super.onCreate(savedInstanceState);
		
		new GetProducts().execute();
	}
	
	class GetProducts extends AsyncTask<Void, Void, JSONArray>
	{
		@Override
		protected JSONArray doInBackground(Void... params)
		{
			String response = "";
			try
			{
				response = new DefaultHttpClient().execute(new HttpGet(url + "/api/products/"),
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
			final ListView listView = (ListView) findViewById(R.id.products_list_view);
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
			try
			{
				return Integer.parseInt(products.getJSONObject(index).getString("id"));
			} catch (NumberFormatException | JSONException e)
			{
				return index;
			}
		}
		
		@Override
		public View getView(int index, View convertView, ViewGroup parent)
		{
			final View productListItem = getLayoutInflater().inflate(R.layout.product_list_item,
					parent, false);
			try
			{
				final JSONObject product = products.getJSONObject(index);
				final int productId = Integer.parseInt(product.getString("id"));
				
				final TextView productName = (TextView) productListItem
						.findViewById(R.id.product_list_item_name);
				final TextView productDescription = (TextView) productListItem
						.findViewById(R.id.product_list_item_description);
				final TextView productCost = (TextView) productListItem
						.findViewById(R.id.product_list_item_cost);
				final TextView productCategories = (TextView) productListItem
						.findViewById(R.id.product_list_categories);
				final ImageView productImage = (ImageView) productListItem
						.findViewById(R.id.product_list_item_thumbnail);
				
				String categories = "";
				
				productName.setText(product.getString("name"));
				productDescription.setText(product.getString("description"));
				productCost.setText(product.getString("cost") + "$");
				
				for (int i = 0; i < product.getJSONArray("categories").length(); i++)
				{
					categories += product.getJSONArray("categories").getJSONObject(i)
							.getString("name")
							+ "\n";
				}
				
				productCategories.setText(categories);
				
				Picasso.with(getBaseContext()).load(product.getString("imgUrl")).resize(50, 50)
						.centerCrop().into(productImage);
				
				final Button button = (Button) productListItem.findViewById(R.id.product_list_buy);
				if (loggedIn)
				{
					button.setVisibility(View.VISIBLE);
					button.setOnClickListener(new OnClickListener()
					{
						@Override
						public void onClick(View createButton)
						{
							final AddToShoppingcart shoppingcart = new AddToShoppingcart();
							shoppingcart.setId(productId);
							shoppingcart.execute();
						}
					});
				} else
				{
					button.setVisibility(View.INVISIBLE);
				}
			} catch (final JSONException e)
			{
				e.printStackTrace();
			}
			
			return productListItem;
		}
	}
	
	class AddToShoppingcart extends AsyncTask<Void, Void, Boolean>
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
				final HttpPost post = new HttpPost(url + "/shoppingcart");
				final DefaultHttpClient client = new DefaultHttpClient();
				
				if (cookies != null)
				{
					for (final Cookie cookie : cookies)
					{
						client.getCookieStore().addCookie(cookie);
					}
				}
				
				final List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				
				nameValuePairs.add(new BasicNameValuePair("product", id + ""));
				nameValuePairs.add(new BasicNameValuePair("quantity", "1"));
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
				// final Intent intent = getIntent();
				// finish();
				// startActivity(intent);
				alert("Product was added to the cart!");
			} else
			{
				alert("The product could not be added!");
			}
			super.onPostExecute(result);
		}
	}
	
	@Override
	protected void onResume()
	{
		page = getResources().getString(R.string.product_all_title);
		super.onResume();
	}
	
}
