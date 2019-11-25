from KB import KB
import utils
if __name__ == '__main__':
    numOfQueries, numberOfSentences, queries, sentences=utils.readFile("finput72.txt")
    #input71, 72
    resultForQueries=[]
    sentences = utils.removeImplication(sentences)
    result = []
    for query in queries:
        kb=KB()
        for idx, sentence in enumerate(sentences):
            kb.TELL(idx, sentence)
        kb.standardizeAllVariables()
        result.append(kb.ASK(query))

    utils.writeResult(result)