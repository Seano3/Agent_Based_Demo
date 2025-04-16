import os

def generate_agents(x, y, size, number_of_agents, columns, gap, starting_id):
    agents = []
    agent_gap = size * 2 + gap
    for i in range(number_of_agents):
        agent = [
            i + starting_id,
            size,
            x + agent_gap * (i % columns),
            y + i // columns * agent_gap,
        ]

        agents.append(agent)
    return agents


file_path = os.getcwd() + '\\AgentInputGenerator\\agent-input.csv'
if __name__ == '__main__':
    # x of starting agent
    x = 375
    # y of starting agent
    y = 280
    # size of all agents
    size = 15
    # number of agents in block
    number_of_agents = 10
    # number of agents to spawn before moving to new row
    columns = 5
    # gap between agents excluding radius of agents
    gap = 5
    # velocity initialization must be done manually atm
    starting_id = 0
    # for generating inputs in chunks where you can't start at 0

    agents = generate_agents(x,y,size,number_of_agents,columns, gap, starting_id)

    f = open(file_path, 'w', newline='')
    for agent in agents:
        f.write(f"{agent[0]}, {agent[1]}, {agent[2]}, {agent[3]}, 0, 0\n")
    print("*** agent input generated successfully***")
