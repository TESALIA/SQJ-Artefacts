package utils.shareGenerator;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class TableReader {

	public static Collection<FeatureShare> getShareFromFile(String pathToMarketShareFileth){
		BufferedReader reader;
		try {
			reader = new BufferedReader(new FileReader(pathToMarketShareFileth));
		} catch (FileNotFoundException e) {
			throw new IllegalStateException(
					"Imposible to open the market share file input.");
		}
		
		Collection<FeatureShare> res = new ArrayList<FeatureShare>();
		try {
			while (reader.ready()) {
				String line = reader.readLine();
				String[] split = line.split(";");
				
				res.add(new FeatureShare(Arrays.copyOfRange(split, 0, split.length-1), Integer.parseInt(split[split.length-1])));
			}
		} catch (IOException e) {
			throw new IllegalStateException("Error reading the market share file");
		}

		return res;
	}
}
