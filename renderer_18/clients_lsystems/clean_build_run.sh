rm *.ppm
rm *.class


javac -g -Xlint -Xdiags:verbose -cp .:.. Turtle3DTest.java
java -cp .:.. Turtle3DTest

