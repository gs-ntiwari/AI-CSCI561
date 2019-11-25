import copy
import utils

class KB:
    def __init__(self):
        self.allPredicates = set()
        self.sentences = []
        self.positivesSentences = {}
        self.negativeSentences = {}
        self.resolvedSentences={}
        self.groundedSentences={}
        self.unifiedSentence=set()

    def TELL(self, idx, sentence):
        self.sentences.append(sentence)
        predicate_terms = utils.getAllPredicates(sentence)
        isSentenceAllConstant = utils.isSentenceAllConstants(sentence)
        for entry in predicate_terms:
            predicate = entry[0]

            if isSentenceAllConstant:
                if predicate not in self.groundedSentences:
                    self.groundedSentences[predicate]=set([idx])
                else:
                    self.groundedSentences[predicate].add(idx)
            else:
                if (predicate not in self.allPredicates):
                    self.allPredicates.add(predicate)
                    if (utils.isNegatedPredicate(predicate)):
                        self.negativeSentences[predicate[1:]] = set([idx])
                    else:
                        self.positivesSentences[predicate] = set([idx])
                else:
                    if (utils.isNegatedPredicate(predicate)):
                        self.negativeSentences[predicate[1:]].add(idx)
                    else:
                        self.positivesSentences[predicate].add(idx)

    def ASK(self, query):
        query=utils.negateQuery(query)
        self.TELL( len(self.sentences),query)
        usedSentences = {}
        return self.applyResolution(query, usedSentences)

    def applyResolution(self, top_query, usedSentences):
        query_queue = []
        query_queue.append(top_query)
        visited_queries = set()
        visited_queries.add(Query(top_query))

        while(query_queue):
            query = query_queue.pop(0)
            constUnifyList, varUnifyList = self.getUnifiableSentences(query)
            #used_sentences_local = copy.deepcopy(usedSentences)

            if (not constUnifyList and not varUnifyList):
                continue
            resolveList={}
            for predicate in constUnifyList.keys():
                for i in constUnifyList[predicate]:
                    #print('------', (i, query), '......')
                    '''if query in usedSentences:
                        if i in set(usedSentences[query]):
                            continue'''
                    newSentence, num_vars, allConstants = self.resolve(predicate, self.sentences[i], query)
                    # print variablesOnly
                    # print("new sentence after resolving query", query, "with sentence", self.sentences[i], "is", newSentence)
                    if (newSentence == "CANNOT RESOLVE"):
                        continue
                    if (newSentence == ""):
                        return True
                    if num_vars not in resolveList:
                        resolveList[num_vars] = []
                    resolveList[num_vars].append((newSentence, num_vars, i))

                    '''if allConstants:
                        resolveList.insert(0,(newSentence, num_vars, i))
                    else:
                    resolveList.append((newSentence, num_vars, i))'''

                '''for currentTuple in resolveList:'''


            for predicate in varUnifyList.keys():
                for i in varUnifyList[predicate]:
                    #print('------', (i, query), '......')
                    '''if query in usedSentences:
                        if i in set(usedSentences[query]):
                            continue'''
                    newSentence, num_vars, allConstants = self.resolve(predicate, self.sentences[i], query)
                    # print variablesOnly
                    # print("new sentence after resolving query", query, "with sentence", self.sentences[i], "is", newSentence)
                    if (newSentence == "CANNOT RESOLVE"):
                        continue
                    if (newSentence == ""):
                        return True
                    if num_vars not in resolveList:
                        resolveList[num_vars] = []
                    resolveList[num_vars].append((newSentence, num_vars, i))

                    '''if allConstants:
                        resolveList.insert(0,(newSentence, num_vars, i))
                    else:
                    resolveList.append((newSentence, num_vars, i))'''

                '''for currentTuple in resolveList:'''
            for key in sorted(resolveList):
                for currentTuple in resolveList[key]:
                    newSentence=currentTuple[0]
                    i=currentTuple[2]
                    '''if not query in usedSentences:
                        usedSentences[query] = [i]
                    else:
                        usedSentences[query].append(i)'''
                    '''if (not variablesOnly):
                        if (not self.isLiteral(i)):
                            if not query in usedSentences:
                                usedSentences[query]=[i]
                            else:
                                usedSentences[query].append(i)'''

                    #if newSentence in self.resolvedSentences:
                        #answer=self.resolvedSentences[newSentence]
                    #else:
                        #answer = self.applyResolution(newSentence, usedSentences)
                    if Query(newSentence) not in visited_queries:
                        query_queue.append(newSentence)
                        visited_queries.add(Query(newSentence))
                        #self.resolvedSentences[newSentence]=answer

                    #if (answer):
                    #    return True
                    '''if (not variablesOnly):
                        if (not self.isLiteral(i)):
                            indices=usedSentences[query]
                            indices = indices[:-1]
                            usedSentences[query]=indices'''
                    '''indices = usedSentences[query]
                    indices = indices[:-1]
                    usedSentences[query] = indices'''
            query_queue.sort(key=utils.numUniqueVars)

        return False

    def getUnifiableSentences(self, currSentence):
        sentences = currSentence.split("|")
        ConstMap={}
        VarMap={}
        for sentence in sentences:
            '''if Query(sentence) in self.unifiedSentence:
                continue'''
            predicate, terms=utils.getPredicateAndTerms(sentence)
            if utils.negateQuery(predicate) in self.groundedSentences:
                groundedSentences= self.groundedSentences[utils.negateQuery(predicate)]
                for groundedSentenceIndex in groundedSentences:
                    allPredicates=utils.getAllPredicates(self.sentences[groundedSentenceIndex])
                    for entry in allPredicates:
                        entry_predicates = entry[0]
                        entry_terms = entry[1]
                        if entry_predicates==utils.negateQuery(predicate) and utils.areTermsEqual(terms, entry_terms):
                            if predicate in ConstMap:
                                ConstMap[predicate].add(groundedSentenceIndex)
                            else:
                                ConstMap[predicate] = set([groundedSentenceIndex])



            if predicate not in VarMap:
                    sentencesIndices=None
                    if utils.isNegatedPredicate(predicate):
                        if predicate[1:] in self.positivesSentences.keys():
                            sentencesIndices=self.positivesSentences[predicate[1:]]
                    else:
                        if predicate in self.negativeSentences.keys():
                            sentencesIndices = self.negativeSentences[predicate]
                    if sentencesIndices is not None:
                        VarMap[predicate]=sentencesIndices
            self.unifiedSentence.add(Query(sentence))
        return ConstMap, VarMap



    def standardizeAllVariables(self):
        sentenceCount=0
        for currSentence in self.sentences:
            predicates= currSentence.split('|')
            for j in range(len(predicates)):
                predicate, terms=utils.getPredicateAndTerms(predicates[j])
                for i in range(len(terms)):
                    if utils.isVariable(terms[i]):
                        terms[i]=terms[i]+str(sentenceCount)
                arguments = ",".join(terms)
                predicates[j]=predicate+"("+arguments+")"
            sentence=" | ".join(predicates)
            self.sentences[sentenceCount]=sentence
            sentenceCount+=1


    def unify(self, terms1, terms2):
        substitution = {}
        #predicate1, terms1=utils.getPredicateAndTerms(sentence1)
        #predicate2, terms2 = utils.getPredicateAndTerms(sentence2)
        if (len(terms1) != len(terms2)):
            return substitution

        g = Graph()


        for i in range(len(terms1)):
            terms1[i] = terms1[i].strip()
            terms2[i] = terms2[i].strip()

            g.addEdge(terms1[i], terms2[i])

            '''if (utils.isVariable(terms1[i])):
                if (utils.isConstant(terms2[i])):
                    # term1 has a variable, term2 has a constant - substitute
                    if (terms1[i] in substitution):
                        if (terms2[i] == substitution[terms1[i]]):
                            substitution[terms1[i]] = terms2[i]
                        else:
                            return {}
                    else:
                        substitution[terms1[i]] = terms2[i]
                else:
                    # both are variables
                    if (not terms1[i] in substitution and not terms2[i] in substitution):
                        substitution[terms1[i]] = terms2[i]
                    if terms1[i] in substitution and terms2[i] != substitution[terms1[i]]:
                        return {}

            else:
                if (utils.isVariable(terms2[i])):
                    # term1 is a constant, term2 is a variable - substitute
                    if (terms2[i] in substitution):
                        if (terms1[i] == substitution[terms2[i]]):
                            substitution[terms2[i]] = terms1[i]
                        else:
                            return {}
                    else:
                        substitution[terms2[i]] = terms1[i]
                else:
                    # both are constants
                    if (terms1[i] == terms2[i]):
                        substitution[terms2[i]] = terms1[i]
                    else:
                        return {}'''
        all_eq_sets = g.findAllSpanningTrees()


        for eq_set in all_eq_sets:
            sub = findSubstitution(eq_set)
            if not sub:
                return {}
            else:
                for key in sub.keys():
                    substitution[key] = sub[key]

        return substitution

    '''def unifySentences(self, sentence1, sentence2, orgPredicate):
        predicates1=sentence1.split('|')
        predicates2=sentence2.split('|')
        for currPredicate in predicates1:
            if currPredicate.strip()==orgPredicate:'''



    def resolve(self, orgPredicate, sentence1, sentence2):
        predicates1 = sentence1.split("|")
        predicates2 = sentence2.split("|")

        variables1=set()
        variables2=set()

        unify_term1 = []
        unify_term2 = []

        structured_1 = []
        structured_2 = []
        for i in range(len(predicates1)):
            currPredicate,terms = utils.getPredicateAndTerms(predicates1[i])
            structured_1.append(terms)
            for term in terms:
                if utils.isVariable(term):
                    variables1.add(term)
            if (currPredicate == utils.negateQuery(orgPredicate)):
                unify_term1.append(i)

        for i in range(len(predicates2)):
            currPredicate,terms = utils.getPredicateAndTerms(predicates2[i])
            structured_2.append(terms)
            for term in terms:
                if utils.isVariable(term):
                    variables2.add(term)
            if (currPredicate == orgPredicate):
                unify_term2.append(i)

        # Create variable name such that there is no intersection between
        # the names used in sentence 1 and 2
        intersectionSet=variables1.intersection(variables2)
        variableNames = variables1.union(variables2)
        mapToUpdateVariableNames={}
        for var in intersectionSet:
            newName = var
            count=0
            while newName in variableNames:
                newName = "a" + str(count)
                count+=1
            mapToUpdateVariableNames[var] = newName
            variableNames.add(newName)

        for term_tuple in structured_2:
            for i in range(len(term_tuple)):
                if term_tuple[i] in mapToUpdateVariableNames:
                    term_tuple[i] = mapToUpdateVariableNames[term_tuple[i]]

        subst_ans = False
        done_i = -1
        done_j = -1
        for i in range(len(unify_term1)):
            for j in range(len(unify_term2)):
                substitution = self.unify(structured_1[unify_term1[i]], structured_2[unify_term2[j]])
                if (substitution):
                    subst_ans = True
                    done_i = i
                    done_j = j
                    break

        if (not subst_ans):
            return "CANNOT RESOLVE", True, True

        resolvent = ""
        allPredicates=set()
        predicates1.pop(unify_term1[done_i])
        predicates2.pop(unify_term2[done_j])
        structured_1.pop(unify_term1[done_i])
        structured_2.pop(unify_term2[done_j])
        all_const=True
        # print terms1
        # print terms2
        uniqueVars=set()
        for i in range(len(predicates1)):
            currResolvent=""
            t = predicates1[i].strip()
            start = t.index("(")
            currResolvent = currResolvent + t[:start + 1]
            #variables = t[start + 1:-1].split(",")
            variables=structured_1[i]
            for j in range(len(variables)):
                v = variables[j]
                if (v in substitution):
                    currResolvent = currResolvent + substitution[v]
                    if(utils.isVariable(substitution[v])):
                        all_const = False
                        uniqueVars.add(substitution[v])
                else:
                    currResolvent = currResolvent + v
                    if utils.isVariable(v):
                        all_const=False
                        uniqueVars.add(v)
                if (j == len(variables) - 1):
                    currResolvent = currResolvent + ")"
                else:
                    currResolvent = currResolvent + ","
            allPredicates.add(currResolvent)
            if (i != len(predicates1) - 1):
                resolvent = resolvent+ currResolvent + " | "
            else:
                resolvent = resolvent+currResolvent

        for i in range(len(predicates2)):
            currResolvent=""
            if (i == 0 and resolvent != ""):
                resolvent = resolvent + " | "
            t = predicates2[i].strip()
            start = t.index("(")
            currResolvent = currResolvent + t[:start + 1]
            #variables = t[start + 1:-1].split(",")
            variables = structured_2[i]
            for j in range(len(variables)):
                v = variables[j]
                if (v in substitution):
                    currResolvent = currResolvent + substitution[v]
                    if (utils.isVariable(substitution[v])):
                        uniqueVars.add(substitution[v])
                else:
                    currResolvent = currResolvent + v
                    if utils.isVariable(v):
                        uniqueVars.add(v)
                if (j == len(variables) - 1):
                    currResolvent = currResolvent + ")"
                else:
                    currResolvent = currResolvent + ","
            allPredicates.add(currResolvent)
            if (i != len(predicates2) - 1):
                resolvent = resolvent+currResolvent + " | "
            else:
                resolvent = resolvent+currResolvent
        allPredicates=set(allPredicates)
        resolvent1="|".join(allPredicates)

        return resolvent1, len(uniqueVars), all_const


    def isLiteral(self, sentenceNum):
        sentence = self.sentences[sentenceNum]
        sentences = sentence.split("|")
        for currSentence in sentences:
            predicate, terms=utils.getPredicateAndTerms(currSentence)
            for term in terms:
                if (utils.isVariable(term)):
                    return False
        return True



    def applyResolutionTemp(self, currSentence, usedSentences):
        filteredSentences= self.getUnifiableSentences(currSentence)
        if not filteredSentences:
            return False
        for key,value in filteredSentences.items():
            for i in value:
                unifiedSentence=self.unifySentences(currSentence,self.sentences[i])
                if unifiedSentence=="":
                    return True
                elif unifiedSentence is None:
                    continue
                else:
                    usedSentences.append(i)
                    result=self.applyResolutionTemp(unifiedSentence, usedSentences)
                    if result:
                        return result
                    else:
                        usedSentences.pop(-1)

class Graph:
    def __init__(self):
        self.nodes = set()
        self.adjacency = {}

    def addEdge(self, t1, t2):
        self.nodes.add(t1)
        self.nodes.add(t2)
        if t1 not in self.adjacency:
            self.adjacency[t1] = set()
        if t2 not in self.adjacency:
            self.adjacency[t2] = set()

        self.adjacency[t1].add(t2)
        self.adjacency[t2].add(t1)

    def findSpanningTree(self, t, visited):
        for ng in self.adjacency[t]:
            if ng not in visited:
                visited.add(ng)
                self.findSpanningTree(ng, visited)
        return visited

    def findAllSpanningTrees(self):
        visitedSoFar = set()
        spanSet = []
        for node in self.nodes:
            if node not in visitedSoFar:
                visited = set()
                visited.add(node)
                ngbd = self.findSpanningTree(node, visited)
                spanSet.append(ngbd)
                for n in ngbd:
                    visitedSoFar.add(n)
        return spanSet

# Find the substitution map given an equivalence set.
def findSubstitution(eq_set):
    # Find if there is a constant or more than one constant
    currentConstant = None
    for t in eq_set:
        if utils.isConstant(t):
            if currentConstant == None:
                currentConstant = t
            else:
                # More than one constants that are different
                return {}
    sub_map = {}
    leader = currentConstant
    if leader == None:
        # Choose first variable as leader
        leader = eq_set.pop()
    sub_map[leader] = leader
    for t in eq_set:
        if utils.isVariable(t):
            sub_map[t] = leader
    return sub_map


class Query:
    def __init__(self, query):
        self.query = query

    def __eq__(self, other):
        return utils.areQueriesEqual(self.query, other.query)

    def __hash__(self):
        return hash(self.query)

    def __ne__(self, other):
        return not self.__eq__(other)







    '''def unifySentences(self, sentence1, sentence2):
        sentences1= sentence1.split('|')
        sentences2= sentence2.split('|')

        unifiedSentence=None

        mappedVariables={}

        for currSentence1 in sentences1:
            predicate1, terms1= utils.getPredicateAndTerms(currSentence1)
            for currSentence2 in sentences2:
                predicate2, terms2 = utils.getPredicate(currSentence2)
                if predicate1 != predicate2:
                    continue
                if len(terms1)!=len(terms2):
                    return "FAIL"
                for i in range(len(terms1)):
                    if utils.isVariable(terms2[i]):
                        if utils.isConstant(terms1[i]):'''



