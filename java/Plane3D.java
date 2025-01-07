

import java.math.*;

public class Plane3D{
	Point3D p1,p2,p3;
	double a,b,c,d;
	
	public Plane3D(Point3D p1, Point3D p2, Point3D p3){
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
        findPlane(p1, p2, p3);
	}
	public Plane3D(double a, double b, double c, double d){
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	public double getA(){
		return a;
	}
	public double getB(){
		return b;
	}
	public double getC(){
		return c;
	}
    public double getD(){
		return d;
	}
    public void findPlane(Point3D p1, Point3D p2, Point3D p3){
        //Find first vector
        double v1_x = (p2.getX()-p1.getX());
        double v1_y = (p2.getY()-p1.getY());
        double v1_z = (p2.getZ()-p1.getZ());
        
        //Find second vector
        double v2_x = (p3.getX()-p1.getX());
        double v2_y = (p3.getY()-p1.getY());
        double v2_z = (p3.getZ()-p1.getZ());
        
        //v1 cross product v2 to find the normal vector
        a = (v1_y*v2_z)-(v2_y*v1_z);
        b = -((v1_x*v2_z)-(v2_x*v1_z));
        c = (v1_x*v2_y)-(v2_x*v1_y);
        
        d = -(a*p1.getX() + b*p1.getY()+ c*p1.getZ());
    }
	
	public double getDistance(Point3D pt){
		//Finding a point in the plane
        double x,y,z;
        if(a != 0) {
            x = (-d)/a;
            y = 0;
            z = 0;
        } else if (b!=0) {
            x = 0;
            y = (-d)/a;
            z = 0;
        } else {
            x = 0;
            y = 0;
            z = (-d)/a;
        }
		double dist = Math.abs(a*(x-pt.getX()) + b*(y-pt.getY()) + c*(z-pt.getZ()))/(Math.sqrt(a*a + b*b + c*c));
        return dist;
	}
	
	//Une methode main servant a debugger Plane3d
	/*public static void main(String[] args){
		double a = Double.parseDouble(args[0]);
		double b = Double.parseDouble(args[1]);
		double c = Double.parseDouble(args[2]);
		double d = Double.parseDouble(args[3]);
		
		double x = Double.parseDouble(args[4]);
		double y = Double.parseDouble(args[5]);
		double z = Double.parseDouble(args[6]);
		
		Point3D pt1 = new Point3D(1,2,-2);
		Point3D pt2 = new Point3D(3,-2,1);
		Point3D pt3 = new Point3D(5,1,-4);
		
		
		
		Plane3D plan = new Plane3D(a, b, c, d);
		
		System.out.println(plan.getA()+"x + "+ plan.getB()+"y + "+ plan.getC()+"z + "+ plan.getD()+" = 0");
		
		Point3D pt4 = new Point3D(x,y,z);
		
		System.out.println("La distance du point au plan est :"+plan.getDistance(pt4));
        
        
		
		
	}*/
}