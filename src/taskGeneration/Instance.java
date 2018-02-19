/**
 * 
 */
package taskGeneration;

/**
 * @author KHUSHKIRAN PAL
 *
 */
public class Instance {
	
	private long highPriorTask;
	private long lowPriorTask;
	private int noOfInstances;
	
	public Instance(Instance i )
	{
		this.highPriorTask= i.highPriorTask;
		this.lowPriorTask= i.lowPriorTask;
		this.noOfInstances= i.noOfInstances;
	}
	
	public Instance( )
	{
		/*this.highPriorTask= 0;
		this.lowPriorTask= 0;
		this.noOfInstances= 0;*/
	}
	 
	/**
	 * @return the highPriorTask
	 */
	public long getHighPriorTask() {
		return highPriorTask;
	}
	/**
	 * @param highPriorTask the highPriorTask to set
	 */
	public void setHighPriorTask(long highPriorTask) {
		this.highPriorTask = highPriorTask;
	}
	/**
	 * @return the lowPriorTask
	 */
	public long getLowPriorTask() {
		return lowPriorTask;
	}
	/**
	 * @param lowPriorTask the lowPriorTask to set
	 */
	public void setLowPriorTask(long lowPriorTask) {
		this.lowPriorTask = lowPriorTask;
	}
	/**
	 * @return the noOfInstances
	 */
	public int getNoOfInstances() {
		return noOfInstances;
	}
	/**
	 * @param noOfInstances the noOfInstances to set
	 */
	public void setNoOfInstances(int noOfInstances) {
		this.noOfInstances = noOfInstances;
	}
	
	public Instance clone()
	{
		Instance instanc = new Instance();
		instanc.highPriorTask= this.getHighPriorTask();
		instanc.lowPriorTask= this.getLowPriorTask();
		instanc.noOfInstances = this.getNoOfInstances();
		return instanc;
	}

}
