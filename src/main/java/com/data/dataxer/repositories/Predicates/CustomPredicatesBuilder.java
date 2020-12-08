package com.data.dataxer.repositories.Predicates;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class CustomPredicatesBuilder<T> {
    private String name;
    private List<SearchCriteria> params;

    public CustomPredicatesBuilder(String name) {
        this.name = name;
        this.params = new ArrayList<>();
    }

    public BooleanExpression build() {
        if (params.size() == 0) {
            return null;
        }

        List<BooleanExpression> predicates = params.stream().map(param -> {
            CustomPredicate<T> predicate = new CustomPredicate<T>(param, name);
            return predicate.getPredicate();
        }).collect(Collectors.toList());

        BooleanExpression result = Expressions.asBoolean(true).isTrue();

        for (BooleanExpression predicate : predicates) {
            result = result.and(predicate);
        }

        return result;
    }

    public BooleanExpression parsePattern(String search) {
        if (search != null) {
            Pattern pattern = Pattern.compile("(\\w+?)(:|<|>)(\\w+?),");
            Matcher matcher = pattern.matcher(search + ",");

            while (matcher.find()) {
                this.params.add(new SearchCriteria(matcher.group(1), matcher.group(2), matcher.group(3)));
            }

            return this.build();
        }

        return null;
    }

}
