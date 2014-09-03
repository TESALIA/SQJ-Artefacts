package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.FAMAAttributedFeatureModel;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.Relation;
import es.us.isa.FAMA.models.FAMAfeatureModel.FAMAFeatureModel;
import es.us.isa.FAMA.models.FAMAfeatureModel.Feature;
import es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.XMLReader;
import es.us.isa.FAMA.models.domain.RangeIntegerDomain;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import es.us.isa.FAMA.models.variabilityModel.VariabilityModel;
import es.us.isa.FAMA.models.variabilityModel.parsers.WrongFormatException;
import es.us.isa.generator.Characteristics;
import es.us.isa.generator.IGenerator;
import es.us.isa.generator.FM.AbstractFMGenerator;
import es.us.isa.generator.FM.attributed.AttributedCharacteristic;
import es.us.isa.generator.FM.attributed.AttributedFMGenerator;
import es.us.isa.utils.BettyException;

public class AttributedFMGeneratorTESALIA extends AttributedFMGenerator {

	String[] names = { "value", "cost" };

	public AttributedFMGeneratorTESALIA(IGenerator gen) {
		super(gen);
	}

	public VariabilityModel generateFM(Characteristics ch, String path)
			throws WrongFormatException {
		if (!(ch instanceof AttributedCharacteristic)) {
			throw new BettyException(
					"The characteristic must be AttributedCharacteristic");
		}

		AttributedCharacteristic ac = (AttributedCharacteristic) ch;

		super.initializeDefaultValueDistribution(ac);
		super.initializeNullValueDistribution(ac);
		// Feature model generation
		XMLReader reader = new XMLReader();
		FAMAFeatureModel fm = (FAMAFeatureModel) reader.parseFile(path);
		//clean the model
		Collection<Feature> features = fm.getFeatures();
		for (Feature f : features) {
			f.setName(f.getName().replace("-", "_").replace("/", "_").replace("min", "mi_n").replace("+", "mas"));
		}
		
		FAMAAttributedFeatureModel afm = FMToAttributedFM(fm);

		// Adding attributes
		addAttributes(ac, afm.getRoot());

		addExtendedContraints(afm, ch);
		return afm;

	}

	@Override
	protected void addAttributes(AttributedCharacteristic ch,
			AttributedFeature f) {
		Iterator<Relation> it = f.getRelations();
		// cleaning dust for the parser
		while (it.hasNext()) {
			Relation r = it.next();
			Iterator<AttributedFeature> it2 = r.getDestination();
			while (it2.hasNext()) {
				AttributedFeature f2 = it2.next();
				// Check if it is a leaf, then we add the attributes
				if (f2.getNumberOfRelations() == 0) {

					for (GenericAttribute att : this.generateAttributes(ch)) {
						f2.addAttribute(att);
					}
				}
				addAttributes(ch, f2);
			}
		}
	}

	@Override
	public Collection<GenericAttribute> generateAttributes(
			AttributedCharacteristic ch) {
		Collection<GenericAttribute> attributes2return = new ArrayList<GenericAttribute>();
		for (int i = 0; i < ch.getNumberOfAttibutesPerFeature(); i++) {
			if (ch.getAttributeType() == AttributedCharacteristic.INTEGER_TYPE) {
				attributes2return.add(new GenericAttribute(names[i],
						new RangeIntegerDomain(ch.getRanges()),
						nullValueDistribution.getValue(),
						defaultValueDistribution.getValue()));
			} else {
			}
		}
		return attributes2return;
	}
}
