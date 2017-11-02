package org.glitch.dragoman.ql.listener.groovy;

import org.apache.commons.lang3.StringUtils;
import org.glitch.dragoman.ql.domain.Projection;
import org.glitch.dragoman.ql.listener.AbstractSelectClauseListener;

import java.util.List;

/**
 * Listen to callbacks from our sql parser and uses them to create a Groovy projection implementation.
 */
public class GroovySelectClauseListener extends AbstractSelectClauseListener<String> {
    private static final String TAB = "\t";
    private static final String NEWLINE = "\n";
    private static final String SELECT_STAR = "return incoming;\n";

    private static final String TEMPLATE_SCRIPT = "package org.glitch.dragoman.ql.listener.groovy\n" +
            "import java.util.Map;\n" +
            "class GroovyMapper implements Mapper {\n" +
            "    @Override\n" +
            "    Map<String, Object> map(Object incoming) {\n" +
            "       " + SELECT_STAR +
            "    }\n" +
            "}\n";

    @Override
    public String get() {
        StringBuilder sb = new StringBuilder();
        List<Projection> projections = getProjections();
        if (!projections.isEmpty()) {
            // create the object we are going to populate
            sb.append(tab(1)).append("Map<String, Object> response = new HashMap<>();").append(NEWLINE);

            // add any standard expressions
            sb.append(getExpressions(projections));

            sb.append(tab(2)).append("return response;").append(NEWLINE);
        }

        String s = sb.toString();

        if (StringUtils.isNotBlank(s)) {
            return TEMPLATE_SCRIPT.replace(SELECT_STAR, s);
        } else {
            return TEMPLATE_SCRIPT;
        }
    }

    private String getExpressions(List<Projection> projections) {
        StringBuilder sb = new StringBuilder();
        projections.forEach(p -> {
            // open the line
            sb.append(tab(2)).append("response.put(");
            // add the key
            sb.append("\"").append(p.getName()).append("\"");
            // prepare to add the value
            sb.append(", ");

            sb.append(toAccessor(p));

            // close the line
            sb.append(");").append(NEWLINE);
        });
        return sb.toString();
    }

    private String toAccessor(Projection ctx) {
        return "incoming" + "?." + ctx.getName().replaceAll("\\.", "?.");
    }

    private String tab(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(TAB);
        }
        return sb.toString();
    }
}