package energy;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.apache.commons.math3.util.MultidimensionalCounter.Iterator;

import queue.ISortedQueue;
import queue.SortedQueuePeriod;
import taskGeneration.ITask;

public class SysClockFreq {
    public DecimalFormat twoDecimals = new DecimalFormat("#.##");  // upto 2 decimal points

	
	/**
	 * @param t_i
	 * @param taskset
	 * @param fMax
	 * @return
	 */
	public double energyMinFreq(ITask t_i, ArrayList<ITask> taskset, double fMax)
	{
		ISortedQueue queue = new SortedQueuePeriod ();
    	queue.addTasks(taskset);
    	ArrayList<ITask> sortedTaskset = new ArrayList<ITask>();
    	sortedTaskset = queue.getSortedSet();
		double slack=0, workload =0 , alpha =1;
		double  idleDuration = 0, w, tempW=0, temp=0 ,delta = 0,  time ;
		boolean busy = true;
		w= (long) (t_i.getWcet()/fMax);
		//-   System.out.println("task i  "+t_i.getId()+"  w  "+w);
		do
		{
			if(busy == true)
				
			{
				
				delta = t_i.getDeadline()-w;
				//-   System.out.println("busy   "+busy+"  delta  "+ delta);
				do
				{
					temp=0;	
					java.util.Iterator<ITask> itr = sortedTaskset.iterator();
					ITask t_j ;
					t_j=itr.next();
					//-   System.out.println("t j  "+t_j.getId()+"  ti  "+t_i.getId());
					while (t_i.getPeriod()>=t_j.getPeriod())
					{
						
						temp=temp+ (t_j.getWcet()/fMax)*((Math.floor(w/t_j.getPeriod()))+1);
						//-   System.out.println("  temp   "+temp);
						if(itr.hasNext())
							t_j=itr.next();
						else
							break;
					}
					tempW= temp + slack;
					//-   System.out.println("  slack  "+slack);
					delta = tempW-w;
					w=tempW;
					//-   System.out.println("w  "+w  + "temp w "+tempW);
					
				}while ((w<t_i.getDeadline())&&(delta>0));
				busy= false;
			}
			else
			{
				java.util.Iterator<ITask> itr1 = sortedTaskset.iterator();
				ITask t =  itr1.next();
				idleDuration =t_i.getPeriod(); 
				
				while (itr1.hasNext() && t.getPeriod()<=t_i.getPeriod())			
				{
					//-   System.out.println("Idle t  "+t.getId());
					double idle = Math.min((t.getPeriod()*Math.ceil((w/t.getPeriod())))-w,  (t_i.getDeadline()-w)); 
					if (idleDuration>idle)
						idleDuration=idle;
					
					t=itr1.next();
				}
				
				slack+= idleDuration;
				w +=idleDuration;
				time = w;
				workload = (w-slack);
				//-   System.out.println("workload  "+workload+"   time  "+time+"  alpha  "+ workload/time);
				if ((workload/time)<alpha)
					alpha= (workload/time);
				
				busy = true;
			}
		}while(w<t_i.getDeadline());
		return alpha;
	}

	public double SysClockF(ArrayList<ITask> taskset)
	{
		double freq_i, freq=0;
		
		for (ITask t : taskset)
		{
		
		//	System.out.println("Task  "+t.getId());
			t.setFrequency(energyMinFreq(t, taskset, 1));
			
			if (freq<t.getFrequency() )
				freq=t.getFrequency();
		//	System.out.println("task freq  "+ t.getFrequency());
		}
		System.out.println("   freq   "+Double.valueOf(twoDecimals.format(freq)));	
		return Double.valueOf(twoDecimals.format(freq));
	
	}
}
