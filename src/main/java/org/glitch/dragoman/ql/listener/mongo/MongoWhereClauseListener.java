package org.glitch.dragoman.ql.listener.mongo;

import com.google.common.collect.Lists;
import com.mongodb.client.model.Filters;
import org.bson.BsonDocument;
import org.bson.conversions.Bson;
import org.glitch.dragoman.ql.domain.Predicate;
import org.glitch.dragoman.ql.listener.AbstractWhereClauseListener;
import org.glitch.dragoman.web.exception.InvalidRequestException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.String.format;

public class MongoWhereClauseListener extends AbstractWhereClauseListener<Bson> {
    private static final Pattern DATE_PATTERN = Pattern.compile("\\d{4}-\\d\\d-\\d\\d.*");

    @Override
    public Bson get() {
        List<Predicate> predicates = getPredicates();

        List<Bson> filters = Lists.newArrayListWithExpectedSize(predicates.size());
        for (Predicate predicate : predicates) {
            if (predicate.isEquals()) {
                filters.add(Filters.eq(predicate.getLhs(), toOperand(predicate.getRhs().get(0))));
            } else if (predicate.isNotEquals()) {
                filters.add(Filters.ne(predicate.getLhs(), toOperand(predicate.getRhs().get(0))));
            } else if (predicate.isGreaterThan()) {
                filters.add(Filters.gt(predicate.getLhs(), toOperand(predicate.getRhs().get(0))));
            } else if (predicate.isGreaterThanOrEqualTo()) {
                filters.add(Filters.gte(predicate.getLhs(), toOperand(predicate.getRhs().get(0))));
            } else if (predicate.isLessThan()) {
                filters.add(Filters.lt(predicate.getLhs(), toOperand(predicate.getRhs().get(0))));
            } else if (predicate.isLessThanOrEqualTo()) {
                filters.add(Filters.lte(predicate.getLhs(), toOperand(predicate.getRhs().get(0))));
            } else if (predicate.isBetween()) {
                // 'x between 5 and 10' _generally_ means: x >= 5 and x < 10
                filters.add(Filters.gte(predicate.getLhs(), toOperand(predicate.getRhs().get(0))));
                filters.add(Filters.lt(predicate.getLhs(), toOperand(predicate.getRhs().get(1))));
            } else if (predicate.isIn()) {
                filters.add(Filters.in(predicate.getLhs(), toOperands(predicate.getRhs())));
            } else if (predicate.isNotIn()) {
                filters.add(Filters.nin(predicate.getLhs(), toOperands(predicate.getRhs())));
            } else if (predicate.isLike()) {
                filters.add(Filters.regex(predicate.getLhs(), toRegex(predicate.getRhs().get(0))));
            } else if (predicate.isNotLike()) {
                filters.add(Filters.not(Filters.regex(predicate.getLhs(), toRegex(predicate.getRhs().get(0)))));
            } else if (predicate.isNull()) {
                filters.add(Filters.eq(predicate.getLhs(), null));
            } else if (predicate.isNotNull()) {
                filters.add(Filters.ne(predicate.getLhs(), null));
            }
        }
        if (filters.isEmpty()) {
            // this is the no-op case
            return new BsonDocument();
        } else {
            return Filters.and(filters.toArray(new Bson[filters.size()]));
        }
    }

    private List<Object> toOperands(List<String> incoming) {
        List<Object> outgoing = Lists.newArrayListWithExpectedSize(incoming.size());
        for (String s : incoming) {
            outgoing.add(toOperand(s));
        }
        return outgoing;
    }

    @SuppressWarnings("EmptyCatchBlock")
    private Object toOperand(String incoming) {
        if ("true".equalsIgnoreCase(incoming)) {
            return true;
        }
        if ("false".equalsIgnoreCase(incoming)) {
            return false;
        }

        // might be numeric
        try {
            return Integer.parseInt(incoming);
        } catch (NumberFormatException ex) {
        }
        try {
            return Long.parseLong(incoming);
        } catch (NumberFormatException ex) {
        }
        try {
            return Double.parseDouble(incoming);
        } catch (NumberFormatException ex) {
        }

        if (isDateTimeLiteral(incoming)) {
            return toDate(incoming);
        }

        // it really is a String!
        return incoming;
    }

    @SuppressWarnings("EmptyCatchBlock")
    private Date toDate(String incoming) {
        try {
            return Date.from(LocalDateTime.parse(incoming).toInstant(ZoneOffset.UTC));
        } catch (DateTimeParseException e) {
        }

        try {
            return Date.from(LocalDate.parse(incoming).atStartOfDay().toInstant(ZoneOffset.UTC));
        } catch (DateTimeParseException e) {
        }

        throw InvalidRequestException.create(
                format("Failed to parse date/time literal: %s, you must supply date/time literals in ISO8601 format!",
                        incoming));
    }

    private boolean isDateTimeLiteral(String incoming) {
        return DATE_PATTERN.matcher(incoming).matches();
    }


    private Pattern toRegex(String operand) {
        // find({"name": "a"}) <-- like '%a%'
        // find({"name": "^pa"}) <-- like 'pa%'
        // find({"name": "ro$"}) <-- like '%ro'

        operand = escapeSpecialCharacters(operand);
        // deal with the leading wildcard (if any)
        if (operand.indexOf("%") == 0) {
            // nothing more we can do here just strip it
            operand = operand.replaceFirst("%", "");
        } else {
            // doesn't start with a wildcard so let's anchor it
            operand = "^" + operand;
        }

        // deal with the trailing wildcard (if any)
        if (operand.indexOf("%") == operand.length() - 1) {
            // nothing more we can do here just strip it
            operand = replaceLast(operand, "%", "");
        } else {
            operand = operand + "$";
        }
        return Pattern.compile(operand);
    }

    private String escapeSpecialCharacters(String operand) {
        if (operand.contains("*")) {
            operand = operand.replace("*", "\\*");
        }
        if (operand.contains("+")) {
            operand = operand.replace("+", "\\+");
        }
        if (operand.contains("$")) {
            operand = operand.replace("$", "\\$");
        }
        return operand;
    }

    @SuppressWarnings("SameParameterValue")
    private String replaceLast(String operand, String substring, String replacement) {
        int index = operand.lastIndexOf(substring);

        return operand.substring(0, index) + replacement + operand.substring(index + substring.length());
    }
}
