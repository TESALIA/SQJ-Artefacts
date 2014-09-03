package experiments;

import java.util.Collection;
import java.util.Set;

import operation.ChocoPackaging;
import utils.BussinesProduct;
import utils.FileWriter;
import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.transformations.AttributedFeatureModelTransform;
import es.us.isa.FAMA.models.featureModel.Product;
import fitnessfunctions.MarketShareFitness;

public class MaximumMSBoundedUsingKnapSack {

	/**
	 * This class is used to execute the experiment number three. Th
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		//Specify the model path
				String modelPath="input/models/android.afm";
				//Specify the output file
				FileWriter output = new FileWriter("./out.csv");
				output.write("MaximumCost;HardwareCoverage;MaxNumberOfTest;MinNumberOfTest;Time;#products");			
				AttributedReader reader = new AttributedReader();
				FAMAAttributedFeatureModel att_model= (FAMAAttributedFeatureModel) reader.parseFile(modelPath);
				AttributedFeatureModelTransform t = new AttributedFeatureModelTransform();
				
				ChocoReasoner reasoner = new ChocoReasoner();
				t.transform(att_model, reasoner);
				for(int i=0;i<=1000; i+=10){
					ChocoPackaging q = new ChocoPackaging(new MarketShareFitness("input/marketshare/globalShare.csv"));
					q.setMaxCost(i);
					long startTime = System.currentTimeMillis();
					reasoner.ask(q);
					long endTime = System.currentTimeMillis();
					Collection<BussinesProduct> products = q.getProducts();
					System.out.println("Variability coverage for "+i);
					System.out.println(q.getProfit());
	
					//output.write(i+";"+q.getBestFitness(products)+";"+max_test+";"+min_test+";"+(endTime-startTime)+";"+q.products);
					output.write(i+";"+q.getProfit()+";"+(endTime-startTime)+";"+getMostValue(q.products).getValue());

					reasoner = new ChocoReasoner();
					t.transform(att_model, reasoner);
					
				}
				
				output.close();
				
			}
			public static BussinesProduct getMostValue(Collection<BussinesProduct> products){
				int val=Integer.MIN_VALUE;
				BussinesProduct res=new BussinesProduct();
				res.setValue(0);
				for(Product p :products){
					BussinesProduct bp=(BussinesProduct)p;
					if(bp.getValue()>val){
						val=bp.getValue();
						res=bp;
					}
				}
				return res;
			}
			
			}
