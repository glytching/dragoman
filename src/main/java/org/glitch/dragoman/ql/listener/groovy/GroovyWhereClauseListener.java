package org.glitch.dragoman.ql.listener.groovy;

import org.glitch.dragoman.ql.domain.Predicate;
import org.glitch.dragoman.ql.listener.AbstractWhereClauseListener;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * Listens to callbacks from our sql parser and uses them to create a Groovy filter implementation.
 */
public class GroovyWhereClauseListener extends AbstractWhereClauseListener<String> {
    private static final String GROOVY_SCRIPT_TEMPLATE =
            "package org.glitch.dragoman.ql.listener.groovy\n" +
                    "class GroovyFilter implements Filter {\n" +
                    "    @Override\n" +
                    "    boolean filter(Object incoming) {\n" +
                    "        boolean isSame = true\n" +
                    "        return isSame\n" +
                    "    }\n" +
                    "}\n";

    @Override
    public String get() {
        String expression = getExpression();
        if (isEmpty(expression)) {
            return GROOVY_SCRIPT_TEMPLATE;
        } else {
            return GROOVY_SCRIPT_TEMPLATE.replace("true", expression);
        }
    }

    private String getExpression() {
        StringBuilder sb = new StringBuilder();
        List<Predicate> predicates = getPredicates();
        for (Predicate element : predicates) {
            String name = "incoming?." + element.getLhs().replaceAll("\\.", "?.");
            StringBuilder inner = new StringBuilder();

            if (element.isBetween()) {
                inner.append(name);
                inner.append(" >= ").append(element.getRhs().get(0));
                inner.append(" && ");
                inner.append(name);
                inner.append(" < ").append(element.getRhs().get(1));
            } else if (element.isNotBetween()) {
                inner.append("!(");
                inner.append(name);
                inner.append(" >= ").append(element.getRhs().get(0));
                inner.append(" && ");
                inner.append(name);
                inner.append(" < ").append(element.getRhs().get(1));
                inner.append(")");
            } else if (element.isLike()) {
                inner.append(name);
                inner.append("=~");
                inner.append(toValue(element.isQuoted(), asYouLikeIt(element.getRhs().get(0))));
            } else if (element.isNotLike()) {
                inner.append("!(");
                inner.append(name);
                inner.append("=~");
                inner.append(toValue(element.isQuoted(), asYouLikeIt(element.getRhs().get(0))));
                inner.append(")");
            } else if (element.isNull()) {
                inner.append(name);
                inner.append("==null");
            } else if (element.isNotNull()) {
                inner.append("!(");
                inner.append(name);
                inner.append("==null");
                inner.append(")");
            } else if (element.isIn()) {
                inner.append(name);
                inner.append(" in [");
                for (int i = 0; i < element.getRhs().size(); i++) {
                    if (i > 0) {
                        inner.append(",");
                    }
                    inner.append(toValue(element.isQuoted(), element.getRhs().get(i)));
                }
                inner.append("]");
            } else if (element.isNotIn()) {
                inner.append("!(");
                inner.append(name);
                inner.append(" in [");
                for (int i = 0; i < element.getRhs().size(); i++) {
                    if (i > 0) {
                        inner.append(",");
                    }
                    inner.append(toValue(element.isQuoted(), element.getRhs().get(i)));
                }
                inner.append("]");
                inner.append(")");
            } else if (element.isEquals()) {
                inner.append(name);
                inner.append("==");
                inner.append(toValue(element.isQuoted(), element.getRhs().get(0)));
            } else {
                inner.append(name);
                inner.append(element.getOperator());
                inner.append(toValue(element.isQuoted(), element.getRhs().get(0)));
            }
            inner.append(" && ");

            sb.append(inner.toString());
        }

        return sb.length() > 4 ? sb.toString().substring(0, (sb.length() - 4)) : sb.toString();
    }

    private Object toValue(boolean quoted, Object value) {
        if (quoted) {
            return "\"" + value + "\"";
        } else {
            return value;
        }
    }

    private String asYouLikeIt(String operand) {
        // deal with the leading wildcard (if any)
        if (operand.indexOf("%") == 0) {
            // nothing more we can do here just strip it
            operand = operand.replaceFirst("%", "");
        }

        // deal with the trailing wildcard (if any)
        if (operand.indexOf("%") == operand.length() - 1) {
            // nothing more we can do here just strip it
            operand = replaceLast(operand, "%", "");
        }
        return operand;
    }

    @SuppressWarnings("SameParameterValue")
    private String replaceLast(String operand, String substring, String replacement) {
        int index = operand.lastIndexOf(substring);

        return operand.substring(0, index) + replacement + operand.substring(index + substring.length());
    }
}