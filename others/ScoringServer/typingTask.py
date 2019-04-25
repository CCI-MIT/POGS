from scoring_algorithms.typing_task_evaluator import typingTaskEvaluator
import json
DEBUG = False

def score_typing_task(request_parameters):
	#Get task_execution_attibutes_string and check text typing subtype:
	has_groud_truth = False
	if request_parameters['dictionaryHasGroundTruth'] != None and request_parameters['dictionaryHasGroundTruth'][0] == "true":
		has_groud_truth = True

	if has_groud_truth:
		return calculate_text_has_ground_truth(request_parameters)
	else:
		return calculate_text_does_not_have_ground_truth(request_parameters)

def calculate_text_has_ground_truth(request_parameters):
	settings = {}
	
	if request_parameters['completedTaskAttributes'][0] != None and request_parameters['dictionaryEntries'][0] != None:
		
		typed_text = extract_typed_text_from_completed_task_attributes(request_parameters)

		dictionary_entries = json.loads(str(request_parameters['dictionaryEntries'][0]))
		
		ground_truth_dictionary_entry = dictionary_entries[0]['entryValue']


		settings['groupTypedText'] = str(typed_text)
		settings['groundTruth'] = str(ground_truth_dictionary_entry)
		
		evaluator = typingTaskEvaluator(settings)

		parameters = {}
		parameters['TypingTaskType'] = 'typingText'
		finalscore = evaluator.computeScores(parameters)
	


	resp = { 'completedTaskScore': {
		'numberOfProcessedEntries' : 100,
		'numberOfWrongAnswers' : 0, 
		'numberOfRightAnswers' : 0, 
		'completedTaskId' :  request_parameters['completedTaskId'][0],
		'numberOfEntries' :  0, 
		'totalScore' : finalscore
	}}
	return resp
def extract_typed_text_from_completed_task_attributes(request_parameters):
	completed_task_attributes = json.loads(str(request_parameters['completedTaskAttributes'][0]))
	typed_text = ''
	for attribute in completed_task_attributes:
		if attribute['attributeName'] == 'fullText':
			return attribute['stringValue']

def calculate_text_does_not_have_ground_truth(request_parameters):


	if request_parameters['completedTaskAttributes'][0] != None and request_parameters['dictionaryEntries'][0] != None:
			
		typed_text = extract_typed_text_from_completed_task_attributes(request_parameters)
		dictionary_entries = json.loads(str(request_parameters['dictionaryEntries'][0]))
		
		answer_text = typed_text
		answers_lines = answer_text.splitlines()
		
		all_dictionary_entries = dictionary_entries

		right_categories_found = {}
		entries_processed = 0
		total_entries =  len(answers_lines)
		not_found_entries = []
		# for each right add points and wrong ignore
		found = False
		#print "Answser has : " + str(len(answers_lines)) + " entries"
		for answer in list(answers_lines):
			found = False
			##print("Dictionary has : " + str(len(all_dictionary_entries)))
			for dict_entries in list(all_dictionary_entries):
				category_entries = dict_entries['entryValue'].splitlines()
				##print("Dict entry has category with : "+str(len(category_entries))+" entries")
				for ent in list(category_entries):
					#print("Comparing : " + answer.lower().strip() + " - ")
					if answer.lower().strip() == ent.lower().strip():
						found = True
						entries_processed = entries_processed + 1
						if dict_entries[ 'entryType'] == 'C':
							##print "Found correct category  "
							right_categories_found[dict_entries['entryCategory']] = 1
						else:
							print "Found wrong category  "
						break
				if found:
					break
			# if not found add to not_found_entry
			if not found:
				not_found_entries.append({'entryValue': answer})

		final_score = 0
		for key, value in right_categories_found.iteritems():
			final_score = final_score + 1



	resp = { 'completedTaskScore': {
		'numberOfProcessedEntries' : entries_processed,
		'numberOfWrongAnswers' : len(not_found_entries), 
		'numberOfRightAnswers' : (total_entries - len(not_found_entries)), 
		'completedTaskId' :  request_parameters['completedTaskId'][0],
		'numberOfEntries' :  total_entries, 
		'totalScore' : final_score
	}, 'unprocessedEntries' : not_found_entries}
	return resp