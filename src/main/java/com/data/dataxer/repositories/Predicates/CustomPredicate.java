package com.data.dataxer.repositories.Predicates;

import com.data.dataxer.filters.SearchableDates;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.*;

import java.time.LocalDateTime;

import static org.apache.commons.lang3.StringUtils.isNumeric;

public class CustomPredicate<T> {
    private String name;
    private Class<T> tClass;
    private SearchCriteria criteria;

    public CustomPredicate(SearchCriteria param, String name) {
        this.criteria = param;
        this.name = name;
    }

    public BooleanExpression getPredicate() {
        PathBuilder<T> entityPath = new PathBuilder<T>(tClass, name);

        // if value is numeric
        if (isNumeric(criteria.getValue().toString())) {
            NumberPath<Integer> path = entityPath.getNumber(criteria.getKey(), Integer.class);
            int value = Integer.parseInt(criteria.getValue().toString());

            switch (criteria.getOperation()) {
                case ":":
                    return path.eq(value);
                case ">":
                    return path.goe(value);
                case "<":
                    return path.loe(value);
            }
        }

        // if value is in date
        if (SearchableDates.isSearchableDate(criteria.getKey())) {
            DateTimePath<LocalDateTime> path = entityPath.getDateTime(criteria.getKey(), LocalDateTime.class);
            LocalDateTime value = LocalDateTime.parse(criteria.getValue().toString());

            switch (criteria.getOperation()) {
                case ">":
                    return path.goe(value);
                case "<":
                    return path.loe(value);
            }
        }


        StringPath path = entityPath.getString(criteria.getKey());

        if (criteria.getOperation().equalsIgnoreCase(":")) {
            return path.containsIgnoreCase(criteria.getValue().toString());
        }

        return null;
    }
}
