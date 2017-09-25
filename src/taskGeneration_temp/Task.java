package taskGeneration_temp;
public class Task 
{
    protected int id;
    protected int c,d,t;
    @Override
    public String toString() {
		return "[Processus= P" + id + ", Temps d'exécution =" + c + ", Echéance =" + d + ", Période =" + t + "]";
	}
    public Task(int id, String c, String d, String t) {
        this.id = id;
        this.c = Integer.parseInt(c);
        this.d = Integer.parseInt(d);
        this.t = Integer.parseInt(t);
    }
    public Task(int id, int c, int d, int t) {
		this.id = id;
		this.c = c;
		this.d = d;
		this.t = t;
	}
    public int getId() {
		return id;
	}
    public int getC() {
		return c;
	}
    public int getD() {
		return d;
	}
    public int getT() {
		return t;
	}
    public double computeUtil(){
		return c/(double)t;
	}
    public double computeDensity(){
		return c/(double)d;
	}
    public void setId(int id) {
        this.id = id;
    }
    public void setC(int c) {
        this.c = c;
    }
    public void setD(int d) {
        this.d = d;
    }
    public void setT(int t) {
        this.t = t;
    }
}
