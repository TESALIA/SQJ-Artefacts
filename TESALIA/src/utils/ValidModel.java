package utils;

import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.ChocoReasoner.attributed.questions.ChocoValidQuestion;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class ValidModel {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		AttributedReader reader = new AttributedReader();
		VariabilityModel afm = reader.parseFile("input/models/random/100-5-0-0.afm");
		ChocoReasoner reasoner = new ChocoReasoner();
		afm.transformTo(reasoner);
		ChocoValidQuestion att= new ChocoValidQuestion();
		reasoner.ask(att);
		System.out.println(att.isValid());
	}

}
