import pandas as pd
import matplotlib.pyplot as plt

path = '../test2.csv'
df = pd.read_csv(path)
nthreads = 4
groups = df[df['Threads'] == nthreads].groupby('AvgEdges')

plt.figure(figsize=(10, 6))
for avg_edges, group in groups:
    group_sorted = group.sort_values('Vertices')
    plt.plot(group_sorted['Vertices'], group_sorted['Speedup'], marker='o', label=f'AvgEdges = {avg_edges}')

plt.title('Speedup vs Vertices (4 threads)')
plt.xlabel('Vertices')
plt.ylabel('Speedup')
plt.grid(True)
plt.legend(title='AvgEdges from Vertex')
plt.tight_layout()

plt.savefig('diagrams/diagrams-4gb/speedup_vertices.png', dpi=300)
plt.show()
