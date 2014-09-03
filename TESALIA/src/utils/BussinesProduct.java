package utils;

import es.us.isa.FAMA.models.featureModel.Product;

public class BussinesProduct extends Product implements Comparable {

	private int  cost;
	private int value;

	protected int inKnapsack = 0; // the pieces of item in solution

	public String toString(){
		return super.toString()+cost+";"+value;
		
	}
	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void setInKnapsack(int _inKnapsack) {
		inKnapsack = Math.max(_inKnapsack, 0);
	}
	
	public int getInKnapsack(){
		return inKnapsack;
	}

	public void checkMembers() {
		setCost(cost);
		setValue(value);
		setInKnapsack(inKnapsack);
	}

	public int compareTo(Object item) {
		int result = 0;
		BussinesProduct i2 = (BussinesProduct) item;
		double rate1 = value / cost;
		double rate2 = i2.value / i2.cost;
		if (rate1 > rate2)
			result = -1; // if greater, put it previously
		else if (rate1 < rate2)
			result = 1;
		return result;
	}

}
