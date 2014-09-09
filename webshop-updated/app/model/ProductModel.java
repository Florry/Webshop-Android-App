package model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import play.data.validation.Constraints.Required;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class ProductModel
{
	public static final int DEFAULT_PRODUCT_ID = -1;
	@ManyToMany
	@JsonManagedReference
	public List<CategoryModel> categories;
	private double cost;
	private String description;
	@Id
	@GeneratedValue
	private int id;
	private String imgUrl;
	private boolean inStore;
	private String name;
	
	private final int productType;
	
	private double rrp;
	private int stock;
	
	public ProductModel()
	{
		this("Unkown name", "no description", 0, 0, null, 0, 0);
	}
	
	public ProductModel(String name, String description, double cost, double rrp,
			List<CategoryModel> categories, int productType, int stock)
	{
		this.name = name;
		this.description = description;
		this.cost = cost;
		this.rrp = rrp;
		this.categories = categories;
		this.productType = productType;
		this.stock = stock;
		this.setInStore(true);
	}
	
	public ProductModel(ProductModel product)
	{
		this.name = product.name;
		this.description = product.description;
		this.cost = product.cost;
		this.rrp = product.rrp;
		this.categories = new ArrayList<CategoryModel>();
		this.productType = product.productType;
		this.stock = product.stock;
		this.setInStore(true);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
		{
			return true;
		}
		if (obj instanceof ProductModel)
		{
			final ProductModel other = (ProductModel) obj;
			
			boolean isEqual = true;
			isEqual = isEqual && other.name.equals(this.name);
			isEqual = isEqual && other.description.equals(this.description);
			isEqual = isEqual && other.cost == this.cost;
			isEqual = isEqual && other.rrp == this.rrp;
			isEqual = isEqual && other.categories.equals(this.categories);
			
			return isEqual;
			
		}
		return false;
	}
	
	public List<CategoryModel> getCategories()
	{
		return this.categories;
	}
	
	public double getCost()
	{
		return this.cost;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public String getImgUrl()
	{
		return this.imgUrl;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public int getProductType()
	{
		return this.productType;
	}
	
	public double getRrp()
	{
		return this.rrp;
	}
	
	public int getStock()
	{
		return this.stock;
	}
	
	@Override
	public int hashCode()
	{
		if (this.id != DEFAULT_PRODUCT_ID)
		{
			return 37 * this.id;
		}
		
		int hash = 37;
		hash *= this.name.hashCode();
		hash *= this.description.hashCode();
		hash += this.cost;
		hash += this.rrp;
		hash *= this.categories.hashCode();
		hash *= this.productType;
		
		return hash;
	}
	
	public boolean isInStore()
	{
		return this.inStore;
	}
	
	public void setCategories(List<CategoryModel> categories)
	{
		this.categories = categories;
	}
	
	public void setCost(double cost)
	{
		this.cost = cost;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public void setImgUrl(String imgUrl)
	{
		this.imgUrl = imgUrl;
	}
	
	public void setInStore(boolean inStore)
	{
		this.inStore = inStore;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public void setRrp(double rrp)
	{
		this.rrp = rrp;
	}
	
	public void setStock(int stock)
	{
		this.stock = stock;
	}
	
	@Override
	public String toString()
	{
		
		return "ID: " + this.id + ", Stock: " + this.stock + ", Name: " + this.name
				+ ", Description: " + this.description + ", Cost: " + this.cost + ", RRP: "
				+ this.rrp + ", Product Type; " + this.productType + ", " + this.categories;
	}
}
