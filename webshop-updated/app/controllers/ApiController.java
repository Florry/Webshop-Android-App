package controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import model.CategoryModel;
import model.OrderModel;
import model.ProductInCartModel;
import model.ProductModel;
import model.UserModel;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

public class ApiController extends Controller
{
	@Transactional
	@Security.Authenticated(AuthenticatorLoggedIn.class)
	public static Result showShoppingCart()
	{
		final String encodedUser = session().get("username");
		final String email = Login.decodeEmail(encodedUser);
		final UserModel user1 = User.getUser(email);
		
		List<ProductInCartModel> products = new ArrayList<ProductInCartModel>();
		for (ProductInCartModel product : user1.getShoppingCartContents())
		{
			products.add(product);
		}
		return ok(Json.toJson(products));
	}
	
	@Transactional
	public static Result loginAppUser()
	{
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		
		final String email = form.get("email")[0];
		final String password = form.get("password")[0];
		
		System.out.println("User: " + email + " password: " + password);
		
		final boolean usernameIsEmpty = "".equals(email);
		final boolean passwordIsEmpty = "".equals(password);
		
		if (usernameIsEmpty || passwordIsEmpty)
		{
			return ok("Bad username or password!");
		}
		
		final UserModel user = controllers.User.getUser(email);
		if (Login.validateUser(email, password))
		{
			final String encoded = Login.encodeEmail(email);
			session().put("username", encoded);
			session().put("rights", "" + user.getRights());
			
			return ok("You were logged in!");
		} else
		{
			return ok("Login details not correct!");
		}
		
	}
	
	@Transactional
	public static Result logoutAppUser()
	{
		if (session().containsKey("username"))
		{
			session().clear();
			return ok("You were logged out!");
		} else
		{
			return ok("You were never logged in!");
		}
	}
	
	@Transactional
	public static Result getAllProducts()
	{
		List<ProductModel> products = getAllInStoreProducts();
		
		return ok(Json.toJson(products));
	}
	
	@Transactional
	public static List<ProductModel> getAllInStoreProducts()
	{
		return JPA
				.em()
				.createQuery("SELECT a FROM ProductModel a WHERE inStore = true",
						ProductModel.class).getResultList();
	}
	
	@Transactional
	public static Result getAllCategories()
	{
		List<CategoryModel> categories = new ArrayList<CategoryModel>();
		categories = Category.getAllCategories();
		
		return ok(Json.toJson(categories));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result addCategoryFromApp()
	{
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final String name = form.get("name")[0];
		System.out.print("name = " + name);
		if (name != "")
		{
			final CategoryModel category = new CategoryModel(name, 0);
			
			JPA.em().persist(category);
		}
		return ok();
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result addProductFromApp()
	{
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final String name = form.get("name")[0];
		final String description = form.get("description")[0];
		final String imgUrl = form.get("url")[0];
		final double cost = Double.parseDouble(form.get("cost")[0]);
		final double rrp = Double.parseDouble(form.get("rrp")[0]);
		final int stock = Integer.parseInt(form.get("stock")[0]);
		
		final List<CategoryModel> categories = new ArrayList<CategoryModel>();
		if (form.get("categories") != null)
		{
			for (int i = 0; i < form.get("categories").length; i++)
			{
				categories.add(controllers.Category.getCategory(Integer.parseInt(form
						.get("categories")[i])));
			}
		}
		
		final ProductModel product = new ProductModel(name, description, cost, rrp, categories, 0,
				stock);
		product.setImgUrl(imgUrl);
		
		JPA.em().persist(product);
		
		final int id = product.getId();
		return ok();
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorLoggedIn.class)
	public static Result placeOrderApp(String email)
	{
		final UserModel user = controllers.User.getUser(email);
		final DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		final Date date = new Date();
		
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
			
			return ok();
		} else
		{
			return ok();
		}
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result showAllOrders()
	{
		List<OrderModel> orders = Order.getAllOrders();
		return ok(Json.toJson(orders));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result showUser(String email)
	{
		return ok(Json.toJson(User.getUser(email)));
	}
}
