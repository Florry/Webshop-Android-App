package model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public final class CategoryModel
{
	public static final int DEFAULT_ID = -1;
	@Id
	@GeneratedValue
	private int id;
	private final String name;
	@ManyToMany(mappedBy = "categories", cascade =
	{ CascadeType.ALL })
	@JsonBackReference
	public List<ProductModel> products;
	
	private final int staff_responsible;
	
	public CategoryModel()
	{
		this("Unkown category", -1);
	}
	
	public CategoryModel(int id, CategoryModel other)
	{
		this(other.name, other.staff_responsible);
	}
	
	public CategoryModel(String name, int staff_responsible)
	{
		this.name = name;
		this.staff_responsible = staff_responsible;
	}
	
	public CategoryModel(CategoryModel category)
	{
		this.name = category.name;
		this.staff_responsible = category.staff_responsible;
		this.products = new ArrayList<ProductModel>();
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj instanceof CategoryModel)
		{
			final CategoryModel other = (CategoryModel) obj;
			return this.id == other.id;
		}
		return false;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public String getName()
	{
		return this.name;
	}
	
	public int getStaff_responsible()
	{
		return this.staff_responsible;
	}
	
	@Override
	public int hashCode()
	{
		return 31 * this.id;
	}
	
	public void setProducts(List<ProductModel> products)
	{
		this.products = products;
	}
	
	@Override
	public String toString()
	{
		return String.format("Id: %s, Name: %s, Straff_responsible: %s", this.id, this.name,
				this.staff_responsible);
	}
	
}
