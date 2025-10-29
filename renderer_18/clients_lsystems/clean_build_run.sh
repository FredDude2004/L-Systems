rm *.ppm
rm *.class


javac -g -Xlint -Xdiags:verbose -cp .:.. Example1.java
java -cp .:.. Example1

