package controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import model.OrderModel;
import model.ProductInCartModel;
import model.UserModel;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.order.showAllOrders;
import views.html.order.showOrderContents;

public class Order extends Controller
{
	@Transactional
	@Security.Authenticated(AuthenticatorLoggedIn.class)
	public static Result placeOrder(String email)
	{
		final UserModel user = controllers.User.getUser(email);
		final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		final Date date = new Date();
		
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		
		if (user.getShoppingCartContents().size() > 0)
		{
			
			final OrderModel order = new OrderModel(user.getEmail(),
					new ArrayList<ProductInCartModel>(user.getShoppingCartContents()),
					dateFormat.format(date));
			
			JPA.em().persist(order);
			JPA.em().flush();
			
			int orderId = 0;
			orderId = order.getId();
			
			user.emptyShoppingCart();
			
			return redirect(routes.Order.showContents(orderId));
		} else
		{
			flash().put("cart-empty", "yes");
			
			if (user.getRights() > 0)
			{
				return redirect(routes.User.showShoppingCartByEmail(email));
			} else
			{
				return redirect(routes.User.showShoppingCart());
			}
		}
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result showAllOrders()
	{
		return ok(showAllOrders.render(getAllOrders()));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorLoggedIn.class)
	public static Result showContents(int id)
	{
		final OrderModel order = getOrder(id);
		if (order != null)
		{
			return ok(showOrderContents.render(order, User.getUser(order.getUser())));
		}
		return ok(showOrderContents.render(order, null));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static List<OrderModel> getAllOrders()
	{
		return JPA.em().createQuery("SELECT a FROM OrderModel a", OrderModel.class).getResultList();
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorLoggedIn.class)
	public static OrderModel getOrder(int id)
	{
		return JPA.em().find(OrderModel.class, id);
	}
}