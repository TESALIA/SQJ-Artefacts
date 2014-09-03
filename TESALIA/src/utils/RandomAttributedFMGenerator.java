/**
  * 	This file is part of Betty.
 *
 *     Betty is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     Betty is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with Betty.  If not, see <http://www.gnu.org/licenses/>.
 */
package utils;

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
import es.us.isa.generator.FM.attributed.AttributedFMGenerator;
import es.us.isa.utils.BettyException;
import es.us.isa.utils.FMWriter;

/* This example shows how to generate an attributed feature model and save it in textual format. The number of features and percentage of cross-tree constraints
 * are given as input parameters.
 */
public class RandomAttributedFMGenerator {

	public static void main(String[] args) throws Exception, BettyException {
		PrintWriter out = new PrintWriter("./input/models/random/seeds.txt");
		int[] features = {1000,2000,5000};
	//	int[] features = {100,200,300,400,500};

		int[] ctc = {5,10,15};
		int[] extendedCTC = {0,2,5};
		Random r= new Random();
		for(int feat:features){
			for(int cons:ctc){
				for(int extCons:extendedCTC){
					for(int i=0;i<10;i++){
						String name=feat+"-"+cons+"-"+extCons+"-"+i+".afm";
						AttributedCharacteristic characteristics = new AttributedCharacteristic();
						characteristics.setNumberOfFeatures(feat); // Number of features
						characteristics.setPercentageCTC(cons); // Percentage of cross-tree
																// constraints.
						characteristics.setNumberOfExtendedCTC(extCons);
						characteristics.setAttributeType(AttributedCharacteristic.INTEGER_TYPE);
						characteristics
								.setDefaultValueDistributionFunction((AttributedCharacteristic.UNIFORM_DISTRIBUTION));
						characteristics.addRange(new Range(0, 10));
						characteristics.setNumberOfAttibutesPerFeature(2);
						String argumentsDistributionFunction[] = { "0", "10" };
						characteristics
								.setDistributionFunctionArguments(argumentsDistributionFunction);
						characteristics.setHeadAttributeName("Atribute");

						// STEP 2: Generate the model with the specific characteristics (FaMa
						// Attributed FM metamodel is used)
						boolean valid=false;
						long seed=0;
						//int not=0;
						
						while(!valid){
							seed=r.nextLong();
							characteristics.setSeed(seed);
							AbstractFMGenerator gen = new FMGenerator();
							AttributedFMGeneratorTESALIA generator = new AttributedFMGeneratorTESALIA(gen);
							FAMAAttributedFeatureModel afm = (FAMAAttributedFeatureModel) generator.generateFM(characteristics);
							
							// STEP 3: Save the model
							FMWriter writer = new FMWriter();
							writer.saveFM(afm, "./input/models/random/"+name);
							
							AttributedReader reader = new AttributedReader();
							
							afm=(FAMAAttributedFeatureModel) reader.parseFile("./input/models/random/"+name);
							ChocoReasoner reasoner = new ChocoReasoner();
							afm.transformTo(reasoner);
							ChocoValidQuestion att= new ChocoValidQuestion();
							reasoner.ask(att);
							valid=att.isValid();
							//not++;
							//System.out.println(not);
							
						}
						out.print(name+";"+seed+"\r\n");
						out.flush();
						
						
		
						
					}
				}
			}
		}
		out.close();

	}
}
