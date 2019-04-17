from Scoring.statsFunctions import princomp
import pandas
from DataStructures.scoringParameters import ciComputationParameters
import numpy as np

#TODO docstrings and doctests
#TODO maybe refactor to separate computation from loading
def computeCI(settings):
    #load the dataframe and the scoring parameters
    dFrame = pandas.DataFrame.from_csv(settings['ResultFiles']['scoresFilename'])
    latentFile = open(settings['ResultFiles']['portionExplainedFilename'], 'w')
    ciParameters = ciComputationParameters(settings)
    
    
    for curCIName in ciParameters:
        #select the tasks from the scoring parameters and their weights
        data = []
        weights = []
        taskNames = []
    
        #load the data, normalize the task scores and apply the weight    
        for curSubTask in ciParameters[curCIName]:
            subTaskName = curSubTask['TaskName']
            curWeight = curSubTask['Weight']
            taskNames.append(subTaskName)
            
            curData = np.array(dFrame[subTaskName], 'f')
            curData -= np.mean(curData)
            curData /= np.std(curData)
            
            data.append(curData)
            weights.append(curWeight)
    
        
        #see if we have weights given (i.e. all weights can be converted to floats )
        try:
            weights = [float(curWeight) for curWeight in weights]
        except:
            weights = False
        
        #if no weights are given we do the PCA
        if not weights:
            #make it into an array and compute the pincipal component analysis
            data = np.array(data)
            (coeff,score,latent) = princomp(data.T)
            
            #compute the portion explained by each factor
            poritonExplained = [str(curPortion) for curPortion in sorted(latent/sum(latent), reverse = True)]
            latentFile.write(curCIName + ',' + ','.join(poritonExplained) + '\n')

            #make sure the main factor points in the positive direction
            if coeff[0,0] < 0:
                score = -score
                coeff = -coeff
        
            #write out the coefficients if we've been given a filename
            if settings['ResultFiles']['factorAnalysisFilename']:
                factorNames = ['Factor{}'.format(curID+1) for curID in range(coeff.shape[0])]
                coeffDFrame = pandas.DataFrame(coeff.astype('float'), columns = factorNames, index = taskNames)
                outFileName = settings['ResultFiles']['factorAnalysisFilename'][:-4] + curCIName + '.csv'
                coeffDFrame.to_csv( outFileName )

            #extract the scores along the first principle component which is groupIQ
            ciScores = score[0,:]

        #otherwise we do weighted averages
        else:
            data = [curData*curWeight for curData,curWeight in zip(data,weights)]
            ciScores = np.sum(data,0)
        
        #add the ciScores to the data frame
        dFrame[curCIName] = ciScores

    #write out the updated scores and the latent scores (i.e. how much variance was explained by each parameter)
    dFrame.to_csv(settings['ResultFiles']['scoresFilename'])
