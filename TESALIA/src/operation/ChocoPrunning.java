package operation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

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

public class ChocoPrunning extends ChocoQuestion {

	public boolean useInCSPCostConstraint = true;
	public boolean onlySolver=false;
	private int costMax;
	public Collection<BussinesProduct> products;
	
	long time=0;
	long propagationTime=0;

	public ChocoPrunning() {
		this.products = new ArrayList<BussinesProduct>();
	}

	public PerformanceResult answer(Reasoner r) {
		ChocoReasoner reasoner = (ChocoReasoner) r;

		ChocoResult res = new ChocoResult();
		Solver sol = new CPSolver();
		Model p = reasoner.getProblem();
		Map<String, IntegerVariable> atributesVar = reasoner
				.getAttributesVariables();

		// primero cramos la coleccion con los atributos que nos interesan
		// dependiendo de la cadena de entrada
		int maxcost=0;

		Collection<IntegerVariable> selectedAtts = new ArrayList<IntegerVariable>();
		Iterator<Entry<String, IntegerVariable>> atributesIt = atributesVar
				.entrySet().iterator();
		while (atributesIt.hasNext()) {
			Entry<String, IntegerVariable> entry = atributesIt.next();
			if (entry.getKey().contains("." + "cost")) {
				IntegerVariable value = entry.getValue();
				selectedAtts.add(value);
				maxcost+=entry.getValue().getUppB();

			
			}
		}
		// Ahora necesitamos crear una variable suma de todos los atributos
		// anteriores"

		IntegerVariable[] reifieds = new IntegerVariable[0];
		IntegerVariable sumaCost = Choco
				.makeIntVar("_sumaC", 0, 10*maxcost);//note we are using the sum functionto sum cost. but a different one can be used
		IntegerExpressionVariable sumatorio = Choco.sum(selectedAtts
				.toArray(reifieds));
		Constraint sumReifieds = Choco.eq(sumaCost, sumatorio);

		p.addConstraint(sumReifieds);
		// wont allow products that exceed the maximum cost allowed
		Constraint lessThan = Choco.leq(sumaCost, costMax);
		p.addConstraint(lessThan);
		sol.read(p);
		sol.setVarIntSelector(new MinDomain(sol, sol.getVar(new IntegerVariable[]{sumaCost})));
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
		//Start
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
						// Only get the leafs // we can decide here to return all features or only the leaf
						//if (f != null && f.getNumberOfRelations() == 0) {
						if (f != null ) {

						prod.addFeature(f);
						}
					}
				}
				prod.setCost(sol.getVar(sumaCost).getVal());
				//we do not use the valu in prunning but both can be used at the same time
				//prod.setValue(fitness.getValue(prod));
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

	public void setMaxCost(int cost) {
		this.costMax = cost;

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
