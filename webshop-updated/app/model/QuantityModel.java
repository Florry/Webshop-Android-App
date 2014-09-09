package model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class QuantityModel
{
	@Id
	@GeneratedValue
	private int id;
	private int quantity;
	
	public QuantityModel()
	{}
	
	public QuantityModel(int quantity)
	{
		this.quantity = quantity;
	}
	
	public QuantityModel(int id, int quantity)
	{
		this.id = id;
		this.quantity = quantity;
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other == this)
		{
			return true;
		}
		
		if (other instanceof QuantityModel)
		{
			final QuantityModel otherQuantity = (QuantityModel) other;
			return (this.quantity == otherQuantity.getQuantity());
		}
		
		return false;
	}
	
	public int getId()
	{
		return this.id;
	}
	
	public int getQuantity()
	{
		return this.quantity;
	}
	
	public void setQuantity(int quantity)
	{
		this.quantity = quantity;
	}
}
