package experiments;

import utils.FileWriter;
import utils.ProductCleaner;
import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.ChocoReasoner.attributed.questions.ChocoProductsQuestion;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.transformations.AttributedFeatureModelTransform;
import es.us.isa.FAMA.models.featureModel.Product;
import fitnessfunctions.MarketShareFitness;

public class MarketShareExtractor {

	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//Specify the model path
				String modelPath="input/models/android.afm";
				//Specify the output file
				FileWriter output = new FileWriter("./out.csv");
				output.write("Product,Value");

				
				AttributedReader reader = new AttributedReader();
				FAMAAttributedFeatureModel att_model= (FAMAAttributedFeatureModel) reader.parseFile(modelPath);
				AttributedFeatureModelTransform t = new AttributedFeatureModelTransform();
				
				ChocoReasoner reasoner = new ChocoReasoner();
				t.transform(att_model, reasoner);
				ChocoProductsQuestion q = new ChocoProductsQuestion();
				reasoner.ask(q);
				MarketShareFitness marketShareFitness = new MarketShareFitness("./input/marketshare/globalShare.csv");
				for(Product p : ProductCleaner.getCleanedProduct(q.getAllProducts())){
					output.write(marketShareFitness.getValue(p)+","+p.toString());
				}
				
				
				output.close();
				
			}
			
			
			}
