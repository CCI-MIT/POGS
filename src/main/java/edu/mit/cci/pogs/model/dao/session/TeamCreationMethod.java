package edu.mit.cci.pogs.model.dao.session;

public enum TeamCreationMethod {

    RESEARCHER('S', "Specified by Researcher"),
    RANDOMLY_SPECIFIC_SIZE('C', "Randomly selected - multiple groups of a single specific size"),
    RANDOMLY_MATRIX('E', "Randomly selected - multiple groups of multiple sizes (based on a matrix)");


    private Character teamCreationMethodChar;
    private String teamCreationMethod;

    TeamCreationMethod(Character teamCreationMethodChar, String teamCreationMethod) {
        this.teamCreationMethod = teamCreationMethod;
        this.teamCreationMethodChar = teamCreationMethodChar;
    }

    public Character getId() {
        return teamCreationMethodChar;
    }

    public String getTeamCreationMethod() {
        return teamCreationMethod;
    }

}
