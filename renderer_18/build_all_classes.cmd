javac -g -Xlint -Xdiags:verbose       renderer\framebuffer\*.java  &&^
javac -g -Xlint -Xdiags:verbose             renderer\scene\*.java  &&^
javac -g -Xlint -Xdiags:verbose  renderer\scene\primitives\*.java  &&^
javac -g -Xlint -Xdiags:verbose        renderer\scene\util\*.java  &&^
javac -g -Xlint -Xdiags:verbose          renderer\models_L\*.java  &&^
javac -g -Xlint -Xdiags:verbose         renderer\models_LP\*.java  &&^
javac -g -Xlint -Xdiags:verbose          renderer\models_F\*.java  &&^
javac -g -Xlint -Xdiags:verbose          renderer\models_T\*.java  &&^
javac -g -Xlint -Xdiags:verbose         renderer\models_TP\*.java  &&^
javac -g -Xlint -Xdiags:verbose          renderer\pipeline\*.java  &&^
javac -g -Xlint -Xdiags:verbose                clients_r18\*.java  &&^
javac -g -Xlint -Xdiags:verbose                clients_r17\*.java  &&^
javac -g -Xlint -Xdiags:verbose                clients_r14\*.java
pause
