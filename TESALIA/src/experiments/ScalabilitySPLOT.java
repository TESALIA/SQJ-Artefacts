package experiments;

import java.io.File;
import java.io.PrintWriter;

import operation.ChocoPackaging;
import operation.ChocoPriorizitation;
import operation.ChocoPrunning;
import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class ScalabilitySPLOT {

	public static void main(String[] args) throws Exception {
		PrintWriter out = new PrintWriter(
				"./output/experiments/splotResults.txt");
		Integer maxCostImpact = Integer.parseInt(args[0]);
		//Integer maxCostImpact = 10;


		File f = new File("./input/models/splot/afm/");
		File[] listFiles = f.listFiles();

		for (File file : listFiles) {
			if (file.getName().endsWith(".afm")) {
				System.out.println(file.getName());
				AttributedReader reader = new AttributedReader();
				VariabilityModel model = reader.parseFile(file
						.getAbsolutePath());

				int feat = model.getElements().size();

				ChocoReasoner reasoner = new ChocoReasoner();
				model.transformTo(reasoner);

				// Do the prunning
				ChocoPrunning prunning = new ChocoPrunning();
				prunning.setMaxCost(maxCostImpact * feat);
				prunning.onlySolver = true;
				reasoner.ask(prunning);

				long pruningTime = prunning.getTime();
				long prunningPropagation = prunning.getPropTime();

				// clean up mem
				prunning = null;
				System.gc();

				// Do the priorization
				ChocoPriorizitation priorizitation = new ChocoPriorizitation();
				priorizitation.onlySolver = true;
				reasoner.ask(priorizitation);

				long prioritizationTime = priorizitation.getTime();
				long priorizationPropagation = priorizitation.getPropTime();

				// clean up mem
				priorizitation = null;
				System.gc();

				// Do the Packaging
				ChocoPackaging packaging = new ChocoPackaging();

				packaging.setMaxCost(maxCostImpact * feat); // prunnning
				packaging.setTotalMaxCost(2 * maxCostImpact * feat); // total
																		// max
																		// cost
				reasoner.ask(packaging);
				long packagingTime = packaging.getTime();

				// clean up mem
				packaging = null;
				// System.gc();

				String output = file.getName() + prunningPropagation + ";"
						+ pruningTime + ";" + priorizationPropagation + ";"
						+ prioritizationTime + ";" + packagingTime;
				
				out.write(output + "\r\n");
				System.out.println(output);
				out.flush();
			}

		}
		out.close();
	}

}
