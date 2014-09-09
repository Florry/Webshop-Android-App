package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.log.Log;

import model.CategoryModel;
import model.ProductModel;
import play.db.jpa.JPA;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import views.html.category.addCategory;
import views.html.category.showAllCategories;
import views.html.category.showOneCategory;

public class Category extends Controller
{
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result addCategory()
	{
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final String name = form.get("name")[0];
		System.out.print("name = " + name);
		if (name != "")
		{
			final CategoryModel category = new CategoryModel(name, 0);
			
			JPA.em().persist(category);
		}
		return redirect(routes.Category.addCategoryForm());
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result addCategoryForm()
	{
		return ok(addCategory.render(getAllCategories()));
	}
	
	@Transactional
	@Security.Authenticated(AuthenticatorAdmin.class)
	public static Result deleteCategory()
	{
		final Map<String, String[]> form = request().body().asFormUrlEncoded();
		final int id = Integer.parseInt(form.get("id")[0]);
		
		final CategoryModel category = getCategory(id);
		for (final ProductModel product : category.products)
		{
			product.categories.remove(category);
		}
		category.setProducts(null);
		
		JPA.em().remove(category);
		return redirect(routes.Category.addCategoryForm());
	}
	
	@Transactional
	public static Result showAllCategories()
	{
		return ok(showAllCategories.render(getAllCategories()));
	}
	
	@Transactional
	public static Result showCategoryById(int id)
	{
		return ok(showOneCategory.render(getCategory(id)));
	}
	
	@Transactional
	public static List<CategoryModel> getAllCategories()
	{
		return JPA.em().createQuery("SELECT a FROM CategoryModel a", CategoryModel.class)
				.getResultList();
	}
	
	@Transactional
	public static CategoryModel getCategory(int id)
	{
		return JPA.em().find(CategoryModel.class, id);
	}
}