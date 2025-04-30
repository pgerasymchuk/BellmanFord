import pandas as pd
import matplotlib.pyplot as plt

def plot_metric(
        df,
        x_col,
        y_col,
        title='',
        xlabel='',
        ylabel='',
        output_path='diagram.png',
        group_by_col=None
):
    if group_by_col:
        groups = df.groupby(group_by_col)
    else:
        groups = [('All Data', df)]

    plt.figure(figsize=(10, 6))

    for label, group in groups:
        group_sorted = group.sort_values(x_col)

        if isinstance(label, tuple) and isinstance(group_by_col, list):
            formatted_label = ", ".join(
                f"{col}: {val}" for col, val in zip(group_by_col, label)
            )
        else:
            formatted_label = f"{group_by_col}: {label}" if group_by_col else label

        plt.plot(group_sorted[x_col], group_sorted[y_col], marker='o', label=formatted_label)

    plt.title(title)
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.grid(True)
    # if group_by_col:
    #     plt.legend(title="Grouped by")
    plt.legend()
    plt.tight_layout()

    plt.savefig(output_path, dpi=300)
    plt.show()


path = '../test1.csv'
df = pd.read_csv(path)
plot_metric(
    df,
    x_col='Threads',
    y_col='Speedup',
    title='Speedup vs Number of threads',
    xlabel='Number of Threads',
    ylabel='Speedup',
    output_path='diagrams/speedup_nthreads.png',
    group_by_col=['Vertices', 'AvgEdges']
)
plot_metric(
    df,
    x_col='Threads',
    y_col='Efficiency',
    title='Efficiency vs Number of threads',
    xlabel='Number of Threads',
    ylabel='Efficiency',
    output_path='diagrams/efficiency_nthreads.png',
    group_by_col=['Vertices', 'AvgEdges']
)
plot_metric(
    df,
    x_col='Threads',
    y_col='CostOfComputation',
    title='Cost of computation vs Number of threads',
    xlabel='Number of Threads',
    ylabel='Cost of computation',
    output_path='diagrams/efficiency_nthreads.png',
    group_by_col=['Vertices', 'AvgEdges']
)

path = '../test2.csv'
df = pd.read_csv(path)
plot_metric(
    df[df['Threads'] == 4],
    x_col='AvgEdges',
    y_col='Speedup',
    title='Speedup vs AvgEdges From Vertex (4 threads)',
    xlabel='Average Edges From Vertex',
    ylabel='Speedup',
    output_path='diagrams/speedup_avgedges.png',
    group_by_col=['Vertices']
)
plot_metric(
    df[df['Threads'] == 4],
    x_col='Vertices',
    y_col='Speedup',
    title='Speedup vs Number of vertices (4 threads)',
    xlabel='Number of vertices',
    ylabel='Speedup',
    output_path='diagrams/speedup_vertices.png',
    group_by_col=['AvgEdges']
)