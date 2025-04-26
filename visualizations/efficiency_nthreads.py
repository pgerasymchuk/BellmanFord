import pandas as pd
import matplotlib.pyplot as plt

path = '../test1.csv'
df = pd.read_csv(path)
groups = df.groupby(['Vertices', 'AvgEdges'])

plt.figure(figsize=(10, 6))
for (vertices, avgedges), group in groups:
    plt.plot(group['Threads'], group['Efficiency'], marker='o', label=f'Vertices={vertices}, AvgEdges={avgedges}')

plt.title('Efficiency vs Number of threads ')
plt.xlabel('Number of threads')
plt.ylabel('Efficiency')
plt.grid(True)
plt.legend(title="Graph properties")
plt.xticks(sorted(df['Threads'].unique()))
plt.ylim(0, 1.1)
plt.tight_layout()

plt.savefig('efficiency_nthreads.png', dpi=300)
plt.show()
