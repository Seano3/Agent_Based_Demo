import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
import numpy as np
import os

def read_csv_files(directory):
    """Read all CSV files in the given directory and return a concatenated DataFrame."""
    dfs = []
    # dfs.append("x,y")  # Add header to the list
    for filename in os.listdir(directory):
        if filename.endswith('.csv'):
            df = pd.read_csv(os.path.join(directory, filename))
            dfs.append(df)
    return pd.concat(dfs, ignore_index=True)

def plot_coordinates(df):
    """Plot the coordinates from the DataFrame."""
    print("DataFrame columns:", df.columns)  # Debugging step
    # Check if columns 'x' and 'y' exist with exact names
    if 'x' not in df.columns or 'y' not in df.columns:
        # Try to find columns with similar names (case-insensitive and stripping spaces)
        df.columns = df.columns.str.strip().str.lower()
        if 'x' not in df.columns or 'y' not in df.columns:
            raise KeyError("The DataFrame does not contain 'x' and 'y' columns.")
    
    plt.figure(figsize=(10, 8))
    sns.kdeplot(data=df, x='x', y='y', cmap='viridis', fill=True, bw_adjust=.5)
    plt.gca().invert_yaxis()  # Invert y-axis if necessary
    plt.title('Heatmap of Coordinate Frequencies')
    plt.xlabel('X Coordinate')
    plt.ylabel('Y Coordinate')
    plt.show()

if __name__ == "__main__":
    directory = './Agent Files'  # Replace with the path to your CSV files
    df = read_csv_files(directory)
    print(df)
    plot_coordinates(df)