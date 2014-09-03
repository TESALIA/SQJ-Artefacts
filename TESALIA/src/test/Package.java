package test;

import java.util.Collection;

import operation.ChocoPackaging;
import utils.BussinesProduct;
import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class Package {

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		
		AttributedReader reader = new AttributedReader();
		VariabilityModel parseFile = reader.parseFile("./input/models/smallModel.afm");
		
		ChocoReasoner reasoner = new ChocoReasoner();
		parseFile.transformTo(reasoner);
		
		ChocoPackaging packaging = new ChocoPackaging();
		packaging.setMaxCost(2); // prunnning
		packaging.setTotalMaxCost(6); // total max cost
		reasoner.ask(packaging);
		
		System.out.println(packaging.getProducts());
		System.out.println("Total profit obtained is "+packaging.getProfit());

	}

}
