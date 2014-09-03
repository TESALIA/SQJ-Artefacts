package test;

import java.util.Collection;

import operation.ChocoPrunning;
import utils.BussinesProduct;
import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class Prunning {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
	
		AttributedReader reader = new AttributedReader();
		VariabilityModel parseFile = reader.parseFile("./input/models/smallModel.afm");
		
		ChocoReasoner reasoner = new ChocoReasoner();
		parseFile.transformTo(reasoner);
		
		ChocoPrunning prunning = new ChocoPrunning();
		prunning.setMaxCost(2);
		
		reasoner.ask(prunning);
		Collection<BussinesProduct> products = prunning.getProducts();
		
		for(BussinesProduct p : products){
			System.out.println(p);
		}
	}

}
