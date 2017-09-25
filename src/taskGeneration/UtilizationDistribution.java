/**
 * 
 */
package taskGeneration;

/**
 * @author kiran
 *
 */
public enum UtilizationDistribution {
	 EXPONENTIAL_DISTRIBUTION(-1),
	 BIMODAL_DISTRIBUTION (1);
	
	private int value;
	
	private UtilizationDistribution(int value)
	{
		this.value = value;
	}

}
