
import java.lang.*;
import java.util.*;
import java.io.*;

public class PlaneRANSAC {
	double eps;
	PointCloud pc;
	long support;
	ArrayList<Point3D> listPoints = new ArrayList<Point3D>();
	
	
	//int count = 1;
	String header = "x	y	z";
	
	Iterator<Point3D> itera;
	
	public PlaneRANSAC(PointCloud pc){
		this.pc = pc;
		support = 0;
		itera = pc.iterator();
	}
	public void setEps(double e){
		eps = e;
	}
	public double getEps(){
		return eps;
	}
	
    //Obtenir le nombre d'iterations requis
	public int getNumberOfIterations(double confidence, double percentageOfPointsOnPlane){
		double k = (Math.log(1-confidence))/Math.log(1-percentageOfPointsOnPlane*percentageOfPointsOnPlane*percentageOfPointsOnPlane);
        int nbrIterations = (int)Math.round(k);
		return nbrIterations;
	}
    
    //Run Ransac
    public PointCloud run(int numberOfIterations, String fileName, int nbrTimes){
		Plane3D planDominant;
		
		int miniSupport = 0;
		//RANSAC proprement dit pour trouver une possibilite du plan dominant
		for(int i=0; i<numberOfIterations; i++){
			Point3D pt1 = pc.getPoint();
			Point3D pt2 = pc.getPoint();
			Point3D pt3 = pc.getPoint();
			
            
			Plane3D plan = new Plane3D(pt1,pt2,pt3);
			//Iterator<Point3D> it = plan.iterator();
            
			
			while(itera.hasNext()){
				Point3D p = itera.next();
                
				if(plan.getDistance(p) < eps){
					listPoints.add(p);
					miniSupport++;
				}
                
			}
            if(miniSupport > support){
                planDominant = plan;
                support = miniSupport;
            }
            miniSupport = 0;
			
		}
        
		//Suppression des points dans le plan dominant trouvé
		for(Point3D i : listPoints){
			Iterator<Point3D> iterat = pc.iterator();
			while(iterat.hasNext()){
				Point3D ptComp = iterat.next();
				if(i.equals(ptComp)){
					iterat.remove();
					break;
				}
			}
		}
		
		//Sauvegarde des points du plan dominant trouvé
        saveToFile(listPoints, fileName, nbrTimes);
		
		//Reinitialiser l'iterateur
		Iterator<Point3D> iterat = pc.iterator();
		itera = iterat;
		//Retour du nouveau PointCloud
		return pc;
        //count++;
    }
    public void saveToFile(List<Point3D> pts, String fileName, int i){
        StringBuffer sb = new StringBuffer();
		
        sb.append(fileName);
        sb.append("_p" + (i+1));
        sb.append(".xyz");
		String domPlFile = sb.toString();
		try {
            BufferedWriter bw = null;
            FileWriter fw = new FileWriter(domPlFile);
            bw = new BufferedWriter(fw);
            bw.write(header);
            bw.newLine();
            for(Point3D p: pts){
				bw.write(p.toString());
                bw.newLine();
			}
            bw.close();
            System.out.println("Successfully wrote to the Test file.");
			System.out.println("The size of the dominant plane is :" + pts.size());
        } catch (IOException e) {
			e.printStackTrace();
        }
    }
    
    public static void main(String[] args){
		//for(int i = 0; i<3; i++){
			PointCloud pc = new PointCloud(args[0]);
		
			PlaneRANSAC pr = new PlaneRANSAC(pc);
			
			double eps = Double.parseDouble(args[1]);
			pr.setEps(eps);
			
			int iterations = pr.getNumberOfIterations(0.99, 0.05);
			
			System.out.println("Number of iterations " + iterations);
			
			int nbrOfRansacRun = Integer.parseInt(args[2]);
			
			String[] tokens = (args[0]).split("\\.");
			String file = tokens[0];
			
			//pr.run(iterations, file, i);
			//System.out.println("The new size is: "+ pc.getSize());
			
			for(int i = 0; i<nbrOfRansacRun; i++){
				pc = pr.run(iterations, file, i);
				pr = new PlaneRANSAC(pc);
				pr.setEps(eps);
				System.out.println("The new size is: "+ pc.getSize());
			}
			
			pc.save(file+"_p0.xyz");
			System.out.println("The new size is: "+ pc.getSize());
		//}
        
		
		
		
	}
	
	
}