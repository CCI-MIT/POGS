package edu.mit.cci.pogs.model.dao.executablescript;

import java.util.Arrays;
import java.util.List;

public enum ScriptType {

    PERPETUAL_INIT_CONDITION('P', "Condition script to start perpetual session"),
    SESSION_WIDE_OVERRIDE('W',"Session wide override script"),
    TEAM_FORMATION_SCRIPT('W',"Team formation script"),
    BEFORE_WORK_SCRIPT('B', "Before work script"),
    AFTER_WORK_SCRIPT('A', "After work script"),
    SCORE_SCRIPT('S', "Score script");

    private Character typeChar;
    private String description;

    ScriptType(Character typeChar, String description){
        this.description = description;
        this.typeChar = typeChar;
    }

    public Character getId(){
        return typeChar;
    }
    public String getDescription(){
        return description;
    }

    public static List<ScriptType> getAllScriptTypes() {
        return Arrays.asList(ScriptType.values());
    }
}
