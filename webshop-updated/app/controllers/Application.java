package controllers;

import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.index;
import views.html.edit.editPage;

public class Application extends Controller
{
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result edit()
	{
		return ok(editPage.render(null));
	}
	
	public static Result home()
	{
		return ok(index.render(null, null));
	}
	
}
