
import re
def readFile(filename):
    with open(filename) as fp:
        line = fp.readline()
        queries = []
        KB = []
        cnt = 0
        n=0
        while line:
            #print("Line {}: {}".format(cnt, line.strip()))
            line=line.rstrip()
            if cnt==0:
                n = int(line)
            elif cnt <=n:
                queries.append(line)
            elif cnt==(n+1):
                k=int(line)
            else:
                KB.append(line)
            cnt += 1
            line = fp.readline()

    return n,k,queries, KB

def writeResult(result):
    i=0
    fp=open('/Users/nishatiwari/PycharmProjects/AIHW3/output/output.txt','w+')
    for currResult in result:
        if currResult:
            currResult="TRUE"
        else:
            currResult = "FALSE"
        fp.write(currResult)
        if len(result)!=i+1:
            fp.write("\n")
        i+=1

def writeResultTemp(result, file):
    i=0
    fp=open('/Users/nishatiwari/PycharmProjects/AIHW3/output/output.txt','a')
    for currResult in result:
        if currResult:
            currResult="TRUE"
        else:
            currResult = "FALSE"
        fp.write(currResult)
        if len(result)!=i+1:
            fp.write("\n")
        i+=1


def isVariableOrConstant(x):
    if (x[0].isupper()):
        return True
    elif (x[0].islower()):
        return False


def isImplication(sentence):
    return "=>" in sentence

def updateSentence(sentence):
    finalSentence=""
    brokenSentence=sentence.split("=>")
    premise=brokenSentence[0]
    conclusion=brokenSentence[1]
    allPredicates= premise.split("&")
    for predicate in allPredicates:
        predicate=predicate.strip()
        if predicate[0]=='~':
            finalSentence+=predicate[1:]
        else:
            finalSentence+='~'
            finalSentence+=predicate
        finalSentence+=' | '
    finalSentence+=conclusion
    return finalSentence


def removeImplication(sentences):
    result=[]
    for sentence in sentences:
        if isImplication(sentence):
            result.append(updateSentence(sentence))
        else:
            result.append(sentence)
    return result

def isNegatedPredicate(predicate):
    return "~" in predicate

def getPredicate(sentence):
    sentence = sentence.strip()
    end = sentence.index("(")
    p = sentence[:end]
    return p

def getPredicateAndTerms(sentence):
    sentence = sentence.strip()
    start = sentence.index("(")
    p = sentence[:start]
    end = sentence.index(")")
    terms=sentence[start+1: end].split(",")
    return p, terms

def negateQuery(query):
    if (isNegatedPredicate(query)):
        ans = query[1:]
    else:
        ans = "~" + query
    return ans

def isVariable(argument):
    return argument[0].islower()

def isConstant(argument):
    return argument[0].isupper()

def allConstants(terms):
    for term in terms:
        if isVariable(term):
            return False
    return True

def getAllPredicates(sentence):
    predicates = sentence.strip().split('|')
    result=[]
    for predicate in predicates:
        predicateName, terms=getPredicateAndTerms(predicate)
        result.append((predicateName,terms))
    return result

def isSentenceAllConstants(sentence):
    predicates_terms = getAllPredicates(sentence)
    for entry in predicates_terms:
        if not allConstants(entry[1]):
            return False
    return True


def areTermsEqual(term1, term2):
    if len(term1)!=len(term2):
        return False
    for i in range(len(term1)):
        if isConstant(term1[i]) and term1[i] != term2[i]:
            return False
    return True

def areQueriesEqual(query1, query2):
    query1Predicates=getAllPredicates(query1)
    query2Predicates=getAllPredicates(query2)

    if len(query1Predicates) != len(query2Predicates):
        return False
    mapPredicate1={}
    for currPredicate in query1Predicates:
        if currPredicate[0] in mapPredicate1:
            mapPredicate1[currPredicate[0]].append(currPredicate[1])
        else:
            mapPredicate1[currPredicate[0]]=[currPredicate[1]]

    for currPredicate in query2Predicates:
        if currPredicate[0] not in mapPredicate1:
            return False
        else:
            matched=False
            for i in range(len(mapPredicate1[currPredicate[0]])):
                term=mapPredicate1[currPredicate[0]][i]
                if areTermsEqual(term, currPredicate[1]):
                    matched=True
                    mapPredicate1[currPredicate[0]].pop(i)
                    break
            if not matched:
                return False
    return True

def numUniqueVars(query):
    unique_vars = set()
    predicate_terms = getAllPredicates(query)
    for entry in predicate_terms:
        terms = entry[1]
        for term in terms:
            if isVariable(term):
                unique_vars.add(term)
    return len(unique_vars)

def compareQueriesByNumVars(query1, query2):
    return numUniqueVars(query1) < numUniqueVars(query2)



