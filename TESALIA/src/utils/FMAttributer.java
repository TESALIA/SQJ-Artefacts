package utils;

import java.io.File;
import java.io.PrintWriter;
import java.util.Random;

import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.ChocoReasoner.attributed.questions.ChocoValidQuestion;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.FAMA.models.domain.Range;
import es.us.isa.generator.FM.AbstractFMGenerator;
import es.us.isa.generator.FM.FMGenerator;
import es.us.isa.generator.FM.attributed.AttributedCharacteristic;
import es.us.isa.utils.FMWriter;

public class FMAttributer {
	static int[] extendedCTC = { 0, 2, 5 };
	static Random r = new Random();

	public static void main(String[] args) throws Exception {
		PrintWriter out = new PrintWriter("./input/models/splot/afm/seeds.txt");

		File f = new File("./input/models/splot/out/");
		File[] listFiles = f.listFiles();
		for (File file : listFiles) {
			if (!file.getName().startsWith(".")) {
				for (int extCons : extendedCTC) {
					for (int i = 0; i < 10; i++) {
						AttributedCharacteristic characteristics = new AttributedCharacteristic();
						characteristics.setNumberOfExtendedCTC(extCons);
						characteristics
								.setAttributeType(AttributedCharacteristic.INTEGER_TYPE);
						characteristics
								.setDefaultValueDistributionFunction((AttributedCharacteristic.UNIFORM_DISTRIBUTION));
						characteristics.addRange(new Range(0, 10));
						characteristics.setNumberOfAttibutesPerFeature(2);
						String argumentsDistributionFunction[] = { "0", "10" };
						characteristics
								.setDistributionFunctionArguments(argumentsDistributionFunction);
						characteristics.setHeadAttributeName("Atribute");

					//	boolean valid = false;
						long seed = 0;
						String name = "./input/models/splot/afm/"	+ file.getName().replace(".xml", "") + "-"+ extCons + "-" + i + ".afm";
					//	while (!valid) {
							seed = r.nextLong();
							characteristics.setSeed(seed);
							AbstractFMGenerator gen = new FMGenerator();
							AttributedFMGeneratorTESALIA generator = new AttributedFMGeneratorTESALIA(
									gen);
							FAMAAttributedFeatureModel afm = (FAMAAttributedFeatureModel) generator
									.generateFM(characteristics,
											file.getAbsolutePath());
							
							//write
							FMWriter writer = new FMWriter();
							writer.saveFM(afm, name);
							//read
							AttributedReader reader = new AttributedReader();
							afm = (FAMAAttributedFeatureModel) reader
									.parseFile(name);
							ChocoReasoner reasoner = new ChocoReasoner();
							afm.transformTo(reasoner);
							ChocoValidQuestion att = new ChocoValidQuestion();
							reasoner.ask(att);
							//valid = att.isValid();
					//	}
						out.print(name+";"+seed+"\r\n");
						out.flush();
					}
				}

			}
		}
		out.close();
	}

}
