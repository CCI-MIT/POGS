"""
Score a study

:Author: David Engel
:Email: entrymissing@gmail.com
"""
from Scoring.taskScoringClasses import basicGridTaskEvaluator, brainstormTaskEvaluator, typingTaskEvaluator, gameTaskEvaluator, orderedGridTaskEvaluator, judgementTaskEvaluator, judgementPagesTaskEvaluator, detectionTaskEvaluator, memoryWordsTaskEvaluator
from DataStructures.sessionData import readStudy
from DataStructures.scoringParameters import scoringParameters, combinationParameters
from Scoring.exportNewItems import exportNewGridAnswers
import pandas
import numpy as np

#TODO write docstring and tests
def scoreStudy(settings):
    #read everything
    sessions = readStudy( settings )
    evaluatorClasses = {'basicGridTask':basicGridTaskEvaluator(settings),
                        'orderedGridTask':orderedGridTaskEvaluator(settings),
                        'brainstormTask':brainstormTaskEvaluator(settings),
                        'typingTask':typingTaskEvaluator(settings),
                        'gameTask':gameTaskEvaluator(settings),
                        'judgementTask':judgementTaskEvaluator(settings),
                        'judgementPagesTask':judgementPagesTaskEvaluator(settings),
                        'detectionTask':detectionTaskEvaluator(settings),
                        'memoryWordsTask':memoryWordsTaskEvaluator(settings)
                        }
    parameters = scoringParameters( settings )
    combineParameters = combinationParameters(settings)
    
    #prepare the results variables
    sessionNames = [curSession.sessionName for curSession in sessions]
    taskNames = []
    taskScores = []
    allNewAnswers = {}
    
    #cycle the tasks and score them
    for curTaskParameters in parameters:
        curScoringFunction = curTaskParameters['ScoringFunction']
        taskNames.append(curTaskParameters['TaskName'])
        scores, newAnswers = evaluatorClasses[curScoringFunction].computeScores(sessions, curTaskParameters)
        taskScores.append(scores)
        for curNewAnswer in newAnswers:
            allNewAnswers[curNewAnswer] = newAnswers[curNewAnswer]
        
    #export the new Grid answers
    exportNewGridAnswers(allNewAnswers, settings)
  
    #cycle the tasks that should be combined
    for curCombination in combineParameters:
        newScores = np.zeros(len(taskScores[0]))
        for curSubTask in combineParameters[curCombination]:
            curTaskScores = np.array(taskScores[taskNames.index(curSubTask['TaskName'])],'f')
            if curSubTask['Mean'] == 'False':
                curTaskScores -= np.mean(curTaskScores)
            else:
                curTaskScores -= float(curSubTask['Mean'])

            if curSubTask['Std'] == 'False':
                curTaskScores /= np.std(curTaskScores)
            else:
                curTaskScores /= float(curSubTask['Std'])
            
            print(newScores.shape)
            newScores += curTaskScores / len(combineParameters[curCombination])

        taskNames.append(curCombination)
        taskScores.append(newScores)


    #store everything in a panda dataframe and write it to the results file
    dFrame = pandas.DataFrame(np.array(taskScores).T, columns = taskNames, index = sessionNames)
    dFrame.to_csv(settings['ResultFiles']['scoresFilename'])