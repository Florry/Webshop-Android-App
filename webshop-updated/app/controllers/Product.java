package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import model.CategoryModel;
import model.ProductModel;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.product.addProduct;
import views.html.product.deleteProduct;
import views.html.product.editProduct;
import views.html.product.showAllProducts;
import views.html.product.showLowStockProducts;
import views.html.product.showOneProduct;

public class Product extends Controller
{
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result addProduct()
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
		return redirect(routes.Product.showProductById(id));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result addProductForm()
	{
		return ok(addProduct.render(controllers.Category.getAllCategories()));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result deleteProduct()
	{
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final int id = Integer.parseInt(form.get("id")[0]);
		
		final ProductModel product = getProduct(id);
		
		product.setInStore(false);
		return redirect(routes.Product.deleteProductform());
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result deleteProductform()
	{
		return ok(deleteProduct.render(getAllProducts()));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result editProduct()
	{
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final String name = form.get("name")[0];
		final String description = form.get("description")[0];
		final String imgUrl = form.get("url")[0];
		final double cost = Double.parseDouble(form.get("cost")[0]);
		final double rrp = Double.parseDouble(form.get("rrp")[0]);
		final int stock = Integer.parseInt(form.get("stock")[0]);
		final int id = Integer.parseInt(form.get("id")[0]);
		
		final List<CategoryModel> categories = new ArrayList<CategoryModel>();
		if (form.get("categories") != null)
		{
			for (int i = 0; i < form.get("categories").length; i++)
			{
				categories.add(controllers.Category.getCategory(Integer.parseInt(form
						.get("categories")[i])));
			}
		}
		
		final ProductModel product = getProduct(id);
		product.setName(name);
		product.setDescription(description);
		product.setCost(cost);
		product.setRrp(rrp);
		product.setStock(stock);
		product.setCategories(categories);
		product.setImgUrl(imgUrl);
		
		return redirect(routes.Product.editProductForm(id));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result editProductForm(int id)
	{
		final ProductModel product = getProduct(id);
		return ok(editProduct.render(product, controllers.Category.getAllCategories()));
	}
	
	@Transactional
	public static Result showAllProducts()
	{
		final List<ProductModel> list = getAllProducts();
		return ok(showAllProducts.render(list));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result showLowStock(int lowestStock)
	{
		final TypedQuery<ProductModel> productsQuery = JPA.em().createQuery(
				"SELECT c FROM ProductModel c WHERE c.stock < :lowStock", ProductModel.class);
		productsQuery.setParameter("lowStock", lowestStock);
		final List<ProductModel> products = productsQuery.getResultList();
		return ok(showLowStockProducts.render(products));
	}
	
	@Transactional
	public static Result showProductById(int id)
	{
		return ok(showOneProduct.render(getProduct(id)));
	}
	
	@Transactional
	public static List<ProductModel> getAllProducts()
	{
		return JPA.em().createQuery("SELECT a FROM ProductModel a", ProductModel.class)
				.getResultList();
	}
	
	@Transactional
	public static ProductModel getProduct(int id)
	{
		return JPA.em().find(ProductModel.class, id);
	}
}
