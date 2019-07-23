package edu.mit.cci.pogs.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

public class StringUtils {

    public static final String[] GREEK_LETTER_NAMES = {
                "alpha",
                "beta",
                "gamma",
                "delta",
                "epsilon",
                "zeta",
                "eta",
                "theta",
                "iota",
                "kappa",
                "lambda",
                "mu",
                "nu",
                "xi",
                "omicron",
                "pi",
                "rho",
                "sigma",
                "tau",
                "upsilon",
                "phi",
                "chi",
                "psi",
                "omega"
    };

    public static String[] getUniqueNamesOfSize(int size){

        Random random = new Random();

        Map<String,Integer> selectedNamesMaps = new LinkedHashMap<>();
        int counter = 0;
        while(selectedNamesMaps.size()!= size) {
            if(selectedNamesMaps.size()>GREEK_LETTER_NAMES.length){
                counter ++;
            }
            int chosenIndex = random.nextInt(GREEK_LETTER_NAMES.length);
            String chosenName = GREEK_LETTER_NAMES[chosenIndex];
            if(counter!=0){
                chosenName = chosenName + counter;
            }
            if(selectedNamesMaps.get(chosenName)== null){
                selectedNamesMaps.put(chosenName,1);
            }

        }
        return selectedNamesMaps.keySet().toArray(new String[selectedNamesMaps.keySet().size()]);
    }
}
