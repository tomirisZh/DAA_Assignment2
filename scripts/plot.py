import pandas as pd
import matplotlib.pyplot as plt
from pathlib import Path

df = pd.read_csv("results/results.csv")
out = Path("results/plots")
out.mkdir(parents=True, exist_ok=True)

cases = [
    ("W1", "-", "W1: Random access (10 000 get)"),
    ("W2", "-", "W2: Search (1 000 contains)"),
    ("W3", "head", "W3: Insert/Remove at head"),
    ("W3", "middle", "W3: Insert/Remove at middle"),
    ("W4", "-", "W4: MinHeap insert + extractMin"),
]

for wl, var, title in cases:
    d = df[(df.workload == wl) & (df.variant == var)]
    name = f"{wl}_{var}".replace("-", "all")

    # График 1: время от n
    fig, ax = plt.subplots(figsize=(7, 4.5))
    for s, g in d.groupby("structure"):
        ax.plot(g.n, g.time_ms, marker="o", label=s)
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("n (number of elements)")
    ax.set_ylabel("Median time (ms)")
    ax.set_title(title + " - time")
    ax.grid(True, which="both", alpha=0.3)
    ax.legend()
    fig.tight_layout()
    fig.savefig(out / f"{name}_time.png", dpi=150)
    plt.close(fig)

    # График 2: steps / moves / comparisons от n
    fig, ax = plt.subplots(figsize=(7, 4.5))
    for s, g in d.groupby("structure"):
        for col, ls in [("steps", "-"), ("moves", "--"), ("comparisons", ":")]:
            g2 = g[g[col] > 0]
            if len(g2):
                ax.plot(g2.n, g2[col], marker="o", linestyle=ls, label=f"{s}: {col}")
    ax.set_xscale("log")
    ax.set_yscale("log")
    ax.set_xlabel("n (number of elements)")
    ax.set_ylabel("Operations (count)")
    ax.set_title(title + " - counters")
    ax.grid(True, which="both", alpha=0.3)
    ax.legend(fontsize=8)
    fig.tight_layout()
    fig.savefig(out / f"{name}_ops.png", dpi=150)
    plt.close(fig)

print("Plots saved to", out)