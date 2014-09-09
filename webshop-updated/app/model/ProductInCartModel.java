package model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class ProductInCartModel
{
	@Id
	@GeneratedValue
	private int id;
	
	@ManyToOne(cascade =
	{ CascadeType.ALL })
	private ProductModel product;
	@ManyToOne(cascade =
	{ CascadeType.ALL })
	private QuantityModel quantity;
	
	@Override
	public boolean equals(Object other)
	{
		if (other == this)
		{
			return true;
		}
		
		if (other instanceof ProductInCartModel)
		{
			final ProductInCartModel otherProductInCartModel = (ProductInCartModel) other;
			return (this.product.equals(otherProductInCartModel.getProduct()) && this.quantity == otherProductInCartModel
					.getQuantity());
		} else if (other instanceof ProductModel)
		{
			final ProductModel productModel = (ProductModel) other;
			return (this.product.equals(productModel));
		}
		
		return false;
	}
	
	public ProductModel getProduct()
	{
		return this.product;
	}
	
	public QuantityModel getQuantity()
	{
		return this.quantity;
	}
	
	public void setProduct(ProductModel product)
	{
		this.product = product;
	}
	
	public void setQuantity(QuantityModel quantity)
	{
		this.quantity = quantity;
	}
}
