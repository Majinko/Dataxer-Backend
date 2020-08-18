package com.data.dataxer.utils;

import com.data.dataxer.models.enums.DocumentType;
import com.data.dataxer.models.enums.Periods;

public class DefaultInvoiceNumberGenerator {

    public static final String title = "Default invoice number generator";

    public static final String format = "YYYYNNNNNN";

    public static final DocumentType type = DocumentType.INVOICE;

    public static final Periods period = Periods.YEAR;

    public static final boolean isDefault = true;

    public static final String lastNumber = "000000";

}
