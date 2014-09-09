package model;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public final class UserModel
{
	private String address1;
	private String address2;
	private String dob;
	@Id
	public String email;
	private String firstname;
	private String lastname;
	@OneToMany(mappedBy = "user")
	public List<OrderModel> orders;
	private String password;
	private String postcode;
	@OneToMany(cascade =
	{ CascadeType.ALL })
	public List<ProductInCartModel> products;
	private int rights;
	
	private String telephone;
	
	private String town;
	
	public UserModel()
	{
		this("email", "password", "firstname", "lastname", "dob", "telephone", "address1",
				"address2", "town", "postcode");
	}
	
	public UserModel(String email, String password, String firstname, String lastname, String dob,
			String telephone, String address1, String address2, String town, String postcode)
	{
		this.email = email.toLowerCase();
		this.password = password;
		this.firstname = firstname;
		this.lastname = lastname;
		this.dob = dob;
		this.telephone = telephone;
		this.address1 = address1;
		this.address2 = address2;
		this.town = town;
		this.postcode = postcode;
		this.rights = 0;
	}
	
	public void addProductToCart(ProductModel product, int quantity)
	{
		final QuantityModel quantityModel = new QuantityModel(quantity);
		final ProductInCartModel productInCart = new ProductInCartModel();
		productInCart.setProduct(product);
		productInCart.setQuantity(quantityModel);
		this.products.add(productInCart);
	}
	
	public void addQuantity(ProductModel product)
	{
		for (final ProductInCartModel productInCart : this.products)
		{
			if (productInCart.getProduct().equals(product))
			{
				final int oldQuantity = productInCart.getQuantity().getQuantity();
				final int newQuantity = oldQuantity + 1;
				final QuantityModel quantityModel = new QuantityModel(newQuantity);
				productInCart.setQuantity(quantityModel);
			}
		}
	}
	
	public void emptyShoppingCart()
	{
		this.products.clear();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if (other == this)
		{
			return true;
		}
		
		if (other instanceof UserModel)
		{
			final UserModel otherUser = (UserModel) other;
			final boolean isSameClass = this.getClass().equals(otherUser.getClass());
			
			return (this.email.equals(otherUser.email)) && isSameClass;
		}
		
		return false;
	}
	
	public String getAddress1()
	{
		return this.address1;
	}
	
	public String getAddress2()
	{
		return this.address2;
	}
	
	public String getDob()
	{
		return this.dob;
	}
	
	public String getEmail()
	{
		return this.email;
	}
	
	public String getFirstname()
	{
		return this.firstname;
	}
	
	public String getLastname()
	{
		return this.lastname;
	}
	
	public List<OrderModel> getOrders()
	{
		return this.orders;
	}
	
	public String getPassword()
	{
		return this.password;
	}
	
	public String getPostcode()
	{
		return this.postcode;
	}
	
	public int getRights()
	{
		return this.rights;
	}
	
	public List<ProductInCartModel> getShoppingCartContents()
	{
		return this.products;
	}
	
	public String getTelephone()
	{
		return this.telephone;
	}
	
	public int getTotalShoppingCartPrice()
	{
		int totalPrice = 0;
		for (final ProductInCartModel product : this.products)
		{
			totalPrice += product.getQuantity().getQuantity() * product.getProduct().getCost();
		}
		return totalPrice;
	}
	
	public String getTown()
	{
		return this.town;
	}
	
	@Override
	public int hashCode()
	{
		int result = 1;
		result += 37 * this.email.hashCode();
		result += 37 * this.getClass().hashCode();
		
		return result;
	}
	
	public void removeProductFromCart(ProductModel product)
	{
		for (int i = 0; i < this.products.size(); i++)
		{
			if (this.products.get(i).getProduct().equals(product))
			{
				this.products.remove(i);
				break;
			}
		}
	}
	
	public void removeQuantity(ProductModel product)
	{
		for (final ProductInCartModel productInCart : this.products)
		{
			if (productInCart.getProduct().equals(product))
			{
				final int oldQuantity = productInCart.getQuantity().getQuantity();
				if (oldQuantity - 1 > 0)
				{
					final int newQuantity = oldQuantity - 1;
					final QuantityModel quantityModel = new QuantityModel(newQuantity);
					productInCart.setQuantity(quantityModel);
				}
			}
		}
	}
	
	public void setAddress1(String address1)
	{
		this.address1 = address1;
	}
	
	public void setAddress2(String address2)
	{
		this.address2 = address2;
	}
	
	public void setDob(String dob)
	{
		this.dob = dob;
	}
	
	public void setFirstname(String firstname)
	{
		this.firstname = firstname;
	}
	
	public void setLastname(String lastname)
	{
		this.lastname = lastname;
	}
	
	public void setPassword(String password)
	{
		this.password = password;
	}
	
	public void setPostcode(String postcode)
	{
		this.postcode = postcode;
	}
	
	public void setProducts(List<ProductInCartModel> products)
	{
		this.products = products;
	}
	
	public void setRights(int rights)
	{
		this.rights = rights;
	}
	
	public void setTelephone(String telephone)
	{
		this.telephone = telephone;
	}
	
	public void setTown(String town)
	{
		this.town = town;
	}
	
	@Override
	public String toString()
	{
		return String
				.format("User: %s Firstname: %s Lastname: %s Dob: %s \nTelephone: %s \nAddress: %s %s %s %s ",
						this.getEmail(), this.getFirstname(), this.getLastname(), this.getDob(),
						this.getTelephone(), this.getAddress1(), this.getAddress2(),
						this.getTown(), this.getPostcode());
	}
	
}
