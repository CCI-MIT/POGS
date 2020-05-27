import string
from difflib import SequenceMatcher
from ioHelpers import importTextFile

class typingMatcher:
	def __init__(self, settings):
		#copy the settings around and run the init script of the derived classes
		self.settings = settings
		self.initScript()

	def getMatchingScore( self ):
		#import the copy that the group has written up and remove empty lines
		copyLines = importTextFile(self.settings['groupTypedText'])#copyFilename #importTextFile(copyFilename)

		#create the matchlist
		hitCounter, hitSequence = self.matchAll( copyLines )

		#compute the scores
		overallScore, scores = self.computeScores( hitCounter, hitSequence )

		#if there is a report file given create that report file

		#return the overallScore
		return overallScore

	def computeScores( self, hitCounter, hitSequence ):
		flagIgnorePenalty = self.settings['punishError'] # True
		#count the number of words copied
		itemsCopied = sum(hitCounter)

		#number of repeated words
		uniqueItemsCopied = sum([int(curItem>0) for curItem in hitCounter])
		repeatedItems = itemsCopied - uniqueItemsCopied

		#pop the end of the hitCounter to determine the length of the copied text
		while hitCounter and hitCounter[-1] == 0:
			hitCounter.pop()

		#determine the number of Holes
		numberOfHoles = len(hitCounter) - uniqueItemsCopied

		#init the counters for finding the out of place words
		maxItemCount = 0
		numberOfMisplacedItems = 0

		#cycle the hitSequence
		for curSeq in hitSequence:
			if curSeq == 0:
				continue
			if curSeq > maxItemCount:
				curSeq = maxItemCount
			else:
				numberOfMisplacedItems += 1

		#catch groups that didn't do anything and compute the scores for the rest
		if not hitCounter or uniqueItemsCopied == 0:
			score = 0
		else:
		    if flagIgnorePenalty:
		        score = uniqueItemsCopied
		    else:
			    score = uniqueItemsCopied * (1- float(numberOfHoles)/len(hitCounter)) * (1- float(numberOfMisplacedItems)/uniqueItemsCopied)

		#return it all
		return score, [itemsCopied, uniqueItemsCopied, repeatedItems, numberOfHoles, numberOfMisplacedItems]


	def  matchAll( self, copyLines ):
		hitCounter = []
		hitSequence = []

		for curOrg in self.originalLines:
			orgItemCount = self.countItems( curOrg )

			#init the hitCounter for this line and the hitSequence
			curHitCounter  = [0]*orgItemCount
			curHitSequence = [0]*orgItemCount

			#cycle the copied lines
			for (lineCount, curCopy) in enumerate(copyLines):
				#match the copy to the original
				hits = self.matchLines(curOrg, curCopy)

				#check that at least a reasonable amount of words match
				if float(sum(hits)) / float(len(string.split(curCopy))) < 0.5:
					continue

				#cycle the hits
				for (curPos, curHit) in enumerate(hits):
					#check if it is a hit
					if curHit > 0:
						#update the hit counter
						curHitCounter[curPos] = curHitCounter[curPos] + 1

						#update the hitSequence
						if curHitSequence[curPos] == 0:
							curHitSequence[curPos] = lineCount

				#this original line can only be hit once
				#break

			#copy the two things to the overall results list
			hitCounter.append(curHitCounter)
			hitSequence.append(curHitSequence)

		#flatten the lists
		hitCounter = [item for sublist in hitCounter for item in sublist]
		hitSequence = [item for sublist in hitSequence for item in sublist]

		#return the lists
		return hitCounter,hitSequence


	def createReport( self, copyLines, hitCounter, hitSequence, overallScore, scores, reportFile ):
		pass

class typingTextMatcher( typingMatcher ):
	def initScript(self):
		#import and set the parameters
		self.originalLines = importTextFile(self.settings['groundTruth']) #importTextFile(self.settings['Typing']['Ground Truth Text'])
		self.minMatchlength = 2 #self.settings['Typing']['Typing Text Minimal Match Length']
		self.spaceBetweenItems = ' '

	def countItems( self, curOrg ):
		return len(string.split(curOrg))

	#match the copy to the original
	def matchLines(self, original, copy):
		#split it on the whitespaces
		orgWords = string.split(original)
		cpyWords = string.split(copy)

		#init the counters
		orgHitCounter = [0]*len(orgWords)

		#match the words
		s = SequenceMatcher()
		s.set_seq1(orgWords)
		s.set_seq2(cpyWords)
		allMatches = s.get_matching_blocks()

		#cycle the matches
		for curMatch in allMatches:
			#remove atomic matches
			if curMatch[2] >= self.minMatchlength:
				#cycle the indices of the match of the overlap
				for curIdx in range(curMatch[0],curMatch[0] + curMatch[2]):
					#increment the hit counter (which counts how often each word was copied)
					orgHitCounter[curIdx] = orgHitCounter[curIdx] + 1

		#return the hit counter
		return orgHitCounter

	def getItemsFromLine(self, curLine):
		return string.split(curLine)


class typingNumbersMatcher( typingMatcher ):
	def initScript(self):
		#import and set the parameters
		self.originalLines = '' #importTextFile(self.settings['Typing']['Ground Truth Numbers'])
		self.minMatchlength = 3 #self.settings['Typing']['Typing Numbers Minimal Match Length']
		self.spaceBetweenItems = ''

	def countItems( self, curOrg ):
		return len(list(curOrg))

	#match the copy to the original
	def matchLines(self, original, copy):
		#split it on the whitespaces
		orgNumbers = list(original)
		cpyNumbers = list(copy)

		#init the counters
		orgHitCounter = [0]*len(orgNumbers)

		#match the number sequence
		s = SequenceMatcher()
		s.set_seq1(orgNumbers)
		s.set_seq2(cpyNumbers)
		allMatches = s.get_matching_blocks()

		#cycle the matches
		for curMatch in allMatches:
			#remove atomic matches
			if curMatch[2] >= self.minMatchlength:
				#cycle the indices of the match of the overlap
				for curIdx in range(curMatch[0],curMatch[0] + curMatch[2]):
					#increment the hit counter (which counts how often each word was copied)
					orgHitCounter[curIdx] = orgHitCounter[curIdx] + 1

		#return the hit counter
		return orgHitCounter

	def getItemsFromLine(self, curLine):
		return list(curLine)