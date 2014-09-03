package fitnessfunctions;

import java.util.Collection;

import es.us.isa.FAMA.models.featureModel.Product;

public interface Fitness {
	 public int getValue(Product p);
	 public int getValue(Collection<Product> products);
	 public int getValue(Collection<Product> products,Product p);

}
