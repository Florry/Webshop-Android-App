package com.omgz.webshop;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.cookie.Cookie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class BaseActivity extends Activity
{
	protected final static String url = "http://192.168.0.109:9000";
	protected static String page = "";
	public static boolean loggedIn = false;
	public static String user = "";
	public static int rights = 0;
	String imagesArr[] =
	{
			"http://risalafurniture.ae/wp-content/uploads/2013/11/abstract-patterns-desktop-hd-wallpaper-14742.jpg",
			"http://hdwallpapersearly.com/wp-content/uploads/2014/03/abstract-wallpaper-6.jpg" };
	public static List<Cookie> cookies = new ArrayList<Cookie>();
	
	public final static boolean debug = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main_menu, menu);
		
		final MenuItem createProduct = menu.findItem(R.id.menu_create_product);
		final MenuItem createCategory = menu.findItem(R.id.menu_create_category);
		final MenuItem listOrders = menu.findItem(R.id.menu_all_orders);
		final MenuItem logout = menu.findItem(R.id.menu_logout);
		final MenuItem login = menu.findItem(R.id.menu_login);
		final MenuItem shoppingCart = menu.findItem(R.id.menu_shoppingcart);
		
		if (loggedIn)
		{
			logout.setVisible(true);
			login.setVisible(false);
			shoppingCart.setVisible(true);
		} else
		{
			logout.setVisible(false);
			login.setVisible(true);
			shoppingCart.setVisible(false);
		}
		if (rights > 0)
		{
			createProduct.setVisible(true);
			createCategory.setVisible(true);
			listOrders.setVisible(true);
		} else
		{
			createProduct.setVisible(false);
			createCategory.setVisible(false);
			listOrders.setVisible(false);
		}
		
		return true;
	}
	
	public void alert(String string)
	{
		System.out.println(getClass() + " - " + string);
		Toast.makeText(getApplicationContext(), string, Toast.LENGTH_LONG).show();
	}
	
	public void shortAlert(String string)
	{
		System.out.println(getClass() + " - " + string);
		Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
	}
	
	public static void log(Object obj)
	{
		System.out.println(obj);
	}
	
	public static void err(Object obj)
	{
		System.err.println(obj);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
			case R.id.menu_login:
				if (!isCurrentIntent(Login.class))
				{
					startActivity(new Intent(this, Login.class));
				}
				break;
			case R.id.menu_logout:
				startActivity(new Intent(this, Logout.class));
				break;
			case R.id.menu_shoppingcart:
				if (!isCurrentIntent(Shoppingcart.class))
				{
					startActivity(new Intent(this, Shoppingcart.class));
				}
				break;
			case R.id.menu_all_orders:
				if (!isCurrentIntent(OrderListAll.class))
				{
					startActivity(new Intent(this, OrderListAll.class));
				}
				break;
			case R.id.menu_list_products:
				if (!isCurrentIntent(ProductListAll.class))
				{
					startActivity(new Intent(this, ProductListAll.class));
				}
				break;
			case R.id.menu_create_product:
				if (isCurrentIntent(ProductCreate.class))
				{
					finish();
				}
				startActivity(new Intent(getBaseContext(), ProductCreate.class));
				break;
			case R.id.menu_create_category:
				if (isCurrentIntent(CategoryCreate.class))
				{
					finish();
				}
				startActivity(new Intent(getBaseContext(), CategoryCreate.class));
				
				break;
			case R.id.menu_list_categories:
				if (!isCurrentIntent(CategoryListAll.class))
				{
					startActivity(new Intent(getBaseContext(), CategoryListAll.class));
				}
				break;
		}
		
		return true;
	}
	
	public boolean isCurrentIntent(Object obj)
	{
		if (getIntent().toString().equals(new Intent(this, (Class<?>) obj).toString()))
		
		{
			return true;
		}
		return false;
	}
	
	@Override
	protected void onResume()
	{
		
		getActionBar().setTitle(page);
		invalidateOptionsMenu();
		super.onResume();
	}
}
