import KB
import utils

if __name__ == '__main__':
    query1='F(N,M)'
    query2 ='~F(N,M)'
    print(KB.areQueriesEqual(query1, query2))

    # Expected answer is true
    print(utils.isSentenceAllConstants("Faster(Bob,Nisha)"))
    print(not utils.isSentenceAllConstants("Faster(Bob,Nisha) | Faster(x,y)"))

