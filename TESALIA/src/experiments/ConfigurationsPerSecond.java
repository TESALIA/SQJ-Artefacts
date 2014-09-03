package experiments;

import utils.FileWriter;
import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.ChocoReasoner.attributed.questions.ChocoProductsQuestion;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.transformations.AttributedFeatureModelTransform;

public class ConfigurationsPerSecond {

	/**
	 * This class is used to execute the experiment number one. This experiment aims to 
	 * calculate the number of obtained configurations per second when using CLASP.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		
		//Specify the model path
		String modelPath="input/models/android.afm";
		//Specify the output file
		FileWriter output = new FileWriter("./output/experiments/experiment1.csv");
		output.write("Iteration;TimeRequiredInSeconds;TotalConfigurations;ConfigurationsPerSecond;");

		float average=0;
		
		for (int i = 0; i < 10; i++) {
			
			//Read the model
			AttributedReader reader = new AttributedReader();
			FAMAAttributedFeatureModel att_model = (FAMAAttributedFeatureModel) reader.parseFile(modelPath);
			
			//Create the reasoner and translate the model into CSP
			AttributedFeatureModelTransform t = new AttributedFeatureModelTransform();
			ChocoReasoner reasoner = new ChocoReasoner();
			t.transform(att_model, reasoner);
			
			//Create the question
			ChocoProductsQuestion q = new ChocoProductsQuestion();
			
			//Ask the question and measure the time.
			long startTime = System.currentTimeMillis();
			reasoner.ask(q);
			long spendTime = System.currentTimeMillis() - startTime;
			
			//Calculate the data to write into the file
			float timeInSeconds = (float)spendTime/1000;
			long numberOfProducts = q.getNumberOfProducts();
			float configurationsPerSecond=numberOfProducts/timeInSeconds;
			
			//Write the output line for this iteration
			output.write(i+";"+timeInSeconds+";"+numberOfProducts+";"+configurationsPerSecond);
			
			//Update the average
			if(average!=0){
				average=average+configurationsPerSecond/2;
			}else{
				average=configurationsPerSecond;
			}
			
		}
		output.write("Data Average ="+average);
		output.close();
	}

}
