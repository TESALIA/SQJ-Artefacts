package utils.shareGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import utils.FileWriter;

public class ShareGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String[] filesFilter={"screen.txt","version.txt","comunications.txt","CPU.txt","input.txt"};
		List<Collection<FeatureShare>> shares = new ArrayList<Collection<FeatureShare>>();
		File dir = new File("./input/marketshare/inputs");
		File[] listFiles = dir.listFiles();
		for(File f : listFiles){
			if(!f.getName().startsWith(".")&&Arrays.asList(filesFilter).contains(f.getName())){
				shares.add(TableReader.getShareFromFile(f.getAbsolutePath()));
			}
		}
		Collection<FeatureShare> feature_base = new ArrayList<FeatureShare>();
		for(int i=0;i<shares.size();i++){
			if(i==0){
				feature_base=shares.get(i);
			}else{
				Collection<FeatureShare> collection = shares.get(i);
				feature_base=multEvery(feature_base,collection);
			}
		}
		FileWriter writer = new FileWriter("./input/marketshare/globalShare.csv");
		for(FeatureShare s:feature_base){
			writer.write(s.toString());
			System.out.println(s);
		}
		writer.close();
	}

	private static Collection<FeatureShare> multEvery(Collection<FeatureShare> feature_base,
			Collection<FeatureShare> collection) {
		Collection<FeatureShare> res = new ArrayList<FeatureShare>();
		for(FeatureShare orig:feature_base){
			for(FeatureShare desr:collection){
				res.add(orig.mult(desr));
			}
		}
		return res;
	}

}
