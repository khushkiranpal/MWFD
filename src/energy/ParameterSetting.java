package energy;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.commons.math3.distribution.NormalDistribution;

import taskGeneration.ITask;

public class ParameterSetting {
	 DecimalFormat twoDecimals = new DecimalFormat("#.##");
	public void set_freq(ArrayList<ITask> taskset,double frequency)
	{
		for (ITask t: taskset)
		{
			t.setFrequency(frequency);
		//	t.setWcet( Double.valueOf(twoDecimals.format((double)t.getWcet()/frequency)));

			t.setWcet( Double.valueOf(twoDecimals.format((double)t.getWCET_orginal()/frequency)));
	//		System.out.println("wcet  "+t.getWcet());
			t.setBCET(t.getBest_CET()/frequency);
		//	System.out.println("bcet   "+t.getBCET());

			t.setACET(t.getAverage_CET()/frequency);
			/*t.setWCET_orginal(t.getC()*1000);
			t.setPeriod(t.getT()*1000);
			t.setDeadline(t.getD()*1000);
			*/
			
			//t.setBCET(t.getBest_CET()*1000);
			//t.setACET(t.getAverage_CET()*1000);
		}
	}
	
	public void setBCET(ArrayList<ITask> taskset, double ratio)
	{
		for (ITask t: taskset)
		{
		//	t.setBCET( Double.valueOf(twoDecimals.format(t.getC()*ratio)));
			t.setBCET( Double.valueOf(twoDecimals.format(t.getWCET_orginal()*ratio)));
		//	System.out.println("bcet   "+t.getBCET());
			
			t.setBest_CET(t.getBCET());
		//	t.setBCET(t.getBest_CET()*1000);
		//	t.setBCET(t.getBest_CET());

		}
	}
	
	public void setACET(ArrayList<ITask> taskset)
	{
		double mean , variance , standardDev, acet;
	//	NormalDistribution normal = 	new NormalDistribution();
		for (ITask t: taskset)
		{
			mean = (t.getWCET_orginal()+t.getBCET())/2;
			variance = (t.getWCET_orginal()-t.getBCET())/6;
			standardDev = Math.sqrt(variance);
			NormalDistribution normal = 	new NormalDistribution(mean, standardDev);
			acet = normal.sample();
			t.setACET( Double.valueOf(twoDecimals.format(acet)));
			t.setAverage_CET(t.getACET());
		//	t.setACET(t.getAverage_CET());
		}
		
	}
	
	public void setParameterDouble(ArrayList<ITask> taskset)
	{
	
		for (ITask t: taskset)
		{
			t.setWcet(t.getWcet()*1000);
	//		System.out.println(" 1000 wcet  "+t.getWcet());
			t.setWCET_orginal(t.getC()*1000);
			t.setPeriod(t.getT()*1000);
			t.setDeadline(t.getD()*1000);
			t.setACET(t.getACET()*1000);
			t.setBCET(t.getBCET()*1000);
			t.setBest_CET(t.getBest_CET()*1000);
			t.setAverage_CET(t.getAverage_CET()*1000);
		}
	}
	
	/**
	 * @param taskset
	 */
	public void setResponseTime(ArrayList<ITask> taskset)
	{
		for(ITask t:taskset)
      {
	//	System.out.println("task i "+t.getId()+" wcet  "+t.getWcet());
            double w=t.getWCET_orginal(),w1=w-1;
            while(w != w1)
            {
                w1 = w;
                w =t.getWCET_orginal();
                for(int i=0; taskset.get(i) != t; i++)
                {
                	w += (int) (Math.ceil((double) w1/taskset.get(i).getPeriod())*taskset.get(i).getWCET_orginal());
      //         	 System.out.println("task j "+taskset.get(i).getId()+"response time  "+w);
                }
            }
            if( w > t.getDeadline())
             t.setResponseTime(0);
            else
            	t.setResponseTime(w);
   //      System.out.println("response time  "+w);
        }
	/*	for (ITask t : taskset)
		{
			System.out.println("task i "+t.getId()+" wcet  "+t.getWcet()+"  response  "+t.getResponseTime());
			

		}*/
	}
	
	public void setResponseTimeForMWFD(ArrayList<ITask> taskset)
	{
		for(ITask t:taskset)
      {
	//	System.out.println("task i "+t.getId()+" wcet  "+t.getWcet());
            double w=t.getWcet(),w1=w-1;
            while(w != w1)
            {
                w1 = w;
                w =t.getWcet();
                for(int i=0; taskset.get(i) != t; i++)
                {
                	w += (int) (Math.ceil((double) w1/taskset.get(i).getPeriod())*taskset.get(i).getWcet());
       //        	 System.out.println("task j "+taskset.get(i).getId()+"response time  "+w);
                }
            }
            if( w > t.getDeadline())
             t.setResponseTime(0);
            else
            	t.setResponseTime(w);
     //    System.out.println("response time  "+w);
        }
		for (ITask t : taskset)
		{
		//	System.out.println("task i "+t.getId()+" wcet  "+t.getWcet()+"  response  "+t.getResponseTime());
			

		}
	}
	public void setPromotionTime(ArrayList<ITask> taskset)
	{
		for (ITask t : taskset)
		{
		    t.setSlack(t.getDeadline()-t.getResponseTime());
		//    System.out.println("task   "+t.getId()+" res time "+t.getResponseTime()+"  promotion   "+t.getSlack());
		}
		
		}
	}
	

