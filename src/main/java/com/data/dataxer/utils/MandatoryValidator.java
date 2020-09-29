package com.data.dataxer.utils;

import com.data.dataxer.exceptions.MandatoryException;
import com.data.dataxer.models.domain.Cost;

public class MandatoryValidator {

    public static void validateRepeatedCostMandatory(Cost cost) {
        if (cost.getPeriod() == null) {
            throw new MandatoryException("period");
        }
        if (cost.getRepeatedFrom() == null) {
            throw new MandatoryException("repeatedFrom");
        }
        if (cost.getRepeatedTo() == null) {
            throw new MandatoryException("repeatedTo");
        }
    }

}
