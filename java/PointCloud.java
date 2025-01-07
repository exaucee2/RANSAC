import java.util.*;
import java.io.*;
import java.lang.*;

public class PointCloud {
    Random random = new Random();
	List<Point3D> pointList = new ArrayList<Point3D>();
    String header = "x	y	z";
    int counter = 0;
	
    //Le constructeur qui lit directement du fichier xyz
    public PointCloud(String fileName){
		try (Scanner scan = new Scanner(new FileReader(fileName)))
        {
			String head= scan.nextLine();
            while(scan.hasNextLine()) 
            {
				String[] p = scan.nextLine().split("\\s+");
				double x = Double.parseDouble(p[0]);
				double y = Double.parseDouble(p[1]);
                double z = Double.parseDouble(p[2]);
                
                Point3D pt = new Point3D(x,y,z);
                addPoint(pt);
                
			}
            
            System.out.println("Successfully created the pointcloud object");
            System.out.println("------------------------------------------");
            System.out.println("There are : "+ counter+" points");
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
    }
    //Ajouter un point dans le nuage de points
	public void addPoint(Point3D pt){
		pointList.add(pt);
        counter++;
	}
    //Prendre un point au hasard du nuage de points
	public Point3D getPoint(){
		int nbr = getSize();
        int rand = random.nextInt(nbr);
        return pointList.get(rand);
	}
    //Sauvegarder le nuage de points dans un fichier XYZ
	public void save(String fileName){
		try {
            BufferedWriter bw = null;
            FileWriter fw = new FileWriter(fileName);
            bw = new BufferedWriter(fw);
            bw.write(header);
            bw.newLine();
            for(Point3D i: pointList){
				bw.write(i.toString());
                bw.newLine();
			}
            bw.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
			e.printStackTrace();
        }
	}
    //L'iterateur de la fonction
	public Iterator<Point3D> iterator(){
		return pointList.iterator();
	}
	public int getSize(){
		return pointList.size();
	}
    /*public static void main(String[] args){
        PointCloud pc = new PointCloud(args[0]);
        
        pc.save("PointCloudTestData.xyz");
        
        
    }*/
	
	
}