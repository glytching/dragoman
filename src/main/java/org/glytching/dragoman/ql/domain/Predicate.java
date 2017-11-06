/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glytching.dragoman.ql.domain;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Class representation of an {@code where} expression.
 */
public class Predicate {

    private String lhs;
    private String operator;
    private final List<String> rhs;
    private boolean negated;
    private boolean quoted;

    public Predicate() {
        rhs = Lists.newArrayList();
    }

    public Predicate(String lhs, String operator, String... rhs) {
        this(lhs, operator, false, false, rhs);
    }

    @SuppressWarnings("SameParameterValue")
    Predicate(String lhs, String operator, boolean quoted, boolean negated, String... rhs) {
        this.lhs = lhs;
        this.operator = operator;
        this.quoted = quoted;
        this.negated = negated;
        this.rhs = Lists.newArrayList(rhs);
    }

    public String getLhs() {
        return lhs;
    }

    public void appendLhsPart(String lhsPart) {
        if (isNotBlank(lhs)) {
            this.lhs = this.lhs + "." + lhsPart;
        } else {
            this.lhs = lhsPart;
        }
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public String getOperator() {
        return this.operator;
    }

    public List<String> getRhs() {
        return rhs;
    }

    public void addRhs(String rhsValue) {
        if (isQuoted(rhsValue)) {
            quoted = true;
            rhsValue = rhsValue.substring(1, rhsValue.length() - 1);
        }
        rhs.add(rhsValue);
    }

    public boolean isQuoted() {
        return quoted;
    }

    private boolean isQuoted(String value) {
        return value.matches("'.*'");
    }

    public boolean isNegated() {
        return negated || "!=".equals(operator);
    }

    @SuppressWarnings("UnusedReturnValue")
    public Predicate negate() {
        this.negated = true;
        return this;
    }

    public void betweenOperator() {
        this.operator = (negated ? "not " : "").concat("between");
    }

    public void nullOperator() {
        this.operator = (negated ? "is not " : "is ").concat("null");
    }

    public void inOperator() {
        this.operator = (negated ? "not " : "").concat("in");
    }

    public boolean isGreaterThan() {
        return ">".equals(operator);
    }

    public boolean isGreaterThanOrEqualTo() {
        return ">=".equals(operator);
    }

    public boolean isLessThan() {
        return "<".equals(operator);
    }

    public boolean isLessThanOrEqualTo() {
        return "<=".equals(operator);
    }

    public boolean isEquals() {
        return "=".equals(operator);
    }

    public boolean isNotEquals() {
        return "!=".equals(operator);
    }

    public boolean isIn() {
        return "in".equalsIgnoreCase(operator);
    }

    public boolean isNotIn() {
        return "not in".equalsIgnoreCase(operator);
    }

    public boolean isBetween() {
        return "between".equalsIgnoreCase(operator);
    }

    public boolean isNotBetween() {
        return "not between".equalsIgnoreCase(operator);
    }

    public boolean isLike() {
        return "like".equalsIgnoreCase(operator);
    }

    public boolean isNotLike() {
        return "not like".equalsIgnoreCase(operator);
    }

    public boolean isNull() {
        return "is null".equalsIgnoreCase(operator);
    }

    public boolean isNotNull() {
        return "is not null".equalsIgnoreCase(operator);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

    @SuppressWarnings("EqualsWhichDoesntCheckParameterClass")
    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(lhs).append(" ").append(operator);
        if (!rhs.isEmpty()) {
            sb.append(" ");
            if (isIn() | isNotIn()) {
                sb.append("(");
                for (String value : rhs) {
                    quoteStringIfNecessary(sb, value);
                    sb.append(", ");
                }
                if (sb.length() > 0) {
                    sb.setLength(sb.length() - 2);
                }
                sb.append(")");
            } else if (isBetween() || isNotBetween()) {
                sb.append(rhs.get(0)).append(" and ").append(rhs.get(1));
            } else {
                quoteStringIfNecessary(sb, rhs.get(0));
            }
        }

        return sb.toString();
    }

    private void quoteStringIfNecessary(StringBuilder sb, String value) {
        if (quoted) {
            sb.append("'");
        }
        sb.append(value);
        if (quoted) {
            sb.append("'");
        }
    }
}