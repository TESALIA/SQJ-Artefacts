package utils;

import choco.cp.solver.constraints.global.geost.dataStructures.LinkedList;

public class CollectionCostCount extends LinkedList {
	private double value;
	private float cost;
	public CollectionCostCount(double value, float cost) {
		super();
		this.value = value;
		this.cost = cost;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public float getCost() {
		return cost;
	}
	public void setCost(float cost) {
		this.cost = cost;
	}

}
