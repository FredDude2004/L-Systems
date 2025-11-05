rm *.ppm
rm *.class
javac -g -Xlint -Xdiags:verbose -cp .:.. HilbertCurve3D.java
java -cp .:.. HilbertCurve3D
