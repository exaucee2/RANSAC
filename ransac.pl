read_xyz_file(File, Points) :-
 open(File, read, Stream),
 read_xyz_points(Stream,Points),
 close(Stream).
read_xyz_points(Stream, []) :-
 at_end_of_stream(Stream).
read_xyz_points(Stream, [Point|Points]) :-
 \+ at_end_of_stream(Stream),
 read_line_to_string(Stream,L), split_string(L, "\t", "\s\t\n",
XYZ), convert_to_float(XYZ,Point),
 read_xyz_points(Stream, Points).
convert_to_float([],[]).
convert_to_float([H|T],[HH|TT]) :-
 atom_number(H, HH),
 convert_to_float(T,TT). 


 random3points(Points, Point3) :-
    random_select([X1,Y1,Z1], Points, P1), 
    random_select([X2,Y2,Z2], P1, P2), 
    random_select([X3,Y3,Z3],P2, _),
    Point3 = [[X1,Y1,Z1], [X2,Y2,Z2], [X3,Y3,Z3]].

    plane(Point3, [A,B,C,D]) :-
    Point3 = [[X1,Y1,Z1], [X2,Y2,Z2], [X3,Y3,Z3]],
    A is Y1*(Z2-Z3) + Y2*(Z3-Z1)+ Y3 *(Z2-Z1),
    B is Z1*(X2-X1)+ Z2*(X3-X1) + Z3*(X1-X2),
    C is X1*(Y2-Y3) + X2*(Y3-Y1) + X3* (Y1-Y2),
    D is -X1*(Y2*Z3-Z2*Y3)- X2*(Y3*Z1-Z3*Y1)-X3*(Y1*Z2-Z1*Y2).

    distance([A,B,C,D],[X,Y,Z], Distance) :-
    Distance is abs(A*X + B*Y + C*Z + D) / sqrt(A*A + B*B + C*C).



    support(Plane, Points, Eps, N) :-
    findall(Point, (member(Point, Points),distance(Plane, Point, Distance),Distance =< Eps), PointsOnPlane),
    length(PointsOnPlane, N),
     N >= 3.


   
   ransac_number_of_iterations(Confidence, Percentage, N) :-
     N is round(log(1 - Confidence) / log(1 - Percentage^3)).





    
 % TESTS POUR LE PREDICAT random3points

% Ce test vérifie le prédicat random3points en vérifiant qu'il retourne trois points distincts de la liste d'entrée.
test(random3points, 1) :-
    Points = [[7,12],[8,10],[11,2]],
    random3points(Points, [P1,P2,P3]),
    permutation([P1,P2,P3], Points).

% Si la longueur est inférieure à 3, cela signifie qu'il n'y a pas assez de points uniques générés, et le test échoue.
test(random3points, 2) :-
    \+ (random3points(Points, _), length(Points, Length), Length < 3).

% on verifie que le prédicat fail  lorsqu il  est appliqué à une liste de  vide.
test(random3points, 3) :-
    random3points([], _),
    fail.
