package controllers;

import model.UserModel;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Http.Context;
import play.mvc.Result;

public class AuthenticatorAdmin extends play.mvc.Security.Authenticator
{
	@Override
	@Transactional
	public String getUsername(Context ctx)
	{
		final String encodedUser = ctx.session().get("username");
		if (encodedUser != null)
		{
			final String user = controllers.Login.decodeEmail(encodedUser);
			final UserModel userModel = JPA.em().find(UserModel.class, user);
			
			if (userModel != null && userModel.getRights() > 0)
			{
				return userModel.getEmail();
			}
		}
		return null;
	}
	
	@Override
	public Result onUnauthorized(Context ctx)
	{
		return redirect(routes.Application.home());
	}
}
