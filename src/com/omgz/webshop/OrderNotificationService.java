package com.omgz.webshop;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

public class OrderNotificationService extends IntentService
{
	private int oldOrderSize = 0;
	private int newOrderSize = 0;
	private String orderUser = "";
	private LocalBroadcastManager broadcaster;
	private final int updateFrequency = 5000;
	public static String UPDATE_MESSAGE = "update";
	
	public OrderNotificationService()
	{
		super("BackgroundService");
	}
	
	@Override
	public void onCreate()
	{
		broadcaster = LocalBroadcastManager.getInstance(this);
		super.onCreate();
	}
	
	public void sendResult(String message)
	{
		final Intent intent = new Intent(UPDATE_MESSAGE);
		if (message != null)
		{
			intent.putExtra(UPDATE_MESSAGE, message);
		}
		broadcaster.sendBroadcast(intent);
	}
	
	@Override
	protected void onHandleIntent(Intent arg0)
	{
		try
		{
			while (BaseActivity.loggedIn && BaseActivity.rights > 0)
			{
				Thread.sleep(updateFrequency);
				new GetOrders().execute();
				if (oldOrderSize != newOrderSize)
				{
					oldOrderSize = newOrderSize;
					final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
							.setSmallIcon(R.drawable.pw).setContentTitle("Webshop - New Order")
							.setContentText("New order from " + orderUser);
					final int mNotificationId = (int) System.currentTimeMillis();
					final NotificationManager mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
					mNotifyMgr.notify(mNotificationId, mBuilder.build());
					
					if (BaseActivity.debug == false)
					{
						@SuppressWarnings("static-access")
						final Vibrator v = (Vibrator) getApplicationContext().getSystemService(
								getBaseContext().VIBRATOR_SERVICE);
						v.vibrate(500);
					}
					sendResult("time to update!");
				}
			}
		} catch (final Exception e)
		{
			BaseActivity.err("Notification could not be executed.");
		}
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
				if (BaseActivity.cookies != null)
				{
					for (final Cookie cookie : BaseActivity.cookies)
					{
						client.getCookieStore().addCookie(cookie);
					}
				}
				response = client.execute(new HttpGet(BaseActivity.url + "/api/orders/"),
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
			newOrderSize = result.length();
			try
			{
				orderUser = result.getJSONObject(result.length() - 1).getString("user");
			} catch (final JSONException e)
			{
				orderUser = "unkown";
			}
		}
	}
}