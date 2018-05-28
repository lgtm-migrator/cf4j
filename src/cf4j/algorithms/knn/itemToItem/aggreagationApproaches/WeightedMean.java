package cf4j.algorithms.knn.itemToItem.aggreagationApproaches;

import cf4j.data.Item;
import cf4j.algorithms.TestPredictions;
import cf4j.data.DataModel;
import cf4j.data.TestItem;
import cf4j.data.TestUser;

/**
 * <p>This class computes the prediction of the test users' test items. The results are 
 * saved in double array on the hashmap of each test user with the key "predictions". This 
 * array overlaps with the test items' array of the test users. For example, the prediction
 * retrieved with the method testUser.getPredictions()[i] is the prediction of the item
 * testUser.getTestItems()[i].</p>
 * 
 * <p>This class uses weighted average as method to combine the ratings of the items more
 * similar to the active one.</p>
 * 
 * @author Fernando Ortega
 */
public class WeightedMean extends TestPredictions {
	
	/**
	 * Minimum similarity computed
	 */
	private double minSim;
	
	/**
	 * Maximum similarity computed
	 */
	private double maxSim;

	@Override
	public void beforeRun() { 
		super.beforeRun();
		
		this.maxSim = Double.MIN_VALUE;
		this.minSim = Double.MAX_VALUE;
		
		for (TestItem testItem : DataModel.gi().getTestItems()) {
			for (double m : testItem.getSimilarities()) {
				if (!Double.isInfinite(m)) {
					if (m < this.minSim) this.minSim = m;
					if (m > this.maxSim) this.maxSim = m;
				}
			}
		}
	}

	@Override
	public double predict (TestUser testUser, int itemCode) {
		
		TestItem item = DataModel.gi().getTestItemByCode(itemCode);
		
		double [] similarities = item.getSimilarities();
		int [] neighbors = item.getNeighbors();
		
		double prediction = 0;
		double sum = 0;
		
		for (int n = 0; n < neighbors.length; n++) {
			if (neighbors[n] == -1) break; // Neighbors array are filled with -1 when no more neighbors exists
			
			int itemIndex = neighbors[n];
			Item neighbor = DataModel.gi().getItems()[itemIndex];
			int neighborCode = neighbor.getItemCode();
			
							
			int i = testUser.getItemIndex(neighborCode);
			if (i != -1) {
				double similarity = similarities[itemIndex];
				double sim = (similarity - this.minSim) / (this.maxSim - this.minSim);
				
				double rating = testUser.getRatings()[i];
				
				prediction += sim * rating;
				sum += sim;
			}
		}
		
		if (sum == 0) {
			return Double.NaN;
		} else {
			prediction /= sum;
			return prediction;
		}
	}
}
