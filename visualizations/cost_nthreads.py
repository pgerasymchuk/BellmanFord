import pandas as pd
import matplotlib.pyplot as plt

path = '../test1.csv'
df = pd.read_csv(path)
groups = df.groupby(['Vertices', 'AvgEdges'])

plt.figure(figsize=(10, 6))
for (vertices, avgedges), group in groups:
    plt.plot(group['Threads'], group['CostOfComputation'], marker='o', label=f'Vertices={vertices}, AvgEdges={avgedges}')

plt.title('Cost of Computation vs Threads for Bellman-Ford Algorithm')
plt.xlabel('Threads')
plt.ylabel('Cost of Computation')
plt.grid(True)
plt.legend(title="Graph properties")
plt.xticks(sorted(df['Threads'].unique()))
plt.tight_layout()

plt.savefig('cost_nthreads.png', dpi=300)
plt.show()
