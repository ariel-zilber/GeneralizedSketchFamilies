import numpy as np
import os


def read_results(path):
    with open(path) as f:
        content = f.readlines()
    # you may also want to remove whitespace characters like `\n` at the end of each line
    content = [x.strip() for x in content]
    return content

def calculate_l1_error(path):
    results=read_results(path)
    N=len(results)
    total_diff_l1=0
    for row in results:
        f,e,k_true,k_estimated=row.split("\t")
        diff=abs(int(k_true)-int(k_estimated))/N
        total_diff_l1=total_diff_l1+diff
    return total_diff_l1

## todo create function to display grahs



dir_path = os.path.dirname(os.path.realpath(__file__))

print(calculate_l1_error(dir_path+"\\VSketch\\size\\vBitmap_M_16_u_1_m_10000"))