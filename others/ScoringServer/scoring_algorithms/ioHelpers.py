#TODO: Docstring and tests
def importTextFile( filecontent ):
    #read the file, remove empty lines and strip everything
    lines = filecontent.splitlines()
    lines = [curLine.strip() for curLine in lines if curLine.strip()]
    return lines
