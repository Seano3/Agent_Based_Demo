import os

def generate_agents(x, y, size, number_of_agents, columns, gap):
    agents = []
    agent_gap = size * 2 + gap
    for i in range(number_of_agents):
        agent = [
            i,
            size,
            x + agent_gap * (i % columns),
            y + i // columns * agent_gap,
        ]

        agents.append(agent)
    return agents


file_path = os.getcwd() + '\\AgentInputGenerator\\agent-input.csv'
if __name__ == '__main__':
    # x of starting agent
    x = 300
    # y of starting agent
    y = 300
    # size of all agents
    size = 10
    # number of agents in block
    number_of_agents = 20
    # number of agents to spawn before moving to new row
    columns = 10
    # gap between agents excluding radius of agents
    gap = 5
    # velocity initialization must be done manually atm

    agents = generate_agents(x,y,size,number_of_agents,columns, gap)

    f = open(file_path, 'w', newline='')
    for agent in agents:
        f.write(f"{agent[0]}, {agent[1]}, {agent[2]}, {agent[3]}, 0, 0\n")

