import copy
import utils

class KB:

    def __init__(self, sentenceStrings):
        self.positives = {}
        self.negatives = {}
        self.conclusions_positives={}
        self.conclusions_negatives={}
        self.predicates = []
        self.conclusions={}
        self.premises_positives={}
        self.premises_negatives = {}
        self.sentences = copy.deepcopy(sentenceStrings)

    # get all predicates separated and store in positive/negative tables
    def TELL(self):
        for i in range(len(self.sentences)):
            string = self.sentences[i]
            self.addToKB(string, i)
        self.standardiseVariables()

    def addToKB(self, sentence, i):
        if utils.isImplication(sentence):
            brokenSentence = sentence.split("=>")
            premise=brokenSentence[0]
            allTerms= premise.split('&')
            for term in allTerms:
                self.extractPredicate(term,i, isPremise=True)
            conclusion=brokenSentence[1]
            self.extractPredicate(i, conclusion, isConclusion=True)
        else:
            self.extractPredicate(i, sentence)

    def extractPredicate(self, i, sentence, isConclusion=False, isPremise=False):
        s = self.getPredicate(sentence)
        if ("~" in s):
            if (s not in self.predicates):
                self.predicates.append(s)
                self.negatives[utils.negateQuery(s)] = [i]
            else:
                self.positives[utils.negateQuery(s)].append(i)
        else:
            if (s not in self.predicates):
                self.predicates.append(s)
                self.positives[s] = [i]
            else:
                self.positives[s].append(i)

    def standardiseVariables(self):
        for i in range(len(self.sentences)):
            sentence = self.sentences[i]
            if utils.isImplication(sentence):
                brokenSentence = sentence.split("=>")
                premise = brokenSentence[0]
                conclusion= brokenSentence[1]
                terms = premise.split("&")
                terms.append(conclusion)

                self.sentences[i] = ""
                for k in range(len(terms)):
                    terms[k] = terms[k].strip()
                    start = terms[k].index("(")
                    end = terms[k].index(")")

                    variables = terms[k][start + 1:end].split(",")
                    terms[k] = terms[k][:start + 1]

                    for j in range(len(variables)):
                        if (variables[j].islower()):
                            variables[j] = variables[j] + str(i)
                        terms[k] = terms[k] + variables[j]
                        if (j == len(variables) - 1):
                            terms[k] = terms[k] + ")"
                        else:
                            terms[k] = terms[k] + ","

                    self.sentences[i] = self.sentences[i] + terms[k]
                    if (k != len(terms) - 1):
                        self.sentences[i] = self.sentences[i] + " => "

    def getPredicate(self, x):
        x = x.strip()
        end = x.index("(")
        s = x[:end]
        return s

    def negate(self, s):
        if ("~" in s):
            ans = s[1:]
        else:
            ans = "~" + s
        return ans

    def getUnifiableList(self, query):
        unify_list = {}
        query = query.strip()
        predicate = self.getPredicate(query)
        toSearch = self.negateSentence(predicate)
        # if ~ means not, means negative, means unify with positives
        if ("~" in p):
            if (toSearch in self.positives.keys()):
                unify_list[p] = self.positives[toSearch]
        else:
            if (toSearch in self.negatives.keys()):
                unify_list[p] = self.negatives[self.negateSentence(p)]
        return unify_list

    def unify(self, term1, term2):
        substitution = {}
        term1 = term1.strip()
        term2 = term2.strip()
        start1 = term1.index("(")
        start2 = term2.index("(")
        vars1 = term1[start1 + 1:-1].split(",")
        vars2 = term2[start2 + 1:-1].split(",")
        if (len(vars1) != len(vars2)):
            return substitution

        for i in range(len(vars1)):
            vars1[i] = vars1[i].strip()
            vars2[i] = vars2[i].strip()

            if (vars1[i].islower()):
                if (not vars2[i].islower()):
                    # term1 has a variable, term2 has a constant - substitute
                    if (vars1[i] in substitution):
                        if (vars2[i] == substitution[vars1[i]]):
                            substitution[vars1[i]] = vars2[i]
                        else:
                            return {}
                    else:
                        substitution[vars1[i]] = vars2[i]
                else:
                    # both are variables
                    if (not vars1[i] in substitution and not vars2[i] in substitution):
                        substitution[vars1[i]] = vars2[i]
            else:
                if (vars2[i].islower()):
                    # term1 is a constant, term2 is a variable - substitute
                    if (vars2[i] in substitution):
                        if (vars1[i] == substitution[vars2[i]]):
                            substitution[vars2[i]] = vars1[i]
                        else:
                            return {}
                    else:
                        substitution[vars2[i]] = vars1[i]
                else:
                    # both are constants
                    if (vars1[i] == vars2[i]):
                        substitution[vars2[i]] = vars1[i]
                    else:
                        return {}
        return substitution

    def resolve(self, sent1, sent2, p):
        print
        "Sentence 1: " + sent1
        terms1 = sent1.split("&")
        # print terms1

        print
        "Sentence 2: " + sent2
        print
        "Predicate: " + p
        terms2 = sent2.split("|")
        # print terms2
        # print "\n"

        unify_term1 = []
        unify_term2 = []
        for i in range(len(terms1)):
            p_test = self.getPredicate(terms1[i])
            if (p_test == self.negateSentence(p)):
                # unify_term1 = i
                # break
                unify_term1.append(i)
        for i in range(len(terms2)):
            p_test = self.getPredicate(terms2[i])
            if (p_test == p):
                # unify_term2 = i
                # break
                unify_term2.append(i)
        # print terms1[unify_term1]
        # print terms2[unify_term2]
        # print "\n"

        subst_ans = False
        done_i = -1
        done_j = -1
        for i in range(len(unify_term1)):
            for j in range(len(unify_term2)):
                substitution = self.unify(terms1[unify_term1[i]], terms2[unify_term2[j]])
                if (substitution):
                    subst_ans = True
                    done_i = i
                    done_j = j
                    break

        print
        substitution
        if (not subst_ans):
            return "CANNOT RESOLVE", True

        all_vars = True
        for x in substitution:
            if (not substitution[x].islower()):
                all_vars = False
                break

        resolvent = ""
        terms1.pop(unify_term1[done_i])
        terms2.pop(unify_term2[done_j])
        # print terms1
        # print terms2
        for i in range(len(terms1)):
            t = terms1[i].strip()
            start = t.index("(")
            resolvent = resolvent + t[:start + 1]
            variables = t[start + 1:-1].split(",")
            for j in range(len(variables)):
                v = variables[j]
                if (v in substitution):
                    resolvent = resolvent + substitution[v]
                else:
                    resolvent = resolvent + v
                if (j == len(variables) - 1):
                    resolvent = resolvent + ")"
                else:
                    resolvent = resolvent + ","
            if (i != len(terms1) - 1):
                resolvent = resolvent + " | "

        for i in range(len(terms2)):
            if (i == 0 and resolvent != ""):
                resolvent = resolvent + " | "
            t = terms2[i].strip()
            start = t.index("(")
            resolvent = resolvent + t[:start + 1]
            variables = t[start + 1:-1].split(",")
            for j in range(len(variables)):
                v = variables[j]
                if (v in substitution):
                    resolvent = resolvent + substitution[v]
                else:
                    resolvent = resolvent + v
                if (j == len(variables) - 1):
                    resolvent = resolvent + ")"
                else:
                    resolvent = resolvent + ","
            if (i != len(terms2) - 1):
                resolvent = resolvent + " | "

        if (resolvent == ""):
            return "FALSE", True
        else:
            return resolvent, all_vars

    def ASK(self, query):
        self.sentences.append(query)
        self.addSentenceToKB(query, len(self.sentences) - 1)
        done = []
        return self.resolution(query, done)

    def onlyConstants(self, sentenceNum):
        sentence = self.sentences[sentenceNum]
        terms = sentence.split("|")
        for term in terms:
            term = term.strip()
            start = term.index("(")
            variables = term[start + 1:-1].split(",")
            for v in variables:
                if (v.islower()):
                    return False
        return True

    def resolution(self, query, done):
        print
        "QUERY: " + query
        print
        "DONE: " + str(done)
        local_done = copy.deepcopy(done)
        unify_list = self.getUnifiableList(query)
        if (not unify_list):
            return False
        for predicate in unify_list:
            for sentenceNum in unify_list[predicate]:
                if (sentenceNum in done):
                    continue
                newSentence, variablesOnly = self.resolve(self.sentences[sentenceNum], query, predicate)
                # print variablesOnly
                if (newSentence == "CANNOT RESOLVE"):
                    continue
                if (newSentence == "FALSE"):
                    return True
                if (not variablesOnly):
                    if (not self.onlyConstants(sentenceNum)):
                        local_done.append(sentenceNum)

                answer = self.resolution(newSentence, local_done)
                if (answer):
                    return True
                if (not variablesOnly):
                    if (not self.onlyConstants(sentenceNum)):
                        local_done.pop()
        return False


if __name__ == '__main__':
    numOfQueries, numberOfSentences, queries, sentences=utils.readFile("input8.txt")
    sentences = utils.removeImplication(sentences)
    kb=KB(sentences)
    kb.standardiseVariables()
    result=[]
    for query in queries:
        kb.TELL()
        kb.negate(query)
        curr_result=kb.ASK(query)
        result.append(curr_result)
    utils.writeResult(result)
