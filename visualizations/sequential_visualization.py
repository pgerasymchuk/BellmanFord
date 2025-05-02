import pandas as pd
import matplotlib.pyplot as plt
import numpy as np

def plot_with_fit(
        df,
        x_col,
        y_col,
        fit_type='linear',
        title='',
        xlabel='',
        ylabel='',
        output_path='diagram.png'
):
    df_sorted = df.sort_values(x_col)
    x = df_sorted[x_col].values
    y = df_sorted[y_col].values

    deg = 1 if fit_type == 'linear' else 2
    coeffs = np.polyfit(x, y, deg)
    fit_func = np.poly1d(coeffs)

    x_fit = np.linspace(min(x), max(x), 500)
    y_fit = fit_func(x_fit)

    plt.figure(figsize=(10, 6))
    plt.plot(x, y, 'o-', label='Measured Data')
    label_eq = fit_func.__str__().replace('\n', ' ')
    plt.plot(x_fit, y_fit, 'r--', label=f'{fit_type.capitalize()} Fit: {label_eq}')
    plt.title(title)
    plt.xlabel(xlabel)
    plt.ylabel(ylabel)
    plt.grid(True)
    plt.legend()
    plt.tight_layout()
    plt.savefig(output_path, dpi=100)
    plt.show()


df = pd.read_csv('../test2.csv')
plot_with_fit(
    df=df[df['AvgEdges'] == 50],
    x_col='Vertices',
    y_col='Sequential(ms)',
    fit_type='quadratic',
    title='Sequential Time vs Vertices (AvgEdges = 50)',
    xlabel='Number of Vertices',
    ylabel='Sequential Time (ms)',
    output_path='diagrams/seq_time_vertices.png'
)

plot_with_fit(
    df=df[df['Vertices'] == 20000],
    x_col='AvgEdges',
    y_col='Sequential(ms)',
    fit_type='linear',
    title='Sequential Time vs AvgEdges (Vertices = 20000)',
    xlabel='Average Edges From Vertex',
    ylabel='Sequential Time (ms)',
    output_path='diagrams/seq_time_avgedges.png'
)
