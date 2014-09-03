package experiments;

import java.io.PrintWriter;

import operation.ChocoPackaging;
import operation.ChocoPriorizitation;
import operation.ChocoPrunning;
import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.fileformats.AttributedReader;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

public class Scalability {

	public static void main(String[] args) throws Exception {
		PrintWriter out = new PrintWriter("./output/experiments/pack.txt");
		Integer maxCostImpact = Integer.parseInt(args[0]);
		//Integer maxCostImpact = 10;
		
		int[] features = {10,20,30,50,100,200,300,400,500,1000,2000,5000};
		int[] ctc = {5,10,15};
		int[] extendedCTC = {0,2,5};
		
		for(int feat:features){
			for(int cons:ctc){
				for(int extCons:extendedCTC){
					for(int i=0;i<10;i++){
						String name=feat+"-"+cons+"-"+extCons+"-"+i;
						AttributedReader reader = new AttributedReader();
						VariabilityModel model = reader.parseFile("./input/models/random/"+name+".afm");
						
						ChocoReasoner reasoner = new ChocoReasoner();
						model.transformTo(reasoner);
						
//						//Do the prunning
//						ChocoPrunning prunning = new ChocoPrunning();
//						prunning.setMaxCost(maxCostImpact*feat);
//						prunning.onlySolver=true;
//						reasoner.ask(prunning);
//						
//						long pruningTime=prunning.getTime();
//						long prunningPropagation=prunning.getPropTime();
//						
//						//clean up mem
//						prunning=null;
//						System.gc();
//						
//						//Do the priorization
//						ChocoPriorizitation priorizitation = new ChocoPriorizitation();
//						priorizitation.onlySolver=true;
//						reasoner.ask(priorizitation);
//						
//						long prioritizationTime=priorizitation.getTime();
//						long priorizationPropagation=priorizitation.getPropTime();
//						
//						//clean up mem
//						priorizitation=null;
//						System.gc();
//						
						//Do the Packaging
						ChocoPackaging packaging = new ChocoPackaging();
						
						packaging.setMaxCost(maxCostImpact*feat); // prunnning
						packaging.setTotalMaxCost(2*maxCostImpact*feat); // total max cost
						reasoner.ask(packaging);
						long packagingTime=packaging.getTime();
						
						//clean up mem
						packaging=null;
					//	System.gc();
						
//						String output=name+";"+feat+";"+cons+";"+extCons+";"+prunningPropagation+";"+pruningTime+";"+priorizationPropagation+";"+prioritizationTime+";"+packagingTime;
//						String output=name+";"+feat+";"+cons+";"+extCons+";"+prunningPropagation+";"+pruningTime+";"+priorizationPropagation+";"+prioritizationTime;
						String output=name+";"+feat+";"+cons+";"+extCons+";"+packagingTime;

						out.write(output+"\r\n");
						System.out.println(output);
						out.flush();
					}
				}
			}
		}
		out.close();
	}

}
