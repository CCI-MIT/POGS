from scoring_algorithms.typing_task_evaluator import typingTaskEvaluator
import json
DEBUG = True
def score_typing_in_colors_task(request_parameters):
	#Get task_execution_attibutes_string and check text typing subtype:
	settings = {}
	
	if request_parameters['completedTaskAttributes'][0] is not None:
		
		typed_text = get_string_value_completed_task_attribute(request_parameters,'fullText')
		
		sections = get_text_sections_by_color_in_order(request_parameters)

		scores = []
		finalscore = 0.0
		full_ground_truth = ''
		finalscoretruth = 0.0
		color_score = []
		counter = 0
		for section in sections:
			full_ground_truth = full_ground_truth + str(section['ground_truth'])
			section_score = (calculate_score_for_text_segment(section['typed_value'],section['ground_truth']))
			finalscore += section_score
			section_ground_score = (calculate_score_for_text_segment(section['ground_truth'],section['ground_truth']))
			finalscoretruth+=section_ground_score
			color_score.append({'color': section['color'],'color_score': section_score, 'ground_text_score': section_ground_score , 'color_index': counter})
			counter +=1
	
		total_typed_score = calculate_score_for_text_segment(typed_text,full_ground_truth )
		finalscore +=total_typed_score
		total_typed_score_ground_truth = calculate_score_for_text_segment(full_ground_truth,full_ground_truth )
		finalscoretruth += total_typed_score_ground_truth
		print finalscoretruth
		normalized_total_score = (finalscore/finalscoretruth)*100

	resp = { 'completedTaskScore': {
		'numberOfProcessedEntries' : 100,
		'numberOfWrongAnswers' : 0, 
		'numberOfRightAnswers' : 0, 
		'completedTaskId' :  request_parameters['completedTaskId'][0],
		'numberOfEntries' :  0, 
		'scoringData' : json.dumps(
			{'colors_score': color_score, 'total_text': {
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

	for section in list(sections):
		#print section
		if section['color'] in colors:
			colors[section['color']]['ground_truth'] = colors[section['color']]['ground_truth'] + section['text']
		else:
			colors[section['color']] = {'ground_truth': section['text'], 'typed_value' : '', 'author': '', 'color': section['color']}
			colorOrder.append(section['color'])

	counter = 0
	finalDict = []
	for se in colorOrder :
		#print se
		colors[se]['author'] = get_string_value_completed_task_attribute(request_parameters, str('subjectAssignedToColor_'+str(counter)))
		colors[se]['typed_value'] = get_string_value_completed_task_attribute(request_parameters,str('fullTextAuthor_'+str(colors[se]['author'])))
		finalDict.append(colors[se])
		counter+=1
	return finalDict

def get_text_sections(request_parameters):
	if request_parameters['taskExecutionAttibutes'][0] != None:
		task_execution_attributes = json.loads(str(request_parameters['taskExecutionAttibutes'][0]))
		for attribute in task_execution_attributes:
			if attribute['attributeName'] == 'gridBluePrint':
				return json.loads(str(attribute['stringValue']))

	return None

def calculate_score_for_text_segment(subject_text,ground_truth):
	settings = {}
	
	#print "["
	#print str(subject_text)
	#print "-"
	#print str(ground_truth)
	#print "]"

	settings['groupTypedText'] = str(subject_text)
	settings['groundTruth'] = str(ground_truth)
		
	evaluator = typingTaskEvaluator(settings)

	parameters = {}
	parameters['TypingTaskType'] = 'typingText'
	return evaluator.computeScores(parameters)