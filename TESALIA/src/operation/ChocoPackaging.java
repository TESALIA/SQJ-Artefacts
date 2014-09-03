package operation;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import utils.BussinesProduct;
import utils.ZeroOneKnapsack;
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
import es.us.isa.FAMA.models.featureModel.Product;
import fitnessfunctions.Fitness;

public class ChocoPackaging extends ChocoQuestion {

	public boolean useInCSPCostConstraint = true;

	private int costMax;
	private int totalCostMax;
	private double profit;
	public LinkedList<BussinesProduct> products;
	Collection<BussinesProduct> return_col;
	Fitness fitness;
	long time=0;
	public double getProfit() {
		return profit;
	}

	public ChocoPackaging(Fitness f) {
		this.costMax = 0;
		this.totalCostMax=0;
		this.products = new LinkedList<BussinesProduct>();
		fitness = f;
		return_col = new LinkedList<BussinesProduct>();
	}

	public ChocoPackaging() {
		this.products = new LinkedList<BussinesProduct>();
		return_col = new LinkedList<BussinesProduct>();

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

		Collection<IntegerVariable> selectedCostAtts = new LinkedList<IntegerVariable>();
		Iterator<Entry<String, IntegerVariable>> atributesIt = atributesVar
				.entrySet().iterator();
		int maxcost=0;
		while (atributesIt.hasNext()) {
			Entry<String, IntegerVariable> entry = atributesIt.next();
			if (entry.getKey().contains("." + "cost")) {
				selectedCostAtts.add(entry.getValue());
				maxcost+=entry.getValue().getUppB();
			}
		}

		Collection<IntegerVariable> selectedValueAtts = new LinkedList<IntegerVariable>();
		Iterator<Entry<String, IntegerVariable>> atributesValueIt = atributesVar
				.entrySet().iterator();
		int maxvalue=0;
		while (atributesValueIt.hasNext()) {
			Entry<String, IntegerVariable> entry = atributesValueIt.next();
			if (entry.getKey().contains("." + "value")) {
				selectedValueAtts.add(entry.getValue());
				maxvalue+=entry.getValue().getUppB();
			}
		}
		
		// placeholder variable to cost. 
		IntegerVariable[] reifieds = new IntegerVariable[selectedCostAtts.size()];

		IntegerVariable suma = Choco
				.makeIntVar("_suma", 0, maxcost);// \
		IntegerExpressionVariable sumatorio = Choco.sum(selectedCostAtts
				.toArray(reifieds));
		Constraint sumReifieds = Choco.eq(suma, sumatorio);
		p.addConstraint(sumReifieds);
		
		// placeholder variable to value. 
		IntegerVariable[] reifiedsVal = new IntegerVariable[selectedValueAtts.size()];

		IntegerVariable sumaV = Choco.makeIntVar("_sumaV", 0, maxvalue);//
		IntegerExpressionVariable sumatorioVal = Choco.sum(selectedValueAtts
				.toArray(reifiedsVal));
		Constraint sumValueReifieds = Choco.eq(sumaV, sumatorioVal);
		
		p.addConstraint(sumValueReifieds);
		
		// wont allow products that exceed the maximum cost allowed
		Constraint lessThan = Choco.leq(suma, costMax);
		p.addConstraint(lessThan);
		sol.read(p);
		sol.setVarIntSelector(new MinDomain(sol, sol.getVar(new IntegerVariable[]{sumaV})));
		sol.setValIntIterator(new DecreasingDomain());
		long start=System.currentTimeMillis();
		try {
			sol.propagate();
		} catch (ContradictionException e1) {
			System.err.println("No solutions with this cost can be find");
			return res;
		}
		// Obtener todo los valores que tengan ese valor
		if (sol.solve() == Boolean.TRUE && sol.isFeasible()) {
			do {
				BussinesProduct prod = new BussinesProduct();
				for (int i = 0; i < p.getNbIntVars(); i++) {
					IntDomainVar aux = sol.getVar(p.getIntVar(i));
					if (aux.getVal() > 0) {
						AttributedFeature f = (AttributedFeature) getFeature(
								aux, reasoner);
						// Only get the leafs
						if (f != null) {// && f.getNumberOfRelations() == 0
							prod.addFeature(f);
						}
					}
				}
				prod.setCost(sol.getVar(suma).getVal());
				if(fitness!=null){//read values from attrs or from table
					prod.setValue(fitness.getValue(prod));
				}else{
					prod.setValue(sol.getVar(sumaV).getVal());

				}
				products.add(prod);
			} while (sol.nextSolution() == Boolean.TRUE);
		}


		
		ZeroOneKnapsack knapsack = new ZeroOneKnapsack(products,	totalCostMax);
		time=System.currentTimeMillis()-start;
		return_col = knapsack.calcSolution();
		profit = knapsack.getProfit();
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
	public void setTotalMaxCost(int cost) {
		this.totalCostMax = cost;

	}
	public Collection<BussinesProduct> getProducts() {
		return return_col;
	}

	private Set<Product> get30000(Collection<Product> products2) {
		Set<Product> res = new HashSet<Product>();
		int i = 0;
		for (Iterator<Product> it = products2.iterator(); it.hasNext()
				&& i < 30000; i++) {
			res.add(it.next());
		}
		return res;
	}

	public long getTime() {
		return time;
	}
}
