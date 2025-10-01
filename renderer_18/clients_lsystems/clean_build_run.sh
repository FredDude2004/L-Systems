rm *.ppm
rm *.class

javac -g -Xlint -Xdiags:verbose -cp .:.. DOLSystem.java
java -cp .:.. DOLSystem

