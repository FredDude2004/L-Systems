javac -g -Xlint -Xdiags:verbose  -cp .;..;../renderer_18  %1
java                             -cp .;..  %~n1
pause
