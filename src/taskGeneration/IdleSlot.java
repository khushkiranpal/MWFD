/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taskGeneration;

/**
 *
 * @author KIRAN
 */
public class IdleSlot implements Cloneable{
    private int id;
	private double startTime;
    private double endTime;
    private long length;
    public IdleSlot()
    {
        id=0;
    	startTime = 0;
        endTime = 0;
        length = 0;
    }
    public IdleSlot(int id,double startTime2,double endTime2, long len)
    {
        this.id= id;
    	this.startTime= startTime2 ;
        this.endTime = endTime2 ;
        this.length = len;
    }
    
    public void setId(int id)
    {
     this.id=id ;   
    }
    
    public int getId()
    {
     return id;   
    }
    
    public void setStartTime(double time)
    {
     startTime = time;   
    }
    
    public void setEndTime(double d)
    {
     endTime = d;   
    }
     
    public double getStartTime()
     
    {
     return startTime  ; 
    }
    
    public double getEndTime()
    {
      return endTime;
    }
    
    public void setLength(long len)
    {
     length = len;   
    }
     
    public long getLength()
     
    {
     return length  ; 
    }
   
    public IdleSlot cloneSlot()
    {
    	
    	return new IdleSlot(id  ,startTime, endTime, length);
    }
}
