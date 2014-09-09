package controllers;

import java.util.List;
import java.util.Map;

import model.OrderModel;
import model.ProductInCartModel;
import model.ProductModel;
import model.QuantityModel;
import model.UserModel;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.shoppingcart.shoppingCart;
import views.html.user.addUser;
import views.html.user.deleteUser;
import views.html.user.editUser;
import views.html.user.registerUser;
import views.html.user.showAllUsers;
import views.html.user.showUser;
import views.html.user.updateProfile;

public class User extends Controller
{
	@Transactional
	@Security.Authenticated(AuthenticatorLoggedIn.class)
	public static Result addProductToCart()
	{
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final String encoded = session().get("username");
		final String email = controllers.Login.decodeEmail(encoded);
		final int id = Integer.parseInt(form.get("product")[0]);
		String app = null;
		if (form.get("app") != null)
		{
			app = form.get("app")[0];
		}
		int quantity = 0;
		
		if (form.get("quantity") != null)
		{
			final String quantityString = form.get("quantity")[0];
			if (quantityString != "")
			{
				quantity = Integer.parseInt(quantityString);
			} else
			{
				quantity = 1;
			}
		} else
		{
			quantity = 1;
		}
		if (quantity <= 0)
		{
			quantity = 1;
		}
		
		final UserModel user = getUser(email);
		final ProductModel product = controllers.Product.getProduct(id);
		
		for (final ProductInCartModel cartProduct : user.products)
		{
			if (cartProduct.getProduct().equals(product))
			{
				final int oldQuantity = cartProduct.getQuantity().getQuantity();
				final int newQuantity = oldQuantity + quantity;
				cartProduct.setQuantity(new QuantityModel(newQuantity));
				
				if (app != null)
				{
					return ok();
				} else
				{
					return redirect(routes.User.showShoppingCart());
				}
			}
		}
		user.addProductToCart(product, quantity);
		if (app != null)
		{
			return ok();
		} else
		{
			return redirect(routes.User.showShoppingCart());
		}
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result addProductToCartByEmail(String email)
	{
		System.out.println("addProductToCartByEmail called!");
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final int id = Integer.parseInt(form.get("product")[0]);
		int quantity;
		if (form.get("quantity") != null)
		{
			final String quantityString = form.get("quantity")[0];
			if (quantityString != "")
			{
				quantity = Integer.parseInt(quantityString);
			} else
			{
				quantity = 1;
			}
		} else
		{
			quantity = 1;
		}
		if (quantity <= 0)
		{
			quantity = 1;
		}
		
		final UserModel user = getUser(email);
		final ProductModel product = controllers.Product.getProduct(id);
		
		for (final ProductInCartModel cartProduct : user.products)
		{
			if (cartProduct.getProduct().equals(product))
			{
				final int oldQuantity = cartProduct.getQuantity().getQuantity();
				final int newQuantity = oldQuantity + quantity;
				cartProduct.setQuantity(new QuantityModel(newQuantity));
				return redirect(routes.User.showShoppingCartByEmail(email));
				
			}
		}
		user.addProductToCart(product, quantity);
		return redirect(routes.User.showShoppingCartByEmail(user.getEmail()));
		
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorLoggedIn.class)
	public static Result addQuantity(String email)
	{
		System.out.println("Add quantity");
		final UserModel currUser = getUser(controllers.Login.decodeEmail(session().get("username")));
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final int id = Integer.parseInt(form.get("id")[0]);
		String app = null;
		if (form.get("app") != null)
		{
			app = form.get("app")[0];
		}
		final UserModel user = getUser(email);
		final ProductModel product = controllers.Product.getProduct(id);
		
		user.addQuantity(product);
		if (app == null)
		{
			if (currUser.getRights() > 0)
			{
				return redirect(routes.User.showShoppingCartByEmail(email));
			} else
			{
				return redirect(routes.User.showShoppingCart());
			}
		} else
		{
			return ok();
		}
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result addUser()
	{
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final String email = form.get("email")[0];
		if (email != "")
		{
			final String password = form.get("password")[0];
			final String firstname = form.get("firstname")[0];
			final String lastname = form.get("lastname")[0];
			final String dob = form.get("dob")[0];
			final String telephone = form.get("telephone")[0];
			final String address1 = form.get("address1")[0];
			final String address2 = form.get("address2")[0];
			final String town = form.get("town")[0];
			final String postcode = form.get("postcode")[0];
			final int rights = Integer.parseInt(form.get("rights")[0]);
			
			final UserModel newUser = new UserModel(email, password, firstname, lastname, dob,
					telephone, address1, address2, town, postcode);
			newUser.setRights(rights);
			
			JPA.em().persist(newUser);
			
			return redirect(routes.User.showUser(email));
		} else
		{
			flash().put("email-null", "yes");
			return redirect(routes.User.addUserForm());
		}
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result addUserForm()
	{
		return ok(addUser.render());
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result deleteUser()
	{
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final String email = form.get("email")[0];
		
		final String currentUser = controllers.Login.decodeEmail(session().get("username"));
		
		final UserModel user1 = getUser(email);
		if (!(user1.getEmail().equals(currentUser)))
		{
			for (final OrderModel order : user1.getOrders())
			{
				for (int i = 0; i < order.products.size(); i++)
				{
					order.products.remove(i);
				}
				order.products.clear();
				JPA.em().remove(order);
			}
			flash().put("user-deleted", user1.getEmail());
			JPA.em().remove(user1);
		} else
		{
			flash().put("delete-yourself", "yes");
		}
		return redirect(routes.User.deleteUserForm());
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result deleteUserForm()
	{
		return ok(deleteUser.render(getAllUsers()));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result editUser()
	{
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final String email = form.get("email")[0];
		final UserModel user1 = getUser(email);
		
		String newPassword = "";
		if (form.get("password") != null)
		{
			newPassword = form.get("password")[0];
		}
		String password = "";
		if (newPassword == "")
		{
			password = user1.getPassword();
		} else
		{
			password = new String(newPassword);
		}
		
		final String firstname = form.get("firstname")[0];
		final String lastname = form.get("lastname")[0];
		final String dob = form.get("dob")[0];
		final String telephone = form.get("telephone")[0];
		final String address1 = form.get("address1")[0];
		final String address2 = form.get("address2")[0];
		final String town = form.get("town")[0];
		final String postcode = form.get("postcode")[0];
		final int rights = Integer.parseInt(form.get("rights")[0]);
		
		user1.setFirstname(firstname);
		user1.setLastname(lastname);
		user1.setDob(dob);
		user1.setTelephone(telephone);
		user1.setAddress1(address1);
		user1.setAddress2(address2);
		user1.setTown(town);
		user1.setPostcode(postcode);
		user1.setPassword(password);
		user1.setRights(rights);
		
		flash().put("user-updated", "yes");
		
		return redirect(routes.User.editUserForm(email));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result editUserForm(String email)
	{
		final UserModel user = getUser(email);
		return ok(editUser.render(user));
	}
	
	@Transactional
	public static Result registerUser()
	{
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final String email = form.get("email")[0];
		final String password = form.get("password")[0];
		final String firstname = form.get("firstname")[0];
		final String lastname = form.get("lastname")[0];
		final String dob = form.get("dob")[0];
		final String telephone = form.get("telephone")[0];
		final String address1 = form.get("address1")[0];
		final String address2 = form.get("address2")[0];
		final String town = form.get("town")[0];
		final String postcode = form.get("postcode")[0];
		
		UserModel knownUser = controllers.User.getUser(email);
		if (knownUser == null)
		{
			if (email != "")
			{
				final UserModel newUser = new UserModel(email, password, firstname, lastname, dob,
						telephone, address1, address2, town, postcode);
				newUser.setRights(0);
				
				JPA.em().persist(newUser);
				
				final String encoded = controllers.Login.encodeEmail(newUser.getEmail());
				final String encodedRights = controllers.Login.encodeEmail("0");
				
				session().put("username", encoded);
				session().put("rights", encodedRights);
				
				return redirect(routes.User.showProfile());
			} else
			{
				flash().put("email-null", "yes");
				return redirect(routes.User.registerUserForm());
			}
		} else
		{
			flash().put("email-in-use", "yes");
			return redirect(routes.User.registerUserForm());
		}
	}
	
	@Transactional
	public static Result registerUserForm()
	{
		return ok(registerUser.render());
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorLoggedIn.class)
	public static Result removeProductFromCart(String email)
	{
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final int id = Integer.parseInt(form.get("id")[0]);
		String app = null;
		if (form.get("app") != null)
		{
			app = form.get("app")[0];
		}
		
		final UserModel user = getUser(email);
		final ProductModel product = controllers.Product.getProduct(id);
		
		user.removeProductFromCart(product);
		
		if (app == null)
		{
			if (user.getRights() > 0)
			{
				return redirect(routes.User.showShoppingCartByEmail(email));
			} else
			{
				return redirect(routes.User.showShoppingCart());
			}
		} else
		{
			return ok();
		}
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorLoggedIn.class)
	public static Result removeQuantity(String email)
	{
		final UserModel currUser = getUser(controllers.Login.decodeEmail(session().get("username")));
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final int id = Integer.parseInt(form.get("id")[0]);
		String app = null;
		if (form.get("app") != null)
		{
			app = form.get("app")[0];
		}
		
		final UserModel user = getUser(email);
		final ProductModel product = controllers.Product.getProduct(id);
		
		user.removeQuantity(product);
		
		if (app == null)
		{
			if (currUser.getRights() > 0)
			{
				return redirect(routes.User.showShoppingCartByEmail(email));
			} else
			{
				return redirect(routes.User.showShoppingCart());
			}
		} else
		{
			return ok();
		}
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result showAllUsers()
	{
		return ok(showAllUsers.render(getAllUsers()));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorLoggedIn.class)
	public static Result showProfile()
	{
		final String encodedUser = session().get("username");
		final String email = controllers.Login.decodeEmail(encodedUser);
		return ok(showUser.render(getUser(email)));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorLoggedIn.class)
	public static Result showShoppingCart()
	{
		System.out.println("hello");
		final String encodedUser = session().get("username");
		final String email = controllers.Login.decodeEmail(encodedUser);
		final UserModel user1 = getUser(email);
		return ok(shoppingCart.render(user1, controllers.Product.getAllProducts()));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result showShoppingCartByEmail(String email)
	{
		final UserModel user1 = getUser(email);
		return ok(shoppingCart.render(user1, controllers.Product.getAllProducts()));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result showUser(String email)
	{
		return ok(showUser.render(getUser(email)));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorLoggedIn.class)
	public static Result updateProfile()
	{
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final String email = form.get("email")[0];
		final UserModel user1 = getUser(email);
		
		if (form.get("oldPassword") != null)
		{
			final String oldPassword = form.get("oldPassword")[0];
			if (oldPassword.equals(user1.getPassword()))
			{
				String newPassword = "";
				if (form.get("password") != null)
				{
					newPassword = form.get("password")[0];
				}
				String password = "";
				if (newPassword == "")
				{
					password = user1.getPassword();
				} else
				{
					password = new String(newPassword);
				}
				
				final String firstname = form.get("firstname")[0];
				final String lastname = form.get("lastname")[0];
				final String dob = form.get("dob")[0];
				final String telephone = form.get("telephone")[0];
				final String address1 = form.get("address1")[0];
				final String address2 = form.get("address2")[0];
				final String town = form.get("town")[0];
				final String postcode = form.get("postcode")[0];
				
				user1.setFirstname(firstname);
				user1.setLastname(lastname);
				user1.setDob(dob);
				user1.setTelephone(telephone);
				user1.setAddress1(address1);
				user1.setAddress2(address2);
				user1.setTown(town);
				user1.setPostcode(postcode);
				user1.setPassword(password);
				
				flash().put("user-updated", "yes");
				
				return redirect(routes.User.updateProfileForm());
			}
		}
		flash().put("password-wrong", "yes");
		return redirect(routes.User.updateProfileForm());
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorLoggedIn.class)
	public static Result updateProfileForm()
	{
		final String encodedUser = session().get("username");
		final String email = controllers.Login.decodeEmail(encodedUser);
		final UserModel user = getUser(email);
		return ok(updateProfile.render(user));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorLoggedIn.class)
	public static List<UserModel> getAllUsers()
	{
		return JPA.em().createQuery("SELECT a FROM UserModel a", UserModel.class).getResultList();
	}
	
	@Security.Authenticated(AuthenticatorLoggedIn.class)
	@Transactional
	public static UserModel getUser(String email)
	{
		return JPA.em().find(UserModel.class, email);
	}
	
}