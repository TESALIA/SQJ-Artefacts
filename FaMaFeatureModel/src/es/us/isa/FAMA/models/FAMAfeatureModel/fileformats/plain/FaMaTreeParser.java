// $ANTLR : "TreeParser.g" -> "FaMaTreeParser.java"$

	package es.us.isa.FAMA.models.FAMAfeatureModel.fileformats.plain;	
	import java.util.*;	
	import es.us.isa.FAMA.Exceptions.*;
	import es.us.isa.FAMA.models.FAMAfeatureModel.*;
	import es.us.isa.FAMA.models.featureModel.*;
	import es.us.isa.FAMA.models.featureModel.extended.*;
	import es.us.isa.util.*;
	import es.us.isa.FAMA.models.domain.*;

import antlr.TreeParser;
import antlr.Token;
import antlr.collections.AST;
import antlr.RecognitionException;
import antlr.ANTLRException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.collections.impl.BitSet;
import antlr.ASTPair;
import antlr.collections.impl.ASTArray;


public class FaMaTreeParser extends antlr.TreeParser       implements FaMaTreeParserTokenTypes
 {

	//Map<String,AST> mapASTFeatures = null;
	
	Map<String,Feature> features = new HashMap<String,Feature>();
	//zona de funciones	
	
	Collection<String> errors = new LinkedList<String>();
	
	//Creamos una feature con su nombre y dominio
	public Feature createFeature(AST name, Domain d){
		String n = name.getText();
		if (features.get(n) != null){
			//ya existe la feauture, asi que esta duplicada
			errors.add("Duplicated feature detected: "+n);	
		}
		Feature f = new Feature(name.getText());
		f.setDomain(d);
		features.put(name.getText(),f);
		return f;
	}
	
	public Relation createRelation(AST relName, AST card, Collection<Feature> children){
		//TODO en teoria debe funcionar
		Relation res = new Relation(relName.getText());
		Cardinality c = getCardinality(card);
		res.addCardinality(c);
		Iterator<Feature> it = children.iterator();
		while (it.hasNext()){
			Feature f = it.next();
			res.addDestination(f);	
		}
		return res;	
	}
	
	//aadimos a una feature todas sus relaciones
	public void addRelations(Feature f, Collection<Relation> rels){
		Iterator<Relation> it = rels.iterator();
		while (it.hasNext()){
			Relation r = it.next();
			f.addRelation(r);	
			//al aadir la relacion, a esta se le pone como feature padre la feature actual
		}	
	}
	
	public Cardinality getCardinality(AST t){
		String aux = t.getFirstChild().getText();
		int min = Integer.parseInt(aux);
		aux = t.getFirstChild().getNextSibling().getText();
		int max = Integer.parseInt(aux);
		Cardinality res = new Cardinality(min,max);
		return res;	
	}
	
	public TreeParserResult createFeatureModel(Feature root, Collection<Dependency> cons){
		TreeParserResult res;
		FAMAFeatureModel fm = new FAMAFeatureModel(root);
		Iterator<Dependency> it = cons.iterator();
		//TODO
		//aadir metodos a feature model para poder aadir todas las dependencias
		//del tiron, y lo mismo para las relaciones y las features
		while (it.hasNext()){
			Dependency d = it.next();
			fm.addDependency(d);	
		}
		res = new TreeParserResult(fm,errors);
		return res;
	}
	
	public ExcludesDependency createExcludes(AST relName,AST f1, AST f2){
		//TODO
		Feature feat1 = features.get(f1.getText());
		Feature feat2 = features.get(f2.getText());
		ExcludesDependency res = new ExcludesDependency(relName.getText(),feat1,feat2);
		return res;	
	}
	
	public RequiresDependency createRequires(AST relName,AST f1, AST f2){
		//TODO
		Feature feat1 = features.get(f1.getText());
		Feature feat2 = features.get(f2.getText());
		RequiresDependency res = new RequiresDependency(relName.getText(),feat1,feat2);
		return res;	
	}
	
	public Dependency ASTtoDependency(AST t, AST name){
		//TODO checkear que funciona bien
		String n = name.getText();
		//Tree<String> tree = fmp.astToTree(t);
		Dependency res;
		if (t.getType() == EXCLUDES){
			AST f1 = t.getFirstChild();
			AST f2 = t.getFirstChild().getNextSibling();
			res = createExcludes(name,f1,f2);
		}
		else{
			AST f1 = t.getFirstChild();
			AST f2 = t.getFirstChild().getNextSibling();
			res = createRequires(name,f1,f2);
		}
		return res;	
	}
	
	public Domain createEnumeratedDomain(Collection<Object> c){
		Domain d;
		Iterator<Object> it = c.iterator();
		if (it.hasNext()){
			Object aux = it.next();
			if (aux instanceof Integer){
				SetIntegerDomain auxDomain = new SetIntegerDomain();
				d = new SetIntegerDomain();
				Integer i = (Integer)aux;
				auxDomain.addValue(i);
				while (it.hasNext()){
					aux = it.next();
					if (aux instanceof Integer){
						i = (Integer)aux;
						auxDomain.addValue(i);
					}
					else{
						throw new FAMAException("Different types on the attribute domain");	
					}
				}
				d = auxDomain;
			}
			else{
				ObjectDomain auxDomain = new ObjectDomain();
				//d = new ObjectDomain();
				auxDomain.addValue(aux);
				while (it.hasNext()){
					aux = it.next();
					auxDomain.addValue(aux);
				}
				d = auxDomain;
			}
		}
		else{
			d = new SetIntegerDomain();
		}
		return d;
	}
	
	public Range createRange(AST min, AST max){
		int minimo = Integer.parseInt(min.getText());
		int maximo = Integer.parseInt(max.getText());
		Range res = new Range (minimo,maximo);
		return res;	
	}
	
	public Integer astToInteger(AST t){
		Integer res = new Integer(t.getText());
		return res;	
	}
	
	public Float astToFloat(AST t){
		Float res = new Float(t.getText());
		return res;	
	}
	
public FaMaTreeParser() {
	tokenNames = _tokenNames;
}

	public final TreeParserResult  entrada(AST _t) throws RecognitionException {
		TreeParserResult res = null;;
		
		AST entrada_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Feature root; Collection<Dependency> cons = new LinkedList<Dependency>();
		
		try {      // for error handling
			AST __t526 = _t;
			AST tmp1_AST_in = (AST)_t;
			match(_t,FEATURE_MODEL);
			_t = _t.getFirstChild();
			root=seccion_rels(_t);
			_t = _retTree;
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case CONSTRAINTS:
			{
				cons=seccion_cons(_t);
				_t = _retTree;
				break;
			}
			case 3:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t526;
			_t = _t.getNextSibling();
			res = createFeatureModel(root,cons);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return res;
	}
	
	public final Feature  seccion_rels(AST _t) throws RecognitionException {
		Feature root = null;;
		
		AST seccion_rels_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t529 = _t;
			AST tmp2_AST_in = (AST)_t;
			match(_t,SECCION_RELACIONES);
			_t = _t.getFirstChild();
			root=feature(_t);
			_t = _retTree;
			_t = __t529;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return root;
	}
	
	public final Collection<Dependency>  seccion_cons(AST _t) throws RecognitionException {
		Collection<Dependency> res = new LinkedList<Dependency>();;
		
		AST seccion_cons_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Dependency aux;
		
		try {      // for error handling
			AST __t582 = _t;
			AST tmp3_AST_in = (AST)_t;
			match(_t,CONSTRAINTS);
			_t = _t.getFirstChild();
			{
			_loop584:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==CONSTRAINT)) {
					aux=constraint(_t);
					_t = _retTree;
					res.add(aux);
				}
				else {
					break _loop584;
				}
				
			} while (true);
			}
			_t = __t582;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return res;
	}
	
	public final Feature  feature(AST _t) throws RecognitionException {
		Feature feat = null;;
		
		AST feature_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST f = null;
		Collection<Relation> rels;Collection<Dependency> invs;Domain d;
		
		try {      // for error handling
			AST __t531 = _t;
			AST tmp4_AST_in = (AST)_t;
			match(_t,FEATURE);
			_t = _t.getFirstChild();
			f = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			d=dom(_t);
			_t = _retTree;
			feat = createFeature(f,d);
			rels=relaciones(_t);
			_t = _retTree;
			addRelations(feat,rels);
			_t = __t531;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return feat;
	}
	
	public final RangeIntegerDomain  dom(AST _t) throws RecognitionException {
		RangeIntegerDomain d = new RangeIntegerDomain();;
		
		AST dom_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST min = null;
		AST max = null;
		Range r;
		
		try {      // for error handling
			AST __t533 = _t;
			AST tmp5_AST_in = (AST)_t;
			match(_t,DOMINIO);
			_t = _t.getFirstChild();
			min = (AST)_t;
			match(_t,LIT_ENTERO);
			_t = _t.getNextSibling();
			max = (AST)_t;
			match(_t,LIT_ENTERO);
			_t = _t.getNextSibling();
			r = createRange(min,max);
				d.addRange(r);
			_t = __t533;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return d;
	}
	
	public final Collection<Relation>  relaciones(AST _t) throws RecognitionException {
		Collection<Relation> rels = new LinkedList<Relation>();;
		
		AST relaciones_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Relation aux;
		
		try {      // for error handling
			AST __t559 = _t;
			AST tmp6_AST_in = (AST)_t;
			match(_t,RELACIONES);
			_t = _t.getFirstChild();
			{
			_loop561:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==RELACION)) {
					aux=relacion(_t);
					_t = _retTree;
					rels.add(aux);
				}
				else {
					break _loop561;
				}
				
			} while (true);
			}
			_t = __t559;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return rels;
	}
	
	public final GenericAttribute  atributo(AST _t) throws RecognitionException {
		GenericAttribute att = null;;
		
		AST atributo_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST n = null;
		Domain d;Object defVal, nullVal;
		
		try {      // for error handling
			AST __t535 = _t;
			AST tmp7_AST_in = (AST)_t;
			match(_t,ATRIBUTO);
			_t = _t.getFirstChild();
			n = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			d=dominio_att(_t);
			_t = _retTree;
			defVal=default_value(_t);
			_t = _retTree;
			nullVal=null_value(_t);
			_t = _retTree;
			_t = __t535;
			_t = _t.getNextSibling();
			att = new GenericAttribute(n.getText(),d,nullVal,defVal);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return att;
	}
	
	public final Domain  dominio_att(AST _t) throws RecognitionException {
		Domain d = null;;
		
		AST dominio_att_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t537 = _t;
			AST tmp8_AST_in = (AST)_t;
			match(_t,DOMINIO);
			_t = _t.getFirstChild();
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case INTEGER:
			{
				d=dominio_rango(_t);
				_t = _retTree;
				break;
			}
			case ENUM:
			{
				d=dominio_enumerado(_t);
				_t = _retTree;
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
			_t = __t537;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return d;
	}
	
	public final Object  default_value(AST _t) throws RecognitionException {
		Object o = null;;
		
		AST default_value_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t553 = _t;
			AST tmp9_AST_in = (AST)_t;
			match(_t,DEF_VALUE);
			_t = _t.getFirstChild();
			o=valor(_t);
			_t = _retTree;
			_t = __t553;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return o;
	}
	
	public final Object  null_value(AST _t) throws RecognitionException {
		Object o = null;;
		
		AST null_value_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t555 = _t;
			AST tmp10_AST_in = (AST)_t;
			match(_t,NULL_VALUE);
			_t = _t.getFirstChild();
			o=valor(_t);
			_t = _retTree;
			_t = __t555;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return o;
	}
	
	public final Domain  dominio_rango(AST _t) throws RecognitionException {
		Domain d = null;
		
		AST dominio_rango_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Collection<Range> ranges;
		
		try {      // for error handling
			AST __t540 = _t;
			AST tmp11_AST_in = (AST)_t;
			match(_t,INTEGER);
			_t = _t.getFirstChild();
			ranges=rangos(_t);
			_t = _retTree;
			_t = __t540;
			_t = _t.getNextSibling();
			d = new RangeIntegerDomain(ranges);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return d;
	}
	
	public final Domain  dominio_enumerado(AST _t) throws RecognitionException {
		Domain d = null;;
		
		AST dominio_enumerado_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Collection<Object> c = new LinkedList<Object>();Object aux;
		
		try {      // for error handling
			AST __t548 = _t;
			AST tmp12_AST_in = (AST)_t;
			match(_t,ENUM);
			_t = _t.getFirstChild();
			AST __t549 = _t;
			AST tmp13_AST_in = (AST)_t;
			match(_t,VALORES);
			_t = _t.getFirstChild();
			{
			int _cnt551=0;
			_loop551:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==LIT_ENTERO||_t.getType()==LIT_REAL||_t.getType()==LIT_STRING)) {
					aux=valor(_t);
					_t = _retTree;
					c.add(aux);
				}
				else {
					if ( _cnt551>=1 ) { break _loop551; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt551++;
			} while (true);
			}
			_t = __t549;
			_t = _t.getNextSibling();
			_t = __t548;
			_t = _t.getNextSibling();
			d = createEnumeratedDomain(c);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return d;
	}
	
	public final Collection<Range>  rangos(AST _t) throws RecognitionException {
		Collection<Range> ranges = new HashSet<Range>();;
		
		AST rangos_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Range aux = null;
		
		try {      // for error handling
			AST __t542 = _t;
			AST tmp14_AST_in = (AST)_t;
			match(_t,RANGOS);
			_t = _t.getFirstChild();
			{
			int _cnt544=0;
			_loop544:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==RANGO)) {
					aux=rango(_t);
					_t = _retTree;
					ranges.add(aux);
				}
				else {
					if ( _cnt544>=1 ) { break _loop544; } else {throw new NoViableAltException(_t);}
				}
				
				_cnt544++;
			} while (true);
			}
			_t = __t542;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return ranges;
	}
	
	public final Range  rango(AST _t) throws RecognitionException {
		Range r = null;;
		
		AST rango_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST min = null;
		AST max = null;
		
		try {      // for error handling
			AST __t546 = _t;
			AST tmp15_AST_in = (AST)_t;
			match(_t,RANGO);
			_t = _t.getFirstChild();
			min = (AST)_t;
			match(_t,LIT_ENTERO);
			_t = _t.getNextSibling();
			max = (AST)_t;
			match(_t,LIT_ENTERO);
			_t = _t.getNextSibling();
			_t = __t546;
			_t = _t.getNextSibling();
			r = createRange(min,max);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return r;
	}
	
	public final Object  valor(AST _t) throws RecognitionException {
		Object o = null;;
		
		AST valor_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST i = null;
		AST r = null;
		AST s = null;
		
		try {      // for error handling
			{
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case LIT_ENTERO:
			{
				i = (AST)_t;
				match(_t,LIT_ENTERO);
				_t = _t.getNextSibling();
				o = astToInteger(i);
				break;
			}
			case LIT_REAL:
			{
				r = (AST)_t;
				match(_t,LIT_REAL);
				_t = _t.getNextSibling();
				o = astToFloat(r);
				break;
			}
			case LIT_STRING:
			{
				s = (AST)_t;
				match(_t,LIT_STRING);
				_t = _t.getNextSibling();
				o = s.getText();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return o;
	}
	
	public final Relation  relacion(AST _t) throws RecognitionException {
		Relation r = null;;
		
		AST relacion_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST n = null;
		AST c = null;
		Collection<Feature> children;
		
		try {      // for error handling
			AST __t563 = _t;
			AST tmp16_AST_in = (AST)_t;
			match(_t,RELACION);
			_t = _t.getFirstChild();
			n = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			c = _t==ASTNULL ? null : (AST)_t;
			card(_t);
			_t = _retTree;
			children=features(_t);
			_t = _retTree;
			_t = __t563;
			_t = _t.getNextSibling();
			r = createRelation(n,c,children);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return r;
	}
	
	public final void card(AST _t) throws RecognitionException {
		
		AST card_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t580 = _t;
			AST tmp17_AST_in = (AST)_t;
			match(_t,CARDINALIDAD);
			_t = _t.getFirstChild();
			AST tmp18_AST_in = (AST)_t;
			match(_t,LIT_ENTERO);
			_t = _t.getNextSibling();
			AST tmp19_AST_in = (AST)_t;
			match(_t,LIT_ENTERO);
			_t = _t.getNextSibling();
			_t = __t580;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final Collection<Feature>  features(AST _t) throws RecognitionException {
		Collection<Feature> feats = new LinkedList<Feature>();;
		
		AST features_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Feature aux;
		
		try {      // for error handling
			AST __t576 = _t;
			AST tmp20_AST_in = (AST)_t;
			match(_t,FEATURES);
			_t = _t.getFirstChild();
			{
			_loop578:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==FEATURE)) {
					aux=feature(_t);
					_t = _retTree;
					feats.add(aux);
				}
				else {
					break _loop578;
				}
				
			} while (true);
			}
			_t = __t576;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return feats;
	}
	
	public final Collection<Dependency>  invariantes(AST _t) throws RecognitionException {
		Collection<Dependency> invs = new LinkedList<Dependency>();;
		
		AST invariantes_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		Dependency aux;
		
		try {      // for error handling
			AST __t565 = _t;
			AST tmp21_AST_in = (AST)_t;
			match(_t,INVARIANTES);
			_t = _t.getFirstChild();
			{
			_loop567:
			do {
				if (_t==null) _t=ASTNULL;
				if ((_t.getType()==CONSTRAINT)) {
					aux=constraint(_t);
					_t = _retTree;
					invs.add(aux);
				}
				else {
					break _loop567;
				}
				
			} while (true);
			}
			_t = __t565;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return invs;
	}
	
	public final Dependency  constraint(AST _t) throws RecognitionException {
		Dependency c = null;;
		
		AST constraint_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		AST n = null;
		AST e = null;
		
		try {      // for error handling
			AST __t569 = _t;
			AST tmp22_AST_in = (AST)_t;
			match(_t,CONSTRAINT);
			_t = _t.getFirstChild();
			n = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			e = _t==ASTNULL ? null : (AST)_t;
			expresion(_t);
			_t = _retTree;
			c = ASTtoDependency(e,n);
			_t = __t569;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
		return c;
	}
	
	public final void expresion(AST _t) throws RecognitionException {
		
		AST expresion_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			if (_t==null) _t=ASTNULL;
			switch ( _t.getType()) {
			case EXCLUDES:
			{
				AST __t571 = _t;
				AST tmp23_AST_in = (AST)_t;
				match(_t,EXCLUDES);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t571;
				_t = _t.getNextSibling();
				break;
			}
			case REQUIRES:
			{
				AST __t572 = _t;
				AST tmp24_AST_in = (AST)_t;
				match(_t,REQUIRES);
				_t = _t.getFirstChild();
				expresion(_t);
				_t = _retTree;
				expresion(_t);
				_t = _retTree;
				_t = __t572;
				_t = _t.getNextSibling();
				break;
			}
			case IDENT:
			{
				AST tmp25_AST_in = (AST)_t;
				match(_t,IDENT);
				_t = _t.getNextSibling();
				break;
			}
			default:
			{
				throw new NoViableAltException(_t);
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	public final void id_att(AST _t) throws RecognitionException {
		
		AST id_att_AST_in = (_t == ASTNULL) ? null : (AST)_t;
		
		try {      // for error handling
			AST __t574 = _t;
			AST tmp26_AST_in = (AST)_t;
			match(_t,ATRIBUTO);
			_t = _t.getFirstChild();
			AST tmp27_AST_in = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			AST tmp28_AST_in = (AST)_t;
			match(_t,IDENT);
			_t = _t.getNextSibling();
			_t = __t574;
			_t = _t.getNextSibling();
		}
		catch (RecognitionException ex) {
			reportError(ex);
			if (_t!=null) {_t = _t.getNextSibling();}
		}
		_retTree = _t;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"FEATURE_MODEL",
		"FEATURE",
		"FEATURES",
		"CONSTRAINTS",
		"CONSTRAINT",
		"DOMINIO",
		"DEF_VALUE",
		"NULL_VALUE",
		"RELACION",
		"CARDINALIDAD",
		"RELACIONES",
		"RANGO",
		"LITERAL",
		"RANGOS",
		"VALORES",
		"ENUM",
		"MENOS_UNARIO",
		"SECCION_RELACIONES",
		"DOSPUNTOS",
		"PyC",
		"IDENT",
		"CORCHETE_ABRIR",
		"CORCHETE_CERRAR",
		"LIT_ENTERO",
		"COMA",
		"LLAVE_ABRIR",
		"LLAVE_CERRAR",
		"SECCION_CONSTRAINTS",
		"EXCLUDES",
		"REQUIRES",
		"ATRIBUTO",
		"INTEGER",
		"LIT_REAL",
		"LIT_STRING",
		"INVARIANTES"
	};
	
	}
	
