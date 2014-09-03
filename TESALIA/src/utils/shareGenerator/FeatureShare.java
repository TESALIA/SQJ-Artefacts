package utils.shareGenerator;

import java.util.HashSet;
import java.util.Set;

public class FeatureShare {
	
	private Set<String> features;
	int share;//0-100.0
	
	public FeatureShare(String[] feats, int share){
		features = new HashSet<String>();
		for(String str:feats){
			features.add(str);
		}
		if(share>1000000||share<0){throw new IllegalStateException();}
		this.share=share;
	}
	
	public FeatureShare(Set<String> feats, int share){
		features = feats;
		if(share>1000000||share<0){throw new IllegalStateException();}
		this.share=share;
	}
	public FeatureShare mult(FeatureShare fs){

		Set<String> resStr = new HashSet<String>();
		resStr.addAll(this.features);
		resStr.addAll(fs.features);
		
		int round = Math.round((this.share/1000000f)*(fs.share/1000000f)*1000000f);
		return new FeatureShare(resStr,round);
	}
	public String toString(){
		String res="";
		for(String feat:features){
			res+=feat+";";
		}
		res+=share;
		return res;
	}
	public Set<String> getFeatures(){
		return features;
	}
	public int getShare(){return share;}
	
}
