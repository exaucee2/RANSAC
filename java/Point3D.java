
public class Point3D{
	double x;
	double y;
	double z;
	public Point3D(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX(){
		return x;
	}
	public double getY(){
		return y;
	}
    public double getZ(){
		return z;
	}
	
	
	@Override
	public String toString(){
		return x+"	"+y+"	"+z;
	}
	
	public boolean equals(Point3D p){
		return this.x == p.getX() && this.y == p.getY() && this.z == p.getZ();
	}
}