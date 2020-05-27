yeahfrom textMatchingClasses import typingTextMatcher, typingNumbersMatcher



class typingTaskEvaluator:
	# TODO add docstring and doctest
	def __init__(self, settings):
		self.settings = settings

		# init the typing scorer classes
		self.scoringFunctions = {'typingText': typingTextMatcher(settings),
								 'typingNumbers': typingNumbersMatcher(settings)}

	def computeScores(self, parameters):
		# extract the task name and init the results


		# pick the scoring function
		scoringClass = self.scoringFunctions[parameters['TypingTaskType']]


		curScore = scoringClass.getMatchingScore()

		return curScore