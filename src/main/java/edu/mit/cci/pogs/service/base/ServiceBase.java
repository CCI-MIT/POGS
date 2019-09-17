package edu.mit.cci.pogs.service.base;

import java.util.List;

public class ServiceBase {

    public void UpdateResearchGroups(List<Long> toCreate, List<Long> toDelete, List<Long> currentResearchGroups, String[] newSelectedValues){

        for (Long rghau : currentResearchGroups) {
            boolean foundRGH = false;
            for (String researchGroupId : newSelectedValues) {
                if (rghau == new Long(researchGroupId).longValue()) {
                    foundRGH = true;
                }
            }
            if (!foundRGH) {
                toDelete.add(rghau);
            }

        }



        for (String researchGroupId : newSelectedValues) {

            boolean selectedAlreadyIn = false;
            for (Long rghau : currentResearchGroups) {
                if (rghau == new Long(researchGroupId).longValue()) {
                    selectedAlreadyIn = true;
                }
            }
            if (!selectedAlreadyIn) {
                toCreate.add(new Long(researchGroupId));
            }

        }

    }
}
