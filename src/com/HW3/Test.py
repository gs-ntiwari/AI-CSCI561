import utils
from KB import KB
if __name__ == '__main__':
    import glob
    txt_files = glob.glob("/Users/nishatiwari/PycharmProjects/AIHW3/input*.txt")
    #20,15 - Two predicates on the right side of implication
    # FailedInput42
    # input8
    #print(txt_files)
    for file in txt_files:
        print(file)
        numOfQueries, numberOfSentences, queries, sentences = utils.readFile(file)
        resultForQueries = []
        sentences = utils.removeImplication(sentences)
        result = []
        for query in queries:
            kb = KB()
            for idx, sentence in enumerate(sentences):
                kb.TELL(idx, sentence)
            kb.standardizeAllVariables()
            result.append(kb.ASK(query))
        print(file, result)