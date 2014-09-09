package model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class OrderModel
{
	private final String date;
	
	@Id
	@GeneratedValue
	private int id;
	@OneToMany(cascade =
	{ CascadeType.ALL })
	public List<ProductInCartModel> products;
	
	private int totalPrice;
	
	private final String user;
	
	public OrderModel()
	{
		this("no@email.com", new LinkedHashMap<ProductModel, QuantityModel>(), "1970-01-01");
		for (final ProductInCartModel product : this.products)
		{
			this.totalPrice += product.getQuantity().getQuantity() * product.getProduct().getCost();
		}
	}
	
	public OrderModel(String user, List<ProductInCartModel> products, String date)
	{
		this.date = date;
		this.user = user;
		this.products = new ArrayList<ProductInCartModel>(products);
		for (final ProductInCartModel product : products)
		{
			this.totalPrice += product.getQuantity().getQuantity() * product.getProduct().getCost();
		}
	}
	
	public OrderModel(String user, Map<ProductModel, QuantityModel> products, String date)
	{
		this.date = date;
		this.user = user;
		this.products = new ArrayList<ProductInCartModel>();
		
		for (final ProductModel product : products.keySet())
		{
			final ProductInCartModel productInOrder = new ProductInCartModel();
			productInOrder.setProduct(product);
			productInOrder.setQuantity(products.get(product));
			this.products.add(productInOrder);
			this.totalPrice += products.get(product).getQuantity() * product.getCost();
		}
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other == this)
		{
			return true;
		}
		
		if (other instanceof OrderModel)
		{
			final OrderModel otherUser = (OrderModel) other;
			final boolean isSameClass = this.getClass().equals(otherUser.getClass());
			
			return (this.user.equals(otherUser.getUser())) && this.id == otherUser.getId()
					&& isSameClass;
		}
		
		return false;
	}
	
	public List<ProductInCartModel> getContents()
	{
		return this.products;
	}
	
	public LinkedHashMap<ProductModel, Integer> getContentsMap()
	{
		final LinkedHashMap<ProductModel, Integer> map = new LinkedHashMap<ProductModel, Integer>();
		for (final ProductInCartModel product : this.products)
		{
			map.put(product.getProduct(), product.getQuantity().getQuantity());
		}
		return map;
	}
	
	public String getDate()
	{
		return this.date;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public int getTotalPrice()
	{
		return this.totalPrice;
	}
	
	public String getUser()
	{
		return this.user;
	}
	
	@Override
	public int hashCode()
	{
		int result = 1;
		result += 37 * this.user.hashCode();
		result += 37 * this.id;
		result += 37 * this.getClass().hashCode();
		
		return result;
	}
	
	public void setContents(List<ProductInCartModel> products)
	{
		this.products = products;
	}
	
	@Override
	public String toString()
	{
		return "Order " + this.getId() + ": " + this.getUser() + " - " + this.getDate() + " - "
				+ this.getContents();
	}
}
