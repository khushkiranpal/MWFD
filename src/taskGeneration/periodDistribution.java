/**
 * 
 */
package taskGeneration;

/**
 * @author kiran
 *
 */
public enum periodDistribution {
	
	UNIFORM_DISTRIBUTION(-1),
	 TRIMODAL_DISTRIBUTION (1);
	
	private int value;
	
	private periodDistribution(int value)
	{
		this.value = value;
	}
}
