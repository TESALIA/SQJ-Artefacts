package experiments;

import java.io.PrintWriter;

import operation.ChocoPackaging;
import operation.ChocoPriorizitation;
import operation.ChocoPrunning;
import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.ChocoReasoner.attributed.questions.ChocoValidQuestion;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class Validity {

	public static void main(String[] args) throws Exception {

		int[] features = { 10, 20, 30, 50, 100, 200, 300, 400, 500 };
		int[] ctc = { 5, 10, 15 };
		int[] extendedCTC = { 0, 2, 5 };

		for (int feat : features) {
			for (int cons : ctc) {
				for (int extCons : extendedCTC) {
					for (int i = 0; i < 10; i++) {
						String name = feat + "-" + cons + "-" + extCons + "-"
								+ i;
						AttributedReader reader = new AttributedReader();
						VariabilityModel model = reader
								.parseFile("./input/models/random/" + name
										+ ".afm");

						ChocoReasoner reasoner = new ChocoReasoner();
						model.transformTo(reasoner);

						// Do the prunning
						ChocoValidQuestion valid = new ChocoValidQuestion();
						reasoner.ask(valid);
						if (!valid.isValid()) {
							System.out.println(name);
						}
					}
				}
			}
		}
	}

}
