# DecisionTreeInduction

This repository is aimed at the grouping and explaining of rule based tree induction algorithms.

- [DecisionTreeInduction](#decisiontreeinduction)
  - [Explaining the RBDT-1 algorithm](#explaining-the-rbdt-1-algorithm)

## Explaining the RBDT-1 algorithm

```mermaid
graph LR
C{A_2}
C -->|A_2 = 7| D{A_1}
C -->|A_2 = 8| C_leaf[14]
C -->|A_2 = 9| C_leaf[14]
C -->|A_2 = DC| C_leaf[14]

D -->|A_1 = 4| F{A_3}
D -->|A_1 = 5| G{A_0}
D -->|A_1 = 6| D_leaf[14]
D -->|A_1 = DC| D_leaf[14]

F -->|A_3 = 11| F_leaf_one[14]
F -->|A_3 = 10| F_leaf_two[13]
F -->|A_3 = DC| F_leaf_two[13]

G -->|A_0 = 3| G_leaf_one[15]
G -->|A_0 = DC| G_leaf_one[15]
```
