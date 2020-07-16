from scoring_algorithms.typing_task_evaluator import typingTaskEvaluator
from HTMLParser import HTMLParser
import json
DEBUG = True
def score_typing_in_colors_task(request_parameters):
	#Get task_execution_attibutes_string and check text typing subtype:
	settings = {}
	
	if request_parameters['completedTaskAttributes'][0] is not None:
		
		typed_text = get_string_value_completed_task_attribute(request_parameters,'fullText') #fullTextHTML
		
		sections = get_text_sections_by_color_in_order(request_parameters)

		#print sections
		scores = []
		finalscore = 0.0
		full_ground_truth = ''
		finalscoretruth = 0.0
		color_score = []
		counter = 0
		h = HTMLParser()
		for section in sections:
			if section['typed_value'] is None:
				section['typed_value'] = ""
			full_ground_truth = full_ground_truth + str(section['ground_truth'])
			unescape = h.unescape(section['typed_value'])
			section_score = (calculate_score_for_text_segment(False,unescape,section['ground_truth']))
			#print section_score
			finalscore += section_score
			section_ground_score = (calculate_score_for_text_segment(False,section['ground_truth'],section['ground_truth']))
			finalscoretruth+=section_ground_score
			aut = section['author']
			if aut == None:
				aut = ''
			color_score.append({'color': section['color'],'author': aut, 'score': section_score, 'ground_text_score': section_ground_score , 'color_index': counter})
			counter +=1
	
		total_typed_score = calculate_score_for_text_segment(True,h.unescape(typed_text),full_ground_truth )
		finalscore +=total_typed_score
		total_typed_score_ground_truth = calculate_score_for_text_segment(True,full_ground_truth,full_ground_truth )
		finalscoretruth += total_typed_score_ground_truth
		#print finalscoretruth
		if finalscoretruth != 0:
		    normalized_total_score = (finalscore/finalscoretruth)*100
		else:
		    normalized_total_score = finalscore

	resp = { 'completedTaskScore': {
		'numberOfProcessedEntries' : 100,
		'numberOfWrongAnswers' : 0, 
		'numberOfRightAnswers' : 0, 
		'completedTaskId' :  request_parameters['completedTaskId'][0],
		'numberOfEntries' :  0, 
		'scoringData' : json.dumps(
			{'individual_subject_scores': color_score, 'total_text': {
				'total_text_score' : total_typed_score,
			 	'total_text_ground_truth_score':total_typed_score_ground_truth },
			 'final_score_number': finalscore,
			 'final_score_ground_truth_number': finalscoretruth,
			 'normalized_total_score': normalized_total_score}),
		'totalScore' : normalized_total_score
	}}
	return resp


def get_string_value_completed_task_attribute(request_parameters, attribute_name):
	completed_task_attributes = json.loads(str(request_parameters['completedTaskAttributes'][0]))
	for attribute in completed_task_attributes:
		if attribute['attributeName'] == attribute_name:
			return attribute['stringValue']

def get_attribute_completed_task_attribute(request_parameters, attribute_name):
	completed_task_attributes = json.loads(str(request_parameters['completedTaskAttributes'][0]))
	for attribute in completed_task_attributes:
		if attribute['attributeName'] == attribute_name:
			return attribute

def get_text_sections_by_color_in_order(request_parameters):
	sections = get_text_sections(request_parameters)
	colors = {}
	colorOrder = []
	#print "GET TXT SECTIONS BY COLOR "
	for section in list(sections):
		#print section
		if section['color'] in colors:
			colors[section['color']]['ground_truth'] = str(colors[section['color']]['ground_truth']) + str(get_dict_value(request_parameters,section['text']))
		else:
			colors[section['color']] = {'ground_truth': str(get_dict_value(request_parameters,section['text'])), 'typed_value' : '', 'author': '', 'color': section['color']}
			colorOrder.append(section['color'])

	counter = 0
	finalDict = []
	h = HTMLParser()
	print " ((()()()()()()"
	for se in colorOrder :
		#print se
		colors[se]['author'] = get_string_value_completed_task_attribute(request_parameters, str('subjectAssignedToColor_'+str(counter)))
		colors[se]['typed_value'] = get_string_value_completed_task_attribute(request_parameters,str('fullTextAuthor_'+str(colors[se]['author'])))
		#print colors[se]['author']
		#print " ########################################################################################################################"
		#print h.unescape(colors[se]['typed_value'])
		#print " ########################################################################################################################"
		#print colors[se]['ground_truth']
		finalDict.append(colors[se])
		counter+=1

	return finalDict
def get_dict_value(request_parameters, id):
	if request_parameters['dictionaryEntries'][0] != None:
		task_execution_attributes = json.loads(str(request_parameters['dictionaryEntries'][0]))
		for attribute in task_execution_attributes:
			if str(attribute['id']) == id:
				return attribute['entryValue']

	return None
def get_text_sections(request_parameters):
	if request_parameters['taskExecutionAttibutes'][0] != None:
		task_execution_attributes = json.loads(str(request_parameters['taskExecutionAttibutes'][0]))
		for attribute in task_execution_attributes:
			if attribute['attributeName'] == 'gridBluePrint':
				return json.loads((attribute['stringValue']).encode('utf-8'))

	return None

def calculate_score_for_text_segment(punish_error,subject_text,ground_truth):
	settings = {}
	
	#print "["
	#print str(subject_text)
	#print "-"
	#print str(ground_truth)
	#print "]"

	settings['groupTypedText'] = (subject_text).encode('utf-8')
	settings['groundTruth'] = (ground_truth).encode('utf-8')
	settings['punishError'] = punish_error
	evaluator = typingTaskEvaluator(settings)

	parameters = {}
	parameters['TypingTaskType'] = 'typingText'

	return evaluator.computeScores(parameters)