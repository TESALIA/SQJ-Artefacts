package utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class ZeroOneKnapsack {

	protected List<BussinesProduct> itemList = new LinkedList<BussinesProduct>();
	protected int maxWeight = 0;
	protected int solutionWeight = 0;
	protected int profit = 0;
	protected boolean calculated = false;

	public ZeroOneKnapsack() {
	}

	public ZeroOneKnapsack(int _maxWeight) {
		setMaxWeight(_maxWeight);
	}

	public ZeroOneKnapsack(List<BussinesProduct> _itemList) {
		setItemList(_itemList);
	}

	public ZeroOneKnapsack(List<BussinesProduct> _itemList, int _maxWeight) {
		setItemList(_itemList);
		setMaxWeight(_maxWeight);
	}

	// calculte the solution of 0-1 knapsack problem with dynamic method:
	public List<BussinesProduct> calcSolution() {
		int n = itemList.size();

		setInitialStateForCalculation();
		if (n > 0 && maxWeight > 0) {
			List<List<Integer>> c = new LinkedList<List<Integer>>();
			List<Integer> curr = new LinkedList<Integer>();

			c.add(curr);
			for (int j = 0; j <= maxWeight; j++)
				curr.add(0);
			for (int i = 1; i <= n; i++) {
				List<Integer> prev = curr;
				c.add(curr = new ArrayList<Integer>());
				for (int j = 0; j <= maxWeight; j++) {
					if (j > 0) {
						int wH = itemList.get(i - 1).getCost();
						curr.add((wH > j) ? prev.get(j) : Math.max(
								prev.get(j),
								itemList.get(i - 1).getValue()
										+ prev.get(j - wH)));
					} else {
						curr.add(0);
					}
				} // for (j...)
			} // for (i...)
			profit = curr.get(maxWeight);

			for (int i = n, j = maxWeight; i > 0 && j >= 0; i--) {
				int tempI = c.get(i).get(j);
				int tempI_1 = c.get(i - 1).get(j);
				if ((i == 0 && tempI > 0) || (i > 0 && tempI != tempI_1)) {
					BussinesProduct iH = itemList.get(i - 1);
					int wH = iH.getCost();
					iH.setInKnapsack(1);
					j -= wH;
					solutionWeight += wH;
				}
			} // for()
			calculated = true;
		} // if()
		Iterator<BussinesProduct> iterator = itemList.iterator();
		
		while(iterator.hasNext()){
			BussinesProduct next = iterator.next();
			if(next.inKnapsack==0){
				iterator.remove();
			}
		}
		return itemList;
		
	}

	// add an item to the item list
	public void add(BussinesProduct p) {

		itemList.add(p);
		setInitialStateForCalculation();
	}



	// remove an item from the item list
	public void remove(String name) {
		for (Iterator<BussinesProduct> it = itemList.iterator(); it.hasNext();) {
			if (name.equals(it.next().getName())) {
				it.remove();
			}
		}
		setInitialStateForCalculation();
	}

	// remove all items from the item list
	public void removeAllItems() {
		itemList.clear();
		setInitialStateForCalculation();
	}

	public int getProfit() {
		if (!calculated)
			calcSolution();
		return profit;
	}

	public int getSolutionWeight() {
		return solutionWeight;
	}

	public boolean isCalculated() {
		return calculated;
	}

	public int getMaxWeight() {
		return maxWeight;
	}

	public void setMaxWeight(int _maxWeight) {
		maxWeight = Math.max(_maxWeight, 0);
	}

	public void setItemList(List<BussinesProduct> _itemList) {
		if (_itemList != null) {
			itemList = _itemList;
			for (BussinesProduct item : _itemList) {
				item.checkMembers();
			}
		}
	}

	// set the member with name "inKnapsack" by all items:
	private void setInKnapsackByAll(int inKnapsack) {
		for (BussinesProduct item : itemList)
			if (inKnapsack > 0)
				item.setInKnapsack(1);
			else
				item.setInKnapsack(0);
	}

	// set the data members of class in the state of starting the calculation:
	protected void setInitialStateForCalculation() {
		setInKnapsackByAll(0);
		calculated = false;
		profit = 0;
		solutionWeight = 0;
	}

} // class

