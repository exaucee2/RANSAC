package main

import (
	"bufio"
	"fmt"
	"math"
	"math/rand"
	"os"
	"strconv"
	"strings"
	"time"
)

type Point3D struct {
	X float64
	Y float64
	Z float64
}
type Plane3D struct {
	A float64
	B float64
	C float64
	D float64
}
type Plane3DwSupport struct {
	Plane3D
	SupportSize int
}

func ReadXYZ(filename string) []Point3D {
	file, err := os.Open(filename)
	if err != nil {
		fmt.Println(err.Error())
	}
	defer file.Close()
	var stocker []Point3D
	scanner := bufio.NewScanner(file)
	for scanner.Scan() {
		line := scanner.Text()
		tab := strings.Split(line, " ")
		x, _ := strconv.ParseFloat(tab[0], 64)
		y, _ := strconv.ParseFloat(tab[1], 64)
		z, _ := strconv.ParseFloat(tab[2], 64)
		stocker = append(stocker, Point3D{x, y, z})
	}
	return stocker
}
func SaveXYZ(filename string, points []Point3D) {
	file, err := os.Create(filename)
	if err != nil {
		fmt.Println(err.Error())
	}
	defer file.Close()
	writer := bufio.NewWriter(file)
	for _, pts := range points {
		fmt.Fprintf(writer, "%f %f %f \n", pts.X, pts.Y, pts.Z)
	}
	writer.Flush()
}
func (p1 *Point3D) GetDistance(p2 *Point3D) float64 {
	//on calcule la differene de coordonnées pour chaque point
	distanceX := math.Pow((p1.X - p2.X), 2)
	distanceY := math.Pow((p1.Y - p2.Y), 2)
	distanceZ := math.Pow((p1.Z - p2.Z), 2)
	return math.Sqrt(distanceX + distanceY + distanceZ)
}
func GetPlane(points []Point3D) Plane3D {
	pt1 := points[0]
	pt2 := points[1]
	pt3 := points[2]
	a := pt2.Y*pt3.Z - pt3.Y*pt2.Z - pt1.Y*pt3.Z + pt3.Y*pt1.Z + pt1.Y*pt2.Z - pt2.Y*pt1.Z
	b := pt2.X*pt3.Z - pt3.X*pt2.Z - pt1.X*pt3.Z + pt3.X*pt1.Z + pt1.X*pt2.Z - pt2.X*pt1.Z
	c := pt2.X*pt3.Y - pt3.X*pt2.Y - pt1.X*pt3.Y + pt3.X*pt1.Y + pt1.X*pt2.Y - pt2.X*pt1.Y
	d := -pt1.X*a - pt1.Y*b - pt1.Z*c
	return Plane3D{a, b, c, d}
}
func GetNumberOfIterations(confidence float64, percentageOfPointsOnPlane float64) int {
	numberOfIterations := int(math.Log(1-confidence) / math.Log(1-math.Pow(percentageOfPointsOnPlane, 3)))
	return numberOfIterations
}
func GetSupport(plane Plane3D, points []Point3D, eps float64) Plane3DwSupport {
	//on va initialiser la taille du support à 0
	SupportSize := 0
	//trouver les coefficients de l'equation cartesienne du plan
	a := plane.A
	b := plane.B
	c := plane.C
	d := plane.D
	//on va parcourir les points et verfier si ils sont sur le plan
	for _, p := range points {
		//si la distance est inferieure ou egale à epsilon alors on incremente le support
		if math.Abs(a*p.X+b*p.Y+c*p.Z+d)/math.Sqrt(a*a+b*b+c*c) <= eps {
			SupportSize++
		}
	}
	return Plane3DwSupport{plane, SupportSize}
}
func GetSupportingPoints(plane Plane3D, points []Point3D, eps float64) []Point3D {
	var liste []Point3D
	a1 := plane.A
	b1 := plane.B
	c1 := plane.C
	d1 := plane.D
	for _, ptt := range points {
		if math.Abs(a1*ptt.X+b1*ptt.Y+c1*ptt.Z+d1)/math.Sqrt(a1*a1+b1*b1+c1*c1) <= eps {
			liste = append(liste, ptt)
		}
	}
	return liste

}
func RemovePlane(plane Plane3D, points []Point3D, eps float64) []Point3D {
	var listes []Point3D
	a2 := plane.A
	b2 := plane.B
	c2 := plane.C
	d2 := plane.D
	for _, c := range points {
		if math.Abs(a2*c.X+b2*c.Y+c2*c.Z+d2)/math.Sqrt(a2*a2+b2*b2+c2*c2) <= eps {
			listes = append(listes, c)
		}
	}
	return listes
}
func main() {
	filename := os.Args[1]
	stocker := ReadXYZ(filename)
	bestSupport := Plane3DwSupport{
		Plane3D{0.0, 0.0, 0.0, 0.0},
		0}
	confidence, _ := strconv.ParseFloat(os.Args[2], 64)
	percentageOfPointsOnPlane, _ := strconv.ParseFloat(os.Args[3], 64)
	NumberOfIterations := GetNumberOfIterations(confidence, percentageOfPointsOnPlane)
	eps, _ := strconv.ParseFloat(os.Args[4], 64)
	// Génération d'un nuage de points aléatoire
	rand.Seed(time.Now().UnixNano())
	points := make([]Point3D, 3)
	for i := 0; i < 3; i++ {
		points[i] = Point3D{X: rand.Float64(), Y: rand.Float64(), Z: rand.Float64()}
	}

	// Création des channels
	randomPointChan := make(chan Point3D)
	tripletChan := make(chan [3]Point3D)
	takeNChan := make(chan [3]Point3D)
	planeChan := make(chan Plane3D)
	supportChan := make(chan Plane3DwSupport)

	dominantPlane := &Plane3DwSupport{}

	// Random point generator
	go func() {
		for {
			randomPointChan <- points[rand.Intn(3)]
		}
	}()

	// Triplet of points generator
	go func() {
		triplet := [3]Point3D{}
		for {
			triplet[0] = <-randomPointChan
			triplet[1] = <-randomPointChan
			triplet[2] = <-randomPointChan
			tripletChan <- triplet
		}
	}()

	// TakeN
	go func() {
		for i := 0; i < NumberOfIterations; i++ {
			takeNChan <- <-tripletChan
		}
		close(takeNChan)
	}()

	// Plane estimator
	go func() {
		for triplet := range takeNChan {
			plane := GetPlane(triplet[:])
			planeChan <- plane
		}
		close(planeChan)
	}()

	// Supporting point finder
	go func() {
		for plane := range planeChan {
			support := GetSupport(plane, points, eps)

			supportChan <- support
		}
		close(supportChan)
	}()

	// Fan In
	go func() {
		for support := range supportChan {
			if support.SupportSize > dominantPlane.SupportSize {
				dominantPlane.Plane3D = support.Plane3D
				dominantPlane.SupportSize = support.SupportSize
			}
		}
	}()

	// Attendre la fin du pipeline
	<-time.After(time.Duration(NumberOfIterations) * time.Second)

	// Afficher le plan dominant
	fmt.Printf("Dominant plane: %vx + %vy + %vz + %v = 0\n", dominantPlane.A, dominantPlane.B, dominantPlane.C, dominantPlane.D)
	fmt.Printf("Support size: %v\n", dominantPlane.SupportSize)
	SaveXYZ(filename+"p", GetSupportingPoints(bestSupport.Plane3D, stocker, eps))
	SaveXYZ(filename+"_p0", RemovePlane(bestSupport.Plane3D, stocker, eps))
}

/*points := []Point3D{{1.2, 2.5, 6.2}, {2.0, 6.1, 8.3}, {2.1, 8.5, 6.1},{1.2,2.5,.8}}

SaveXYZ("PoinCloud1", points)
read := ReadXYZ("PoinCloud1")
fmt.Println(read)
plane := GetPlane(points)
fmt.Println("plan", plane)
distance := points[0].GetDistance(&points[1])
fmt.Println("distance", distance)
confidence := 0.99
percentageOfPointsOnPlane := 0.9
eps := 0.5
itt := GetNumberOfIterations(confidence, percentageOfPointsOnPlane)
fmt.Println("itt", itt)
plan := Plane3D{1, 2, 3, 5}
support := GetSupport(plan, points, eps)
fmt.Println("support", support)
supp := GetSupport(plan, points, eps)
fmt.Println("supp", supp)
remov := GetSupport(plan, points, eps)
fmt.Println("remov", remov)
*/
