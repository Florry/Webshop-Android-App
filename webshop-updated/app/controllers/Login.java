package controllers;

import java.util.Map;

import model.UserModel;

import org.apache.commons.codec.binary.Base64;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class Login extends Controller
{
	public static Result hardLogoutUser()
	{
		session().clear();
		return redirect(routes.Application.home());
	}
	
	@Transactional
	public static Result loginUser()
	{
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final Map<String, String[]> headers = request().headers();
		
		final String email = form.get("email")[0];
		final String password = form.get("password")[0];
		
		final boolean usernameIsEmpty = "".equals(email);
		final boolean passwordIsEmpty = "".equals(password);
		
		if (usernameIsEmpty || passwordIsEmpty)
		{
			if (usernameIsEmpty)
			{
				flash().put("username-empty", "yes");
			}
			if (passwordIsEmpty)
			{
				flash().put("password-empty", "yes");
			}
			
			return redirect(routes.Application.home());
		}
		
		final UserModel user = controllers.User.getUser(email);
		if (validateUser(email, password))
		{
			final String encoded = encodeEmail(email);
			final String encodedRights = encodeEmail("" + user.getRights());
			session().put("username", encoded);
			session().put("rights", encodedRights);
			
			return redirect(headers.get("Referer")[0]);
		} else
		{
			flash().put("no-username-password-match", "yes");
			
			return redirect(routes.Application.home());
		}
		
	}
	
	@Transactional
	public static Result logoutUser()
	{
		final Map<String, String[]> headers = request().headers();
		session().clear();
		return redirect(headers.get("Referer")[0]);
	}
	
	@Transactional
	public static String decodeEmail(String encoded)
	{
		final byte[] decoded = Base64.decodeBase64(encoded.getBytes());
		return new String(decoded);
	}
	
	@Transactional
	public static String encodeEmail(String email)
	{
		final byte[] encoded = Base64.encodeBase64(email.getBytes());
		return new String(encoded);
	}
	
	@Transactional
	public static boolean validateEncodedUser(String encoded)
	{
		final String userEmail = decodeEmail(encoded);
		final UserModel user = controllers.User.getUser(userEmail);
		if (user != null)
		{
			return true;
		}
		return false;
	}
	
	@Transactional
	public static boolean validateUser(String email, String password)
	{
		final UserModel user = controllers.User.getUser(email);
		if (user != null)
		{
			if (user.getPassword().equals(password))
			{
				return true;
			}
		}
		return false;
	}
}