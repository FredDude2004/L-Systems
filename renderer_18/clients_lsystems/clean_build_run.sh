rm *.ppm
rm *.class


javac -g -Xlint -Xdiags:verbose -cp .:.. Turtle3DTestTwo.java
java -cp .:.. Turtle3DTestTwo

