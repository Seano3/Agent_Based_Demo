To run sim add a csv file to working directory called agent-input.csv 

where each agent is formated like :

agent ID, size, xPos, yPos, xVel, yVel


Scale 

30	px  =	1	m	
                
Adults				
Scale	Size	±	Speed	±
Meters	0.255	0.035	1.25	0.3
Pixels	7.65	1.05	37.5	9
                
Children				
Scale	Size	±	Speed	±
Meters	0.21	0.015	0.9	0.3
Pixels	6.3	    0.45	27	9
                
Elderly				
Scale	Size	±	Speed	±
Meters	0.25	0.02	0.8	0.3
Pixels	7.5	    0.6	    24	9



Timestep: 

Delta T < 0.1(min radius)/(max velocity)

0.1(0.195)/(1.55) = 0.01