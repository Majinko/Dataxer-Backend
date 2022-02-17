package com.data.dataxer.models.enums;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum CategoryType {
    // FIRM
    STUFF_SERVICE_FOR_COMPANY("stuff_service_for_company"), // Tovar / služna pre chod firmy
    TIME_FOR_COMPANY("time_for_company"), // Časový záznam pre chod firmy
    STUFF_SERVICE_NOT_IN_COST("stuff_service_not_in_cost"), // Tovar / služba pre firmu nezahrnutá do nákladov

    // PROJECT
    STUFF_SERVICE_IN_PROJECT_BALANCE("stuff_service_in_project_balance"), // Tovar / služba zákazky započítaná do bilancie zákazky
    STUFF_SERVICE_IN_OPERATING_COST_COMPANY("stuff_service_in_operating_cost_company"), // Tovar / služba zákazky započítaná do prevádzkových nákladov firmy
    TIME_CAPITAL_IN_PROJECT("time_capital_in_project"), // Časové záznamy hlavných činností na zákazke
    TIME_NO_CAPITAL_IN_PROJECT("time_no_capital_in_project"), // Časové záznamy vedľajších činností na zákazke
    TIME_AFTER_PROJECT_END("time_after_project_edn"), // Časové záznamy servisných činností po ukončení zákazky

    // SALARY
    SALARY_FOR_COMPANY("salary_for_company"), // Mzdy prevádzka firmy
    SALARY_FOR_PROJECT("salary_for_project"), // Mzdy zákazky

    // TYPE_PROJECT
    TYPE_PROJECT_CATEGORY_SUBCATEGORY_SEARCH("type_project_category_subcategory_search"), // Vytvorte si kategórie a podkategórie zákazok

    // CONTACT
    TYPE_CONTACT("type_contact"); // Kontakty sa automaticky zaradia podla toho, ci od nich mame naklad (vtedy budú automaticky v dodavateloch), ked im budeme fakturovat, budu automaticky v klientoch. Niektori mozu byt v oboch. Ale mali byt sme tam mat moznost pridat dalsie kategorie kontaktov, kde si ulozim kontakty na veci, ktore su napriklad statne institucie a podobne.

    public final String categoryType;

    CategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public static List<CategoryType> getManHoursTypes() {
        return new ArrayList<>(
                Arrays.asList(
                        STUFF_SERVICE_FOR_COMPANY,
                        TIME_FOR_COMPANY,
                        STUFF_SERVICE_IN_OPERATING_COST_COMPANY,
                        TIME_NO_CAPITAL_IN_PROJECT,
                        TIME_AFTER_PROJECT_END
                )
        );
    }

    public static List<CategoryType> getTimeTypes() {
        return new ArrayList<>(
                Arrays.asList(
                        TIME_CAPITAL_IN_PROJECT,
                        TIME_FOR_COMPANY
                )
        );
    }
}
