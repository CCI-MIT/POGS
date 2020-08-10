package edu.mit.cci.pogs.model.dao.executablescript;

import java.util.Arrays;
import java.util.List;

public enum ScriptType {

    PERPETUAL_INIT_CONDITION('P', "Condition script to start perpetual session"),
    SESSION_WIDE_OVERRIDE('W',"Session wide override script"),
    RECORDED_SESSION_SCRIPT('R', "Recorded session script"),
    SESSION_BEFORE_START('Z',"Before session starts script"),
    SESSION_AFTER_END('X',"After session ends script"),
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
