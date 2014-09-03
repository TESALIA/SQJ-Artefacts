package utils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;
/**
 * This Class will aim to delete every non final feature from a product, 
 * and then not to have any feature that don't have an associated command line option. 
 * @author malawito
 *
 */
public class ProductCleaner {

	static public Set<Product> getCleanedProduct(Collection<Product> products){
		Set<Product> res = new HashSet<Product>();
		
		//clean non concrete features
		Iterator<Product> it = products.iterator();
		while(it.hasNext()){
			Product next = it.next();
			Collection<GenericFeature> features = next.getFeatures();
			Iterator<GenericFeature> it2 = features.iterator();
			while(it2.hasNext()){
				AttributedFeature next2 = (AttributedFeature) it2.next();
				if(next2.getNumberOfRelations()!=0){
					it2.remove();
				}
			}
		}
		for(Product p : products){
			res.add(p);
		}
		return res;
	}
}
