package com.data.dataxer.filters;

import com.querydsl.core.types.Ops;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Filter {

    public static final String GREATER_THAN = "GT";
    public static final String EQUAL_GREATER_THAN = "EGT";
    public static final String LOWER_THAN = "LT";
    public static final String EQUAL_LOWER_THAN = "ELT";
    public static final String EQUAL = "E";
    public static final String BETWEEN = "BT";
    public static final String NOT_EQUAL = "NE";
    public static final String LIKE = "L";

    private String columnId;
    private String operator;
    private List<String> values;

    public Filter(String jsonFilter) {
        JsonParser parser = JsonParserFactory.getJsonParser();
        Map<String, Object> parsedFilter = parser.parseMap(jsonFilter);
        this.columnId = parsedFilter.get("columnId").toString();
        this.operator = parsedFilter.get("operator").toString();
        this.values = Arrays.asList(parsedFilter.get("values").toString().split(","));
    }

    public Ops resolveOperator() {
        switch(this.operator) {
            case GREATER_THAN: return Ops.GT;
            case EQUAL_GREATER_THAN: return Ops.GOE;
            case LOWER_THAN: return Ops.LT;
            case EQUAL_LOWER_THAN: return Ops.LOE;
            case BETWEEN: return Ops.BETWEEN;
            case LIKE: return Ops.LIKE;
            case NOT_EQUAL: return Ops.NE;
            case EQUAL:
            default: return Ops.EQ;
        }
    }

    public String getColumnId() {
        return columnId;
    }

    public String getOperator() {
        return operator;
    }

    public List<String> getValues() {
        return values;
    }
}
