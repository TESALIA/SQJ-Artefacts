package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeature;

public class ShareReader {
	private Map<Collection<String>, Integer> configurationShare;

	private BufferedReader reader;

	public ShareReader(String pathToMarketShareFileth) {
		try {
			this.reader = new BufferedReader(new FileReader(
					pathToMarketShareFileth));
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(
					"Imposible to open the market share file input.");
		}
		this.configurationShare = new HashMap<Collection<String>, Integer>();
		parseFile();
	}

	public void parseFile() {

		try {
			while (reader.ready()) {
				String line = reader.readLine();

				int i=0;
				Collection<String> stringCollection= new ArrayList<String>();
				int marketShare=0;
				
				StringTokenizer tokenizer=new StringTokenizer(line,";");
				while(tokenizer.hasMoreElements()){
					String element = (String) tokenizer.nextElement();
					if(i<4){
						stringCollection.add(element);
					}else{
						marketShare = Integer.parseInt(element);
					}
					i++;
				}
				this.configurationShare.put(stringCollection, marketShare);
				
			}
		} catch (IOException e) {
			throw new IllegalStateException("Error reading the market share file");
		}

	}
	
	public Map<Collection<AttributedFeature>,Integer> getCollectionAsFeatures(){
		if(configurationShare.size()==0){
			throw new IllegalStateException("Something went wrong when reading the file or the file is empty");	
		}
		Map<Collection<AttributedFeature>,Integer> res = new HashMap<Collection<AttributedFeature>, Integer>();
		Iterator<Entry<Collection<String>, Integer>> iterator = configurationShare.entrySet().iterator();
		while(iterator.hasNext()){
				Entry<Collection<String>, Integer> next = iterator.next();
				Collection<AttributedFeature> collection = new ArrayList<AttributedFeature>();
				for(String featureName:next.getKey()){
					collection.add(new AttributedFeature(featureName));
				}
				res.put(collection, next.getValue());
		}
		return res;
	}
	
	public Map<Collection<String>,Integer> getCollection(){
		if(configurationShare.size()==0){
			throw new IllegalStateException("Something went wrong when reading the file or the file is empty");	
		}

		return configurationShare;
	}
	
	public double getShare(Collection<GenericFeature> collection){
		if(configurationShare.size()==0){
			throw new IllegalStateException("Something went wrong when reading the file or the file is empty");	
		}
		
		double res=0f;
		int numberOfCoincidences=0;
		
		Set<Entry<Collection<AttributedFeature>,Integer>> entrySet = getCollectionAsFeatures().entrySet();
		for(Entry<Collection<AttributedFeature>,Integer> entry :entrySet){
		
			if(collection.containsAll(entry.getKey())){
				res=entry.getValue();
				numberOfCoincidences++;
			}
		}
		if(numberOfCoincidences>1){
			throw new IllegalStateException("Something went wrong when looking for the share");
		}
		
		return res;
	} 

}
