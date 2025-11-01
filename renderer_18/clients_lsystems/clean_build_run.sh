rm *.ppm
rm *.class


javac -g -Xlint -Xdiags:verbose -cp .:.. Tetrahedron3D.java
java -cp .:.. Tetrahedron3D

