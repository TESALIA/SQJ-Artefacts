/*
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

*/
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.SPLXReader;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.XMLWriter;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;

import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.MasonTagTypes;
import net.htmlparser.jericho.MicrosoftTagTypes;
import net.htmlparser.jericho.Source;

public class SPLOTLoader {
	public static void main(String[] args) throws Exception {
		String sourceUrlString = "http://gsd.uwaterloo.ca:8088//SPLOT/SplotAnalysesServlet?action=select_model&enableSelection=false&&showModelDetails=true";
		//http://gsd.uwaterloo.ca:8088/SPLOT/models/movies_app_fm.xml
		if (args.length == 0)
			System.err.println("Using default argument of \"" + sourceUrlString
					+ '"');
		else
			sourceUrlString = args[0];
		if (sourceUrlString.indexOf(':') == -1)
			sourceUrlString = "file:" + sourceUrlString;
		MicrosoftTagTypes.register();
		MasonTagTypes.register();
		Source source = new Source(new URL(sourceUrlString));
		System.out
				.println("\n*******************************************************************************\n");

		List<Element> linkElements = source.getAllElements(HTMLElementName.A);
		for (Element linkElement : linkElements) {
			String href = linkElement.getAttributeValue("href");
			if (href == null)
				continue;
			// A element can contain other tags so need to extract the text from
			// it:
			String label = linkElement.getContent().getTextExtractor()
					.toString();
			if (!href.contains("javascript:")) {
				href = href.substring(href.indexOf("modelFile=") + 10);
				// System.out.println(href);
				procesaDescarga(href);
			}
		}

	}

	private static void procesaDescarga(String href) throws Exception {
		URL url = new URL("http://gsd.uwaterloo.ca:8088/SPLOT/models/"+href);
		
		// establecemos conexion
		URLConnection urlCon = url.openConnection();

		// Sacamos por pantalla el tipo de fichero
//		System.out.println(urlCon.getContentType());

		// Se obtiene el inputStream de la web y se abre el fichero
		// local.
		InputStream is = urlCon.getInputStream();
		File tempFile = File.createTempFile("temp", ".splx");
		FileOutputStream fos = new FileOutputStream(tempFile);
		
		

		byte[] array = new byte[4*1024]; // buffer temporal de lectura.
		int leido = is.read(array);
		while (leido > 0) {
			fos.write(array, 0, leido);
			leido = is.read(array);
		}

		// cierre de conexion y fichero.
		is.close();
		fos.close();
		
		SPLXReader reader= new SPLXReader();
		VariabilityModel parseFile = reader.parseFile(tempFile.getAbsolutePath());
		XMLWriter writer = new XMLWriter();
		writer.writeFile("./out/"+href, parseFile);
	}

}
