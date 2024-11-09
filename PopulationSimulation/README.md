To run sim add a csv file to working directory called agent-input.csv 

where each agent is formated like :

agent ID, size, xPos, yPos, xVel, yVel


Scale 

10	px  =	1	m	
                
Adults				
Scale	Size	±	Speed	±
Meters	0.255	0.035	1.25	0.3
Pixels	2.55	0.35	12.5	3
                
Children				
Scale	Size	±	Speed	±
Meters	0.21	0.015	0.9	0.3
Pixels	2.1    0.15	    9	3
                
Elderly				
Scale	Size	±	Speed	±
Meters	0.25	0.02	0.8	0.3
Pixels	2.5	    0.2	    8	3



Timestep: 

Delta T < 0.1(min radius)/(max velocity)

0.1(0.195)/(1.55) = 0.01