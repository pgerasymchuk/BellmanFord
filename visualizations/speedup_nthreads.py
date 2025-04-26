import pandas as pd
import matplotlib.pyplot as plt

path = '../test1.csv'
df = pd.read_csv(path)
groups = df.groupby(['Vertices', 'AvgEdges'])

plt.figure(figsize=(10, 6))
for (vertices, avgedges), group in groups:
    plt.plot(group['Threads'], group['Speedup'], marker='o', label=f'Vertices={vertices}, AvgEdgesFromVertex={avgedges}')

plt.title('Speedup vs Number of threads')
plt.xlabel('Number of threads')
plt.ylabel('Speedup')
plt.grid(True)
plt.legend(title="Graph properties")
plt.xticks(group['Threads'].unique())
plt.tight_layout()

plt.savefig('speedup_nthreads.png', dpi=300)
plt.show()