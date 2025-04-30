import pandas as pd
import matplotlib.pyplot as plt

path = '../test2.csv'
df = pd.read_csv(path)
nthreads = 4
groups = df[df['Threads'] == nthreads].groupby('Vertices')

plt.figure(figsize=(10, 6))
for vertices, group in groups:
    group_sorted = group.sort_values('AvgEdges')
    plt.plot(group_sorted['AvgEdges'], group_sorted['Speedup'], marker='o', label=f'Vertices = {vertices}')

plt.title('Speedup vs AvgEdges From Vertex (4 threads)')
plt.xlabel('Average Edges From Vertex')
plt.ylabel('Speedup')
plt.grid(True)
plt.legend()
plt.tight_layout()

plt.savefig('diagrams/speedup_avgedges.png', dpi=300)
plt.show()
