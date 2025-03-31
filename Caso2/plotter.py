import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import matplotlib.ticker as ticker

# Constants
DATA_FILE_PATH = 'output/data.txt'
EXPECTED_ROW_COUNT = 6
ERROR_MESSAGE = "You must run the main with 'generateGraphData = true'"
FIGURE_SIZE = (10, 6)
BAR_WIDTH = 0.2
GRID_ENABLED = True
HITS_Y_LIMIT_MIN = 735000
HITS_Y_LIMIT_MAX = 760000
LEGEND_LOCATION = 'lower right'

def main():
    """Main function to execute the plotting process"""
    df = load_data()
    validate_data(df)

    create_page_faults_graph(df)
    create_hits_graph(df)

    display_graphs()

def load_data():
    """Loads data from the output file"""
    return pd.read_csv(DATA_FILE_PATH, sep=r'\s+', header=0)

def validate_data(df):
    """Validates that the data frame has the expected format"""
    if df.shape[0] != EXPECTED_ROW_COUNT:
        raise ValueError(ERROR_MESSAGE)

def create_page_faults_graph(df):
    """Creates the page faults graph with logarithmic scale"""
    fig1, ax1 = plt.subplots(figsize=FIGURE_SIZE)

    bar_positions = calculate_bar_positions(df)
    plot_fault_bars(ax1, df, bar_positions)

    configure_fault_graph_appearance(ax1, df, bar_positions)
    configure_logarithmic_scale(ax1, df)

def create_hits_graph(df):
    """Creates the page hits graph"""
    fig2, ax2 = plt.subplots(figsize=FIGURE_SIZE)

    bar_positions = calculate_bar_positions(df)
    plot_hit_bars(ax2, df, bar_positions)

    configure_hit_graph_appearance(ax2, df, bar_positions)

def calculate_bar_positions(df):
    """Calculates the positions of the bars on the x-axis"""
    return np.arange(len(df['page_size'].unique()))

def plot_fault_bars(ax, df, bar_positions):
    """Plots the bars for the page faults graph"""
    for i, frames in enumerate(df['frames_assigned'].unique()):
        df_frames = df[df['frames_assigned'] == frames]
        ax.bar(bar_positions + i * BAR_WIDTH, df_frames['number_faults'],
               BAR_WIDTH, label=f'{frames} frames')

def plot_hit_bars(ax, df, bar_positions):
    """Plots the bars for the hits graph"""
    for i, frames in enumerate(df['frames_assigned'].unique()):
        df_frames = df[df['frames_assigned'] == frames]
        ax.bar(bar_positions + i * BAR_WIDTH, df_frames['number_hits'],
               BAR_WIDTH, label=f'{frames} frames')

def configure_fault_graph_appearance(ax, df, bar_positions):
    """Configures the appearance of the page faults graph"""
    ax.set_xlabel('Page Size')
    ax.set_ylabel('Num Faults (Note: Logarithmic scale)')
    ax.set_title('Total Number of Page Faults\n(by Page Size and Assigned Frames)')
    set_xticks_and_labels(ax, df, bar_positions)
    ax.legend()
    ax.grid(GRID_ENABLED)

def configure_hit_graph_appearance(ax, df, bar_positions):
    """Configures the appearance of the hits graph"""
    ax.set_xlabel('Page Size')
    ax.set_ylabel('Num Hits')
    ax.set_title('Total Number of Hits\n(by Page Size and Assigned Frames)')
    set_xticks_and_labels(ax, df, bar_positions)
    ax.set_ylim(HITS_Y_LIMIT_MIN, HITS_Y_LIMIT_MAX)
    ax.legend(loc=LEGEND_LOCATION)
    ax.grid(GRID_ENABLED)

def set_xticks_and_labels(ax, df, bar_positions):
    """Sets the x-ticks and x-labels for the graphs"""
    frame_count = len(df['frames_assigned'].unique())
    center_offset = BAR_WIDTH * (frame_count - 1) / 2
    ax.set_xticks(bar_positions + center_offset)
    ax.set_xticklabels(df['page_size'].unique())

def configure_logarithmic_scale(ax, df):
    """Configures the logarithmic scale for the page faults graph"""
    ax.set_yscale('log')
    ax.yaxis.set_major_formatter(ticker.ScalarFormatter())

    max_fault = df["number_faults"].max()
    print(max_fault)
    log = np.log10(max_fault)
    zeros = int(log // 1)
    logarithmic_scale = [10**x for x in range(zeros+1)]
    ax.set_yticks(logarithmic_scale)
    print(logarithmic_scale)

def display_graphs():
    """Displays all generated graphs"""
    plt.show()

if __name__ == "__main__":
    main()