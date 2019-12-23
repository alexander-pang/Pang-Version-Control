a)
Daniel Conway, Alex Pang, Seojin Ko

b)
https://research-git.uiowa.edu/dyconway/oosd-group-project

c)
Nothing & we left comments for the source code that used at the really first of each file.

d) a list of the files created by your code to save the revisions:
usually it is the xml file, the patch files, and any other files you need to keep track of the changes of a file.

filename.filetype
.filename.filetype
filename.xml
filename_last.filetype
current.patch
temp.txt
temp2.txt

For example, if our filename.filetype is a.txt, .a.txt, a.xml, a_last.txt, current.patch, temp.txt and temp2.txt is created by our codes.

e)

******* Note: For our command line, the order of options does not matter! *******

java -jar oosd-group-project.jar commit -f A.txt -br firstBr    	        // commit command line - will make 1.1 with branch name firstBr

java -jar oosd-group-project.jar commit -f A.txt -m			    	        // commit command line - will make 1.2 and let you type commit message

java -jar oosd-group-project.jar commit -f A.txt -m				            // commit command line - will make 1.3 and let you type commit message

java -jar oosd-group-project.jar checkout -rev 1.1 -f A.txt                 // checkout command line - will set current to 1.1

java -jar oosd-group-project.jar commit -f A.txt -m 			            // commit command line - will make a branch 1.1.1 and let you type commit message

java -jar oosd-group-project.jar commit -f A.txt -m 			            // commit command line - will make 1.1.1.1 and let you type commit message

java -jar oosd-group-project.jar checkout -br firstBr -f A.txt	            // checkout command line - will set current to 1.1 whose branch name is firstBr

java -jar oosd-group-project.jar merge -f A.txt -rev 1.1.1 -rev 1.3	    	// merge command line - will merge 1.1.1 and 1.3 and put into 1.4

java -jar oosd-group-project.jar rename -f A.txt -rev 1.3 -br thirdBr   	// rename command line - will give a branch name, thirdB, third to 1.3

java -jar oosd-group-project.jar rename -f A.txt -br thirdBr -br firstBr	//  rename command line - won't work because branch name, firstBr, already exists

java -jar oosd-group-project.jar rename -f A.txt -br thirdBr -br newThird	// rename command line - will change the branch name of 1.3, thirdBr, to newThird

java -jar oosd-group-project.jar diff -f A.txt						        // diff command line - prints a diff

java -jar oosd-group-project.jar branch -list A.txt					        // branch command line - prints all branches of A.txt
