package fitnessfunctions;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import utils.shareGenerator.FeatureShare;
import utils.shareGenerator.TableReader;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.Product;

public class MarketShareFitness implements Fitness {

	Collection<FeatureShare> share;

	public MarketShareFitness(String path) {
		share = TableReader.getShareFromFile(path);
	}

	@Override
	public int getValue(Product p) {
		Collection<String> productString = getProductString(p);

		for (FeatureShare s : share) {
			if (cA(s.getFeatures(),productString)) {
				return s.getShare();
			}
		}
		throw new IllegalStateException(productString.toString());
	}

	@Override
	public int getValue(Collection<Product> products) {
		int res = 0;
		for (Product p : products) {
			res += getValue(p);
		}
		return res;
	}

	@Override
	public int getValue(Collection<Product> products, Product p) {
		return getValue(products) + getValue(p);
	}

	private Collection<String> getProductString(Product p) {
		Set<String> res = new HashSet<String>();
		for (GenericFeature f : p.getFeatures()) {
			res.add(f.getName());
		}
		return res;
	}
	
	public boolean cA(Collection<String> a, Collection<String> b){
		int n=1;
		for(String sa:a){
			for(String sb:b){
				if(sa.equals(sb)){
					n++;
				}
			}
		}
		return n==a.size();
	}

}
