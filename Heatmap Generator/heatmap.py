import pandas as pd
import numpy as np
import os
import matplotlib.pyplot as plt

#README: To use this just put the agent output files into the Agent Files folder and run the script. It will output a csv file with the heatmap data.
# use excel to turn that into a colorful one afterwards :)

def read_csv_files(directory):
    """Read all CSV files in the given directory and return a concatenated DataFrame."""
    dfs = []
    for filename in os.listdir(directory):
        if filename.endswith('.csv'):
            df = pd.read_csv(os.path.join(directory, filename))
            df.columns = ['x', 'y']
            dfs.append(df)
    return pd.concat(dfs, ignore_index=True)

def count_numbers(df):
    """Count duplicate rows in the DataFrame and return a table of distinct rows with their counts."""
    # Count occurrences of each distinct row
    counts = df.value_counts().reset_index()
    counts.columns = list(df.columns) + ['count']  # Rename columns to include 'count'
    return counts


def max_value(dt):
    """Return the maximum value in the 'count' column of the DataFrame."""
    return dt['count'].max()

def convert_count_to_table(dt):
    #Max Size of the Simulation
    max_x = 110
    max_y = 72
    
    matrix = np.zeros((max_x + 1, max_y + 1), dtype=int)
    
    for _, row in dt.iterrows():
        x, y, count = int(row['x']), int(row['y']), int(row['count'])
        matrix[x, y] = count
        
    return matrix


if __name__ == "__main__":
    directory = './Agent-Files'  # Replace with the path to your CSV files
    df = read_csv_files(directory)
    dt = count_numbers(df)
    max = max_value(dt)
    dm = convert_count_to_table(dt)
    
    dm = np.rot90(dm, k=1) #Rotate 90 becuase our origin is at the upper left rather than bottem left
    
    print(dm)
    np.savetxt("output_heatmap.csv", dm, delimiter=",", fmt='%d')
    
    normalized_dm = dm / max
    plt.imshow(normalized_dm, cmap='Reds', origin='lower')
    plt.colorbar(label='Intensity')  # Add a color bar to show the scale
    plt.title("Heatmap")
    # plt.xlabel("X")
    # plt.ylabel("Y")
    
    plt.savefig("output_heatmap.png")
    plt.show()