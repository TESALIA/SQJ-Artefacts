package operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import utils.BussinesProduct;
import choco.Choco;
import choco.cp.solver.CPSolver;
import choco.cp.solver.search.integer.valiterator.DecreasingDomain;
import choco.cp.solver.search.integer.varselector.MinDomain;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerExpressionVariable;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.ContradictionException;
import choco.kernel.solver.Solver;
import choco.kernel.solver.variables.integer.IntDomainVar;
import es.us.isa.ChocoReasoner.ChocoResult;
import es.us.isa.ChocoReasoner.attributed.ChocoQuestion;
import es.us.isa.ChocoReasoner.attributed.ChocoReasoner;
import es.us.isa.FAMA.Benchmarking.PerformanceResult;
import es.us.isa.FAMA.Reasoner.Reasoner;
import es.us.isa.FAMA.models.FAMAAttributedfeatureModel.AttributedFeature;
import es.us.isa.FAMA.models.featureModel.GenericFeature;
import es.us.isa.FAMA.models.featureModel.extended.GenericAttribute;
import fitnessfunctions.Fitness;

public class ChocoPriorizitation extends ChocoQuestion {

	public boolean useInCSPCostConstraint = true;

	private double profit;
	public Set<BussinesProduct> products;
	long time=0;
	long propagationTime=0;
	public boolean onlySolver=false;

	//Fitness fitness;

	public double getProfit() {
		return profit;
	}

	public ChocoPriorizitation() {

		this.products = new HashSet<BussinesProduct>();
		
	}

	public PerformanceResult answer(Reasoner r) {
		ChocoReasoner reasoner = (ChocoReasoner) r;

		ChocoResult res = new ChocoResult();
		Solver sol = new CPSolver();
		Model p = reasoner.getProblem();
		Map<String, IntegerVariable> atributesVar = reasoner.getAttributesVariables();

		// primero cramos la coleccion con los atributos que nos interesan
		// dependiendo de la cadena de entrada

		Collection<IntegerVariable> selectedAtts = new ArrayList<IntegerVariable>();
		Iterator<Entry<String, IntegerVariable>> atributesIt = atributesVar
				.entrySet().iterator();
		//Get the maximum value of in each product
		
		int maxval=0;
		while (atributesIt.hasNext()) {
			Entry<String, IntegerVariable> entry = atributesIt.next();
			
			if (entry.getKey().contains("." + "value")) {
				selectedAtts.add(entry.getValue());
				maxval+=entry.getValue().getUppB();
			}
		}
		
		
		
		// Ahora necesitamos crear una variable suma de todos los atributos
		// anteriores"
		IntegerVariable[] reifieds = new IntegerVariable[selectedAtts.size()];

		IntegerVariable sumaValue = Choco.makeIntVar("_sumaV", 0,
				maxval);// note we are using the sum functionto sum
										// value. but a different one can be used
		IntegerExpressionVariable sumatorio = Choco.sum(selectedAtts
				.toArray(reifieds));
		Constraint sumReifieds = Choco.eq(sumaValue, sumatorio);

		p.addConstraint(sumReifieds);
		// wont allow products that exceed the maximum cost allowed
		// Constraint lessThan = Choco.leq(sumaValue, costMax);
		// p.addConstraint(lessThan);
		sol.read(p);
		sol.setVarIntSelector(new MinDomain(sol, sol.getVar(new IntegerVariable[]{sumaValue})));
		sol.setValIntIterator(new DecreasingDomain());
	
		boolean fail=false;
		try {
			long propStart = System.currentTimeMillis();
			sol.propagate();
			propagationTime=System.currentTimeMillis()-propStart;
		} catch (ContradictionException e1) {
			System.err.println("No solutions with this cost found");
			fail=true;//we can measure the time to check this
		}
		if(fail){
			return res;
		}

		// Obtener todo los valores que tengan ese valor
		time=System.currentTimeMillis();
		if (sol.solve() == Boolean.TRUE && sol.isFeasible()) {
			
			do {
			if(onlySolver){
				BussinesProduct prod = new BussinesProduct();
				for (int i = 0; i < p.getNbIntVars(); i++) {
					IntDomainVar aux = sol.getVar(p.getIntVar(i));
					if (aux.getVal() > 0) {
						AttributedFeature f = (AttributedFeature) getFeature(
								aux, reasoner);
						
						if (f != null) {
							prod.addFeature(f);
						}
					}
				}
				prod.setValue(sol.getVar(sumaValue).getVal());
				// we do not use the cost in priorizitation but both can be used
				// at the same time
				products.add(prod);
			}
			} while (sol.nextSolution() == Boolean.TRUE);
		}
		time=System.currentTimeMillis()-time;

		//System.out.println(products.size());

		return res;

	}

	private GenericFeature getFeature(IntDomainVar aux, ChocoReasoner reasoner) {
		String temp = new String(aux.toString().substring(0,
				aux.toString().indexOf(":")));
		GenericFeature f = reasoner.searchFeatureByName(temp);
		return f;
	}

	public Collection<BussinesProduct> getProducts() {
		return products;
	}

	public long getTime() {
		return time;
	}
	public long getPropTime(){
		return propagationTime;
	}
}
