The main code I have modified is in the basic and src/Test directories. The basic class file are Mat.java, STATE_NODE.java, STATE.java, and the run file is Test/WorkflowMDP.java.
In the WorkflowMDP.java, there are two main methods: readfile and runWorkflow. 
The readfile method read "example_MDP.txt" in the Test directory and initiate the static fields, such as m, n, dollarCost, time, reliability. 
The runWorkflow method run MDP and prints the result.