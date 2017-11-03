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
package org.glitch.dragoman.ql.listener.logging;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.glitch.dragoman.antlr.SQLParser;
import org.glitch.dragoman.antlr.SQLParserBaseListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link SQLParserBaseListener} which logs every callback from ANTLR, very useful when writing the
 * parsers and when diagnosing issues in the parsers but very verbose so the logging config is expected to mute this
 * logger except when necessary during development.
 */
public class LoggingListener extends SQLParserBaseListener {
    private static final Logger logger = LoggerFactory.getLogger(LoggingListener.class);

    @Override
    public void enterField_type(SQLParser.Field_typeContext ctx) {
        super.enterField_type(ctx);
        logger.debug("enterField_type: " + ctx.start.getText());
    }

    @Override
    public void exitField_type(SQLParser.Field_typeContext ctx) {
        super.exitField_type(ctx);
        logger.debug("exitField_type: " + ctx.start.getText());
    }

    @Override
    public void enterQuery_expression(SQLParser.Query_expressionContext ctx) {
        super.enterQuery_expression(ctx);
        logger.debug("enterQuery_expression: " + ctx.start.getText());
    }

    @Override
    public void exitQuery_expression(SQLParser.Query_expressionContext ctx) {
        super.exitQuery_expression(ctx);
        logger.debug("exitQuery_expression: " + ctx.start.getText());
    }

    @Override
    public void enterScalar_subquery(SQLParser.Scalar_subqueryContext ctx) {
        super.enterScalar_subquery(ctx);
        logger.debug("enterScalar_subquery: " + ctx.start.getText());
    }

    @Override
    public void exitScalar_subquery(SQLParser.Scalar_subqueryContext ctx) {
        super.exitScalar_subquery(ctx);
        logger.debug("exitScalar_subquery: " + ctx.start.getText());
    }

    @Override
    public void enterPredicate(SQLParser.PredicateContext ctx) {
        super.enterPredicate(ctx);
        logger.debug("enterPredicate: " + ctx.start.getText());
    }

    @Override
    public void exitPredicate(SQLParser.PredicateContext ctx) {
        super.exitPredicate(ctx);
        logger.debug("exitPredicate: " + ctx.start.getText());
    }

    @Override
    public void enterAggregate_function(SQLParser.Aggregate_functionContext ctx) {
        super.enterAggregate_function(ctx);
        logger.debug("enterAggregate_function: " + ctx.start.getText());
    }

    @Override
    public void exitAggregate_function(SQLParser.Aggregate_functionContext ctx) {
        super.exitAggregate_function(ctx);
        logger.debug("exitAggregate_function: " + ctx.start.getText());
    }

    @Override
    public void enterTable_reference_list(SQLParser.Table_reference_listContext ctx) {
        super.enterTable_reference_list(ctx);
        logger.debug("enterTable_reference_list: " + ctx.start.getText());
    }

    @Override
    public void exitTable_reference_list(SQLParser.Table_reference_listContext ctx) {
        super.exitTable_reference_list(ctx);
        logger.debug("exitTable_reference_list: " + ctx.start.getText());
    }

    @Override
    public void enterRollup_list(SQLParser.Rollup_listContext ctx) {
        super.enterRollup_list(ctx);
        logger.debug("enterRollup_list: " + ctx.start.getText());
    }

    @Override
    public void exitRollup_list(SQLParser.Rollup_listContext ctx) {
        super.exitRollup_list(ctx);
        logger.debug("exitRollup_list: " + ctx.start.getText());
    }

    @Override
    public void enterPattern_matcher(SQLParser.Pattern_matcherContext ctx) {
        super.enterPattern_matcher(ctx);
        logger.debug("enterPattern_matcher: " + ctx.start.getText());
    }

    @Override
    public void exitPattern_matcher(SQLParser.Pattern_matcherContext ctx) {
        super.exitPattern_matcher(ctx);
        logger.debug("exitPattern_matcher: " + ctx.start.getText());
    }

    @Override
    public void enterOr_predicate(SQLParser.Or_predicateContext ctx) {
        super.enterOr_predicate(ctx);
        logger.debug("enterOr_predicate: " + ctx.start.getText());
    }

    @Override
    public void exitOr_predicate(SQLParser.Or_predicateContext ctx) {
        super.exitOr_predicate(ctx);
        logger.debug("exitOr_predicate: " + ctx.start.getText());
    }

    @Override
    public void enterCube_list(SQLParser.Cube_listContext ctx) {
        super.enterCube_list(ctx);
        logger.debug("enterCube_list: " + ctx.start.getText());
    }

    @Override
    public void exitCube_list(SQLParser.Cube_listContext ctx) {
        super.exitCube_list(ctx);
        logger.debug("exitCube_list: " + ctx.start.getText());
    }

    @Override
    public void enterNumeric_type(SQLParser.Numeric_typeContext ctx) {
        super.enterNumeric_type(ctx);
        logger.debug("enterNumeric_type: " + ctx.start.getText());
    }

    @Override
    public void exitNumeric_type(SQLParser.Numeric_typeContext ctx) {
        super.exitNumeric_type(ctx);
        logger.debug("exitNumeric_type: " + ctx.start.getText());
    }

    @Override
    public void enterSchema_statement(SQLParser.Schema_statementContext ctx) {
        super.enterSchema_statement(ctx);
        logger.debug("enterSchema_statement: " + ctx.start.getText());
    }

    @Override
    public void exitSchema_statement(SQLParser.Schema_statementContext ctx) {
        super.exitSchema_statement(ctx);
        logger.debug("exitSchema_statement: " + ctx.start.getText());
    }

    @Override
    public void enterRow_value_special_case(SQLParser.Row_value_special_caseContext ctx) {
        super.enterRow_value_special_case(ctx);
        logger.debug("enterRow_value_special_case: " + ctx.start.getText());
    }

    @Override
    public void exitRow_value_special_case(SQLParser.Row_value_special_caseContext ctx) {
        super.exitRow_value_special_case(ctx);
        logger.debug("exitRow_value_special_case: " + ctx.start.getText());
    }

    @Override
    public void enterBinary_type(SQLParser.Binary_typeContext ctx) {
        super.enterBinary_type(ctx);
        logger.debug("enterBinary_type: " + ctx.start.getText());
    }

    @Override
    public void exitBinary_type(SQLParser.Binary_typeContext ctx) {
        super.exitBinary_type(ctx);
        logger.debug("exitBinary_type: " + ctx.start.getText());
    }

    @Override
    public void enterExplicit_row_value_constructor(SQLParser.Explicit_row_value_constructorContext ctx) {
        super.enterExplicit_row_value_constructor(ctx);
        logger.debug("enterExplicit_row_value_constructor: " + ctx.start.getText());
    }

    @Override
    public void exitExplicit_row_value_constructor(SQLParser.Explicit_row_value_constructorContext ctx) {
        super.exitExplicit_row_value_constructor(ctx);
        logger.debug("exitExplicit_row_value_constructor: " + ctx.start.getText());
    }

    @Override
    public void enterTime_literal(SQLParser.Time_literalContext ctx) {
        super.enterTime_literal(ctx);
        logger.debug("enterTime_literal: " + ctx.start.getText());
    }

    @Override
    public void exitTime_literal(SQLParser.Time_literalContext ctx) {
        super.exitTime_literal(ctx);
        logger.debug("exitTime_literal: " + ctx.start.getText());
    }

    @Override
    public void enterRow_value_constructor_predicand(SQLParser.Row_value_constructor_predicandContext ctx) {
        super.enterRow_value_constructor_predicand(ctx);
        logger.debug("enterRow_value_constructor_predicand: " + ctx.start.getText());
    }

    @Override
    public void exitRow_value_constructor_predicand(SQLParser.Row_value_constructor_predicandContext ctx) {
        super.exitRow_value_constructor_predicand(ctx);
        logger.debug("exitRow_value_constructor_predicand: " + ctx.start.getText());
    }

    @Override
    public void enterBit_type(SQLParser.Bit_typeContext ctx) {
        super.enterBit_type(ctx);
        logger.debug("enterBit_type: " + ctx.start.getText());
    }

    @Override
    public void exitBit_type(SQLParser.Bit_typeContext ctx) {
        super.exitBit_type(ctx);
        logger.debug("exitBit_type: " + ctx.start.getText());
    }

    @Override
    public void enterPrecision_param(SQLParser.Precision_paramContext ctx) {
        super.enterPrecision_param(ctx);
        logger.debug("enterPrecision_param: " + ctx.start.getText());
    }

    @Override
    public void exitPrecision_param(SQLParser.Precision_paramContext ctx) {
        super.exitPrecision_param(ctx);
        logger.debug("exitPrecision_param: " + ctx.start.getText());
    }

    @Override
    public void enterDrop_table_statement(SQLParser.Drop_table_statementContext ctx) {
        super.enterDrop_table_statement(ctx);
        logger.debug("enterDrop_table_statement: " + ctx.start.getText());
    }

    @Override
    public void exitDrop_table_statement(SQLParser.Drop_table_statementContext ctx) {
        super.exitDrop_table_statement(ctx);
        logger.debug("exitDrop_table_statement: " + ctx.start.getText());
    }

    @Override
    public void enterLimit_clause(SQLParser.Limit_clauseContext ctx) {
        super.enterLimit_clause(ctx);
        logger.debug("enterLimit_clause: " + ctx.start.getText());
    }

    @Override
    public void exitLimit_clause(SQLParser.Limit_clauseContext ctx) {
        super.exitLimit_clause(ctx);
        logger.debug("exitLimit_clause: " + ctx.start.getText());
    }

    @Override
    public void enterFrom_clause(SQLParser.From_clauseContext ctx) {
        super.enterFrom_clause(ctx);
        logger.debug("enterFrom_clause: " + ctx.start.getText());
    }

    @Override
    public void exitFrom_clause(SQLParser.From_clauseContext ctx) {
        super.exitFrom_clause(ctx);
        logger.debug("exitFrom_clause: " + ctx.start.getText());
    }

    @Override
    public void enterIn_predicate_value(SQLParser.In_predicate_valueContext ctx) {
        super.enterIn_predicate_value(ctx);
        logger.debug("enterIn_predicate_value: " + ctx.start.getText());
    }

    @Override
    public void exitIn_predicate_value(SQLParser.In_predicate_valueContext ctx) {
        super.exitIn_predicate_value(ctx);
        logger.debug("exitIn_predicate_value: " + ctx.start.getText());
    }

    @Override
    public void enterIndividual_hash_partition(SQLParser.Individual_hash_partitionContext ctx) {
        super.enterIndividual_hash_partition(ctx);
        logger.debug("enterIndividual_hash_partition: " + ctx.start.getText());
    }

    @Override
    public void exitIndividual_hash_partition(SQLParser.Individual_hash_partitionContext ctx) {
        super.exitIndividual_hash_partition(ctx);
        logger.debug("exitIndividual_hash_partition: " + ctx.start.getText());
    }

    @Override
    public void enterColumn_partitions(SQLParser.Column_partitionsContext ctx) {
        super.enterColumn_partitions(ctx);
        logger.debug("enterColumn_partitions: " + ctx.start.getText());
    }

    @Override
    public void exitColumn_partitions(SQLParser.Column_partitionsContext ctx) {
        super.exitColumn_partitions(ctx);
        logger.debug("exitColumn_partitions: " + ctx.start.getText());
    }

    @Override
    public void enterBoolean_test(SQLParser.Boolean_testContext ctx) {
        super.enterBoolean_test(ctx);
        logger.debug("enterBoolean_test: " + ctx.start.getText());
    }

    @Override
    public void exitBoolean_test(SQLParser.Boolean_testContext ctx) {
        super.exitBoolean_test(ctx);
        logger.debug("exitBoolean_test: " + ctx.start.getText());
    }

    @Override
    public void enterExists_predicate(SQLParser.Exists_predicateContext ctx) {
        super.enterExists_predicate(ctx);
        logger.debug("enterExists_predicate: " + ctx.start.getText());
    }

    @Override
    public void exitExists_predicate(SQLParser.Exists_predicateContext ctx) {
        super.exitExists_predicate(ctx);
        logger.debug("exitExists_predicate: " + ctx.start.getText());
    }

    @Override
    public void enterCharacter_factor(SQLParser.Character_factorContext ctx) {
        super.enterCharacter_factor(ctx);
        logger.debug("enterCharacter_factor: " + ctx.start.getText());
    }

    @Override
    public void exitCharacter_factor(SQLParser.Character_factorContext ctx) {
        super.exitCharacter_factor(ctx);
        logger.debug("exitCharacter_factor: " + ctx.start.getText());
    }

    @Override
    public void enterNamed_columns_join(SQLParser.Named_columns_joinContext ctx) {
        super.enterNamed_columns_join(ctx);
        logger.debug("enterNamed_columns_join: " + ctx.start.getText());
    }

    @Override
    public void exitNamed_columns_join(SQLParser.Named_columns_joinContext ctx) {
        super.exitNamed_columns_join(ctx);
        logger.debug("exitNamed_columns_join: " + ctx.start.getText());
    }

    @Override
    public void enterPattern_matching_predicate(SQLParser.Pattern_matching_predicateContext ctx) {
        super.enterPattern_matching_predicate(ctx);
        logger.debug("enterPattern_matching_predicate: " + ctx.start.getText());
    }

    @Override
    public void exitPattern_matching_predicate(SQLParser.Pattern_matching_predicateContext ctx) {
        super.exitPattern_matching_predicate(ctx);
        logger.debug("exitPattern_matching_predicate: " + ctx.start.getText());
    }

    @Override
    public void enterUnsigned_value_specification(SQLParser.Unsigned_value_specificationContext ctx) {
        super.enterUnsigned_value_specification(ctx);
        logger.debug("enterUnsigned_value_specification: " + ctx.start.getText());
    }

    @Override
    public void exitUnsigned_value_specification(SQLParser.Unsigned_value_specificationContext ctx) {
        super.exitUnsigned_value_specification(ctx);
        logger.debug("exitUnsigned_value_specification: " + ctx.start.getText());
    }

    @Override
    public void enterParam(SQLParser.ParamContext ctx) {
        super.enterParam(ctx);
        logger.debug("enterParam: " + ctx.start.getText());
    }

    @Override
    public void exitParam(SQLParser.ParamContext ctx) {
        super.exitParam(ctx);
        logger.debug("exitParam: " + ctx.start.getText());
    }

    @Override
    public void enterSimple_table(SQLParser.Simple_tableContext ctx) {
        super.enterSimple_table(ctx);
        logger.debug("enterSimple_table: " + ctx.start.getText());
    }

    @Override
    public void exitSimple_table(SQLParser.Simple_tableContext ctx) {
        super.exitSimple_table(ctx);
        logger.debug("exitSimple_table: " + ctx.start.getText());
    }

    @Override
    public void enterParam_clause(SQLParser.Param_clauseContext ctx) {
        super.enterParam_clause(ctx);
        logger.debug("enterParam_clause: " + ctx.start.getText());
    }

    @Override
    public void exitParam_clause(SQLParser.Param_clauseContext ctx) {
        super.exitParam_clause(ctx);
        logger.debug("exitParam_clause: " + ctx.start.getText());
    }

    @Override
    public void enterDerived_table(SQLParser.Derived_tableContext ctx) {
        super.enterDerived_table(ctx);
        logger.debug("enterDerived_table: " + ctx.start.getText());
    }

    @Override
    public void exitDerived_table(SQLParser.Derived_tableContext ctx) {
        super.exitDerived_table(ctx);
        logger.debug("exitDerived_table: " + ctx.start.getText());
    }

    @Override
    public void enterHaving_clause(SQLParser.Having_clauseContext ctx) {
        super.enterHaving_clause(ctx);
        logger.debug("enterHaving_clause: " + ctx.start.getText());
    }

    @Override
    public void exitHaving_clause(SQLParser.Having_clauseContext ctx) {
        super.exitHaving_clause(ctx);
        logger.debug("exitHaving_clause: " + ctx.start.getText());
    }

    @Override
    public void enterCharacter_string_type(SQLParser.Character_string_typeContext ctx) {
        super.enterCharacter_string_type(ctx);
        logger.debug("enterCharacter_string_type: " + ctx.start.getText());
    }

    @Override
    public void exitCharacter_string_type(SQLParser.Character_string_typeContext ctx) {
        super.exitCharacter_string_type(ctx);
        logger.debug("exitCharacter_string_type: " + ctx.start.getText());
    }

    @Override
    public void enterSet_function_type(SQLParser.Set_function_typeContext ctx) {
        super.enterSet_function_type(ctx);
        logger.debug("enterSet_function_type: " + ctx.start.getText());
    }

    @Override
    public void exitSet_function_type(SQLParser.Set_function_typeContext ctx) {
        super.exitSet_function_type(ctx);
        logger.debug("exitSet_function_type: " + ctx.start.getText());
    }

    @Override
    public void enterParenthesized_boolean_value_expression(
            SQLParser.Parenthesized_boolean_value_expressionContext ctx) {
        super.enterParenthesized_boolean_value_expression(ctx);
        logger.debug("enterParenthesized_boolean_value_expression: " + ctx.start.getText());
    }

    @Override
    public void exitParenthesized_boolean_value_expression(
            SQLParser.Parenthesized_boolean_value_expressionContext ctx) {
        super.exitParenthesized_boolean_value_expression(ctx);
        logger.debug("exitParenthesized_boolean_value_expression: " + ctx.start.getText());
    }

    @Override
    public void enterExtended_datetime_field(SQLParser.Extended_datetime_fieldContext ctx) {
        super.enterExtended_datetime_field(ctx);
        logger.debug("enterExtended_datetime_field: " + ctx.start.getText());
    }

    @Override
    public void exitExtended_datetime_field(SQLParser.Extended_datetime_fieldContext ctx) {
        super.exitExtended_datetime_field(ctx);
        logger.debug("exitExtended_datetime_field: " + ctx.start.getText());
    }

    @Override
    public void enterComp_op(SQLParser.Comp_opContext ctx) {
        super.enterComp_op(ctx);
        logger.debug("enterComp_op: " + ctx.start.getText());
    }

    @Override
    public void exitComp_op(SQLParser.Comp_opContext ctx) {
        super.exitComp_op(ctx);
        logger.debug("exitComp_op: " + ctx.start.getText());
    }

    @Override
    public void enterJoined_table(SQLParser.Joined_tableContext ctx) {
        super.enterJoined_table(ctx);
        logger.debug("enterJoined_table: " + ctx.start.getText());
    }

    @Override
    public void exitJoined_table(SQLParser.Joined_tableContext ctx) {
        super.exitJoined_table(ctx);
        logger.debug("exitJoined_table: " + ctx.start.getText());
    }

    @Override
    public void enterFactor(SQLParser.FactorContext ctx) {
        super.enterFactor(ctx);
        logger.debug("enterFactor: " + ctx.start.getText());
    }

    @Override
    public void exitFactor(SQLParser.FactorContext ctx) {
        super.exitFactor(ctx);
        logger.debug("exitFactor: " + ctx.start.getText());
    }

    @Override
    public void enterCase_expression(SQLParser.Case_expressionContext ctx) {
        super.enterCase_expression(ctx);
        logger.debug("enterCase_expression: " + ctx.start.getText());
    }

    @Override
    public void exitCase_expression(SQLParser.Case_expressionContext ctx) {
        super.exitCase_expression(ctx);
        logger.debug("exitCase_expression: " + ctx.start.getText());
    }

    @Override
    public void enterSelect_list(SQLParser.Select_listContext ctx) {
        super.enterSelect_list(ctx);
        logger.debug("enterSelect_list: " + ctx.start.getText());
    }

    @Override
    public void exitSelect_list(SQLParser.Select_listContext ctx) {
        super.exitSelect_list(ctx);
        logger.debug("exitSelect_list: " + ctx.start.getText());
    }

    @Override
    public void enterCharacter_value_expression(SQLParser.Character_value_expressionContext ctx) {
        super.enterCharacter_value_expression(ctx);
        logger.debug("enterCharacter_value_expression: " + ctx.start.getText());
    }

    @Override
    public void exitCharacter_value_expression(SQLParser.Character_value_expressionContext ctx) {
        super.exitCharacter_value_expression(ctx);
        logger.debug("exitCharacter_value_expression: " + ctx.start.getText());
    }

    @Override
    public void enterSort_specifier_list(SQLParser.Sort_specifier_listContext ctx) {
        super.enterSort_specifier_list(ctx);
        logger.debug("enterSort_specifier_list: " + ctx.start.getText());
    }

    @Override
    public void exitSort_specifier_list(SQLParser.Sort_specifier_listContext ctx) {
        super.exitSort_specifier_list(ctx);
        logger.debug("exitSort_specifier_list: " + ctx.start.getText());
    }

    @Override
    public void enterGeneral_literal(SQLParser.General_literalContext ctx) {
        super.enterGeneral_literal(ctx);
        logger.debug("enterGeneral_literal: " + ctx.start.getText());
    }

    @Override
    public void exitGeneral_literal(SQLParser.General_literalContext ctx) {
        super.exitGeneral_literal(ctx);
        logger.debug("exitGeneral_literal: " + ctx.start.getText());
    }

    @Override
    public void enterOrdinary_grouping_set(SQLParser.Ordinary_grouping_setContext ctx) {
        super.enterOrdinary_grouping_set(ctx);
        logger.debug("enterOrdinary_grouping_set: " + ctx.start.getText());
    }

    @Override
    public void exitOrdinary_grouping_set(SQLParser.Ordinary_grouping_setContext ctx) {
        super.exitOrdinary_grouping_set(ctx);
        logger.debug("exitOrdinary_grouping_set: " + ctx.start.getText());
    }

    @Override
    public void enterTime_zone_field(SQLParser.Time_zone_fieldContext ctx) {
        super.enterTime_zone_field(ctx);
        logger.debug("enterTime_zone_field: " + ctx.start.getText());
    }

    @Override
    public void exitTime_zone_field(SQLParser.Time_zone_fieldContext ctx) {
        super.exitTime_zone_field(ctx);
        logger.debug("exitTime_zone_field: " + ctx.start.getText());
    }

    @Override
    public void enterCast_target(SQLParser.Cast_targetContext ctx) {
        super.enterCast_target(ctx);
        logger.debug("enterCast_target: " + ctx.start.getText());
    }

    @Override
    public void exitCast_target(SQLParser.Cast_targetContext ctx) {
        super.exitCast_target(ctx);
        logger.debug("exitCast_target: " + ctx.start.getText());
    }

    @Override
    public void enterIndividual_hash_partitions(SQLParser.Individual_hash_partitionsContext ctx) {
        super.enterIndividual_hash_partitions(ctx);
        logger.debug("enterIndividual_hash_partitions: " + ctx.start.getText());
    }

    @Override
    public void exitIndividual_hash_partitions(SQLParser.Individual_hash_partitionsContext ctx) {
        super.exitIndividual_hash_partitions(ctx);
        logger.debug("exitIndividual_hash_partitions: " + ctx.start.getText());
    }

    @Override
    public void enterColumn_reference_list(SQLParser.Column_reference_listContext ctx) {
        super.enterColumn_reference_list(ctx);
        logger.debug("enterColumn_reference_list: " + ctx.start.getText());
    }

    @Override
    public void exitColumn_reference_list(SQLParser.Column_reference_listContext ctx) {
        super.exitColumn_reference_list(ctx);
        logger.debug("exitColumn_reference_list: " + ctx.start.getText());
    }

    @Override
    public void enterData_statement(SQLParser.Data_statementContext ctx) {
        super.enterData_statement(ctx);
        logger.debug("enterData_statement: " + ctx.start.getText());
    }

    @Override
    public void exitData_statement(SQLParser.Data_statementContext ctx) {
        super.exitData_statement(ctx);
        logger.debug("exitData_statement: " + ctx.start.getText());
    }

    @Override
    public void enterTable_expression(SQLParser.Table_expressionContext ctx) {
        super.enterTable_expression(ctx);
        logger.debug("enterTable_expression: " + ctx.start.getText());
    }

    @Override
    public void exitTable_expression(SQLParser.Table_expressionContext ctx) {
        super.exitTable_expression(ctx);
        logger.debug("exitTable_expression: " + ctx.start.getText());
    }

    @Override
    public void enterString_value_function(SQLParser.String_value_functionContext ctx) {
        super.enterString_value_function(ctx);
        logger.debug("enterString_value_function: " + ctx.start.getText());
    }

    @Override
    public void exitString_value_function(SQLParser.String_value_functionContext ctx) {
        super.exitString_value_function(ctx);
        logger.debug("exitString_value_function: " + ctx.start.getText());
    }

    @Override
    public void enterDerived_column(SQLParser.Derived_columnContext ctx) {
        super.enterDerived_column(ctx);
        logger.debug("enterDerived_column: " + ctx.start.getText());
    }

    @Override
    public void exitDerived_column(SQLParser.Derived_columnContext ctx) {
        super.exitDerived_column(ctx);
        logger.debug("exitDerived_column: " + ctx.start.getText());
    }

    @Override
    public void enterQuery_term(SQLParser.Query_termContext ctx) {
        super.enterQuery_term(ctx);
        logger.debug("enterQuery_term: " + ctx.start.getText());
    }

    @Override
    public void exitQuery_term(SQLParser.Query_termContext ctx) {
        super.exitQuery_term(ctx);
        logger.debug("exitQuery_term: " + ctx.start.getText());
    }

    @Override
    public void enterInsert_statement(SQLParser.Insert_statementContext ctx) {
        super.enterInsert_statement(ctx);
        logger.debug("enterInsert_statement: " + ctx.start.getText());
    }

    @Override
    public void exitInsert_statement(SQLParser.Insert_statementContext ctx) {
        super.exitInsert_statement(ctx);
        logger.debug("exitInsert_statement: " + ctx.start.getText());
    }

    @Override
    public void enterJoin_type(SQLParser.Join_typeContext ctx) {
        super.enterJoin_type(ctx);
        logger.debug("enterJoin_type: " + ctx.start.getText());
    }

    @Override
    public void exitJoin_type(SQLParser.Join_typeContext ctx) {
        super.exitJoin_type(ctx);
        logger.debug("exitJoin_type: " + ctx.start.getText());
    }

    @Override
    public void enterJoin_specification(SQLParser.Join_specificationContext ctx) {
        super.enterJoin_specification(ctx);
        logger.debug("enterJoin_specification: " + ctx.start.getText());
    }

    @Override
    public void exitJoin_specification(SQLParser.Join_specificationContext ctx) {
        super.exitJoin_specification(ctx);
        logger.debug("exitJoin_specification: " + ctx.start.getText());
    }

    @Override
    public void enterData_type(SQLParser.Data_typeContext ctx) {
        super.enterData_type(ctx);
        logger.debug("enterData_type: " + ctx.start.getText());
    }

    @Override
    public void exitData_type(SQLParser.Data_typeContext ctx) {
        super.exitData_type(ctx);
        logger.debug("exitData_type: " + ctx.start.getText());
    }

    @Override
    public void enterGrouping_element_list(SQLParser.Grouping_element_listContext ctx) {
        super.enterGrouping_element_list(ctx);
        logger.debug("enterGrouping_element_list: " + ctx.start.getText());
    }

    @Override
    public void exitGrouping_element_list(SQLParser.Grouping_element_listContext ctx) {
        super.exitGrouping_element_list(ctx);
        logger.debug("exitGrouping_element_list: " + ctx.start.getText());
    }

    @Override
    public void enterCast_operand(SQLParser.Cast_operandContext ctx) {
        super.enterCast_operand(ctx);
        logger.debug("enterCast_operand: " + ctx.start.getText());
    }

    @Override
    public void exitCast_operand(SQLParser.Cast_operandContext ctx) {
        super.exitCast_operand(ctx);
        logger.debug("exitCast_operand: " + ctx.start.getText());
    }

    @Override
    public void enterFunction_name(SQLParser.Function_nameContext ctx) {
        super.enterFunction_name(ctx);
        logger.debug("enterFunction_name: " + ctx.start.getText());
    }

    @Override
    public void exitFunction_name(SQLParser.Function_nameContext ctx) {
        super.exitFunction_name(ctx);
        logger.debug("exitFunction_name: " + ctx.start.getText());
    }

    @Override
    public void enterQuery_expression_body(SQLParser.Query_expression_bodyContext ctx) {
        super.enterQuery_expression_body(ctx);
        logger.debug("enterQuery_expression_body: " + ctx.start.getText());
    }

    @Override
    public void exitQuery_expression_body(SQLParser.Query_expression_bodyContext ctx) {
        super.exitQuery_expression_body(ctx);
        logger.debug("exitQuery_expression_body: " + ctx.start.getText());
    }

    @Override
    public void enterDatetime_literal(SQLParser.Datetime_literalContext ctx) {
        super.enterDatetime_literal(ctx);
        logger.debug("enterDatetime_literal: " + ctx.start.getText());
    }

    @Override
    public void exitDatetime_literal(SQLParser.Datetime_literalContext ctx) {
        super.exitDatetime_literal(ctx);
        logger.debug("exitDatetime_literal: " + ctx.start.getText());
    }

    @Override
    public void enterBoolean_factor(SQLParser.Boolean_factorContext ctx) {
        super.enterBoolean_factor(ctx);
        logger.debug("enterBoolean_factor: " + ctx.start.getText());
    }

    @Override
    public void exitBoolean_factor(SQLParser.Boolean_factorContext ctx) {
        super.exitBoolean_factor(ctx);
        logger.debug("exitBoolean_factor: " + ctx.start.getText());
    }

    @Override
    public void enterQuery_primary(SQLParser.Query_primaryContext ctx) {
        super.enterQuery_primary(ctx);
        logger.debug("enterQuery_primary: " + ctx.start.getText());
    }

    @Override
    public void exitQuery_primary(SQLParser.Query_primaryContext ctx) {
        super.exitQuery_primary(ctx);
        logger.debug("exitQuery_primary: " + ctx.start.getText());
    }

    @Override
    public void enterNumeric_value_expression(SQLParser.Numeric_value_expressionContext ctx) {
        super.enterNumeric_value_expression(ctx);
        logger.debug("enterNumeric_value_expression: " + ctx.start.getText());
    }

    @Override
    public void exitNumeric_value_expression(SQLParser.Numeric_value_expressionContext ctx) {
        super.exitNumeric_value_expression(ctx);
        logger.debug("exitNumeric_value_expression: " + ctx.start.getText());
    }

    @Override
    public void enterJoined_table_primary(SQLParser.Joined_table_primaryContext ctx) {
        super.enterJoined_table_primary(ctx);
        logger.debug("enterJoined_table_primary: " + ctx.start.getText());
    }

    @Override
    public void exitJoined_table_primary(SQLParser.Joined_table_primaryContext ctx) {
        super.exitJoined_table_primary(ctx);
        logger.debug("exitJoined_table_primary: " + ctx.start.getText());
    }

    @Override
    public void enterRoutine_invocation(SQLParser.Routine_invocationContext ctx) {
        super.enterRoutine_invocation(ctx);
        logger.debug("enterRoutine_invocation: " + ctx.start.getText());
    }

    @Override
    public void exitRoutine_invocation(SQLParser.Routine_invocationContext ctx) {
        super.exitRoutine_invocation(ctx);
        logger.debug("exitRoutine_invocation: " + ctx.start.getText());
    }

    @Override
    public void enterNon_join_query_expression(SQLParser.Non_join_query_expressionContext ctx) {
        super.enterNon_join_query_expression(ctx);
        logger.debug("enterNon_join_query_expression: " + ctx.start.getText());
    }

    @Override
    public void exitNon_join_query_expression(SQLParser.Non_join_query_expressionContext ctx) {
        super.exitNon_join_query_expression(ctx);
        logger.debug("exitNon_join_query_expression: " + ctx.start.getText());
    }

    @Override
    public void enterColumn_name_list(SQLParser.Column_name_listContext ctx) {
        super.enterColumn_name_list(ctx);
        logger.debug("enterColumn_name_list: " + ctx.start.getText());
    }

    @Override
    public void exitColumn_name_list(SQLParser.Column_name_listContext ctx) {
        super.exitColumn_name_list(ctx);
        logger.debug("exitColumn_name_list: " + ctx.start.getText());
    }

    @Override
    public void enterSign(SQLParser.SignContext ctx) {
        super.enterSign(ctx);
        logger.debug("enterSign: " + ctx.start.getText());
    }

    @Override
    public void exitSign(SQLParser.SignContext ctx) {
        super.exitSign(ctx);
        logger.debug("exitSign: " + ctx.start.getText());
    }

    @Override
    public void enterValue_expression_primary(SQLParser.Value_expression_primaryContext ctx) {
        super.enterValue_expression_primary(ctx);
        logger.debug("enterValue_expression_primary: " + ctx.start.getText());
    }

    @Override
    public void exitValue_expression_primary(SQLParser.Value_expression_primaryContext ctx) {
        super.exitValue_expression_primary(ctx);
        logger.debug("exitValue_expression_primary: " + ctx.start.getText());
    }

    @Override
    public void enterRange_partitions(SQLParser.Range_partitionsContext ctx) {
        super.enterRange_partitions(ctx);
        logger.debug("enterRange_partitions: " + ctx.start.getText());
    }

    @Override
    public void exitRange_partitions(SQLParser.Range_partitionsContext ctx) {
        super.exitRange_partitions(ctx);
        logger.debug("exitRange_partitions: " + ctx.start.getText());
    }

    @Override
    public void enterComparison_predicate(SQLParser.Comparison_predicateContext ctx) {
        super.enterComparison_predicate(ctx);
        logger.debug("enterComparison_predicate: " + ctx.start.getText());
    }

    @Override
    public void exitComparison_predicate(SQLParser.Comparison_predicateContext ctx) {
        super.exitComparison_predicate(ctx);
        logger.debug("exitComparison_predicate: " + ctx.start.getText());
    }

    @Override
    public void enterCase_specification(SQLParser.Case_specificationContext ctx) {
        super.enterCase_specification(ctx);
        logger.debug("enterCase_specification: " + ctx.start.getText());
    }

    @Override
    public void exitCase_specification(SQLParser.Case_specificationContext ctx) {
        super.exitCase_specification(ctx);
        logger.debug("exitCase_specification: " + ctx.start.getText());
    }

    @Override
    public void enterBinary_large_object_string_type(SQLParser.Binary_large_object_string_typeContext ctx) {
        super.enterBinary_large_object_string_type(ctx);
        logger.debug("enterBinary_large_object_string_type: " + ctx.start.getText());
    }

    @Override
    public void exitBinary_large_object_string_type(SQLParser.Binary_large_object_string_typeContext ctx) {
        super.exitBinary_large_object_string_type(ctx);
        logger.debug("exitBinary_large_object_string_type: " + ctx.start.getText());
    }

    @Override
    public void enterIn_predicate(SQLParser.In_predicateContext ctx) {
        super.enterIn_predicate(ctx);
        logger.debug("enterIn_predicate: " + ctx.start.getText());
    }

    @Override
    public void exitIn_predicate(SQLParser.In_predicateContext ctx) {
        super.exitIn_predicate(ctx);
        logger.debug("exitIn_predicate: " + ctx.start.getText());
    }

    @Override
    public void enterOuter_join_type(SQLParser.Outer_join_typeContext ctx) {
        super.enterOuter_join_type(ctx);
        logger.debug("enterOuter_join_type: " + ctx.start.getText());
    }

    @Override
    public void exitOuter_join_type(SQLParser.Outer_join_typeContext ctx) {
        super.exitOuter_join_type(ctx);
        logger.debug("exitOuter_join_type: " + ctx.start.getText());
    }

    @Override
    public void enterString_value_expression(SQLParser.String_value_expressionContext ctx) {
        super.enterString_value_expression(ctx);
        logger.debug("enterString_value_expression: " + ctx.start.getText());
    }

    @Override
    public void exitString_value_expression(SQLParser.String_value_expressionContext ctx) {
        super.exitString_value_expression(ctx);
        logger.debug("exitString_value_expression: " + ctx.start.getText());
    }

    @Override
    public void enterIdentifier(SQLParser.IdentifierContext ctx) {
        super.enterIdentifier(ctx);
        logger.debug("enterIdentifier: " + ctx.start.getText());
    }

    @Override
    public void exitIdentifier(SQLParser.IdentifierContext ctx) {
        super.exitIdentifier(ctx);
        logger.debug("exitIdentifier: " + ctx.start.getText());
    }

    @Override
    public void enterResult(SQLParser.ResultContext ctx) {
        super.enterResult(ctx);
        logger.debug("enterResult: " + ctx.start.getText());
    }

    @Override
    public void exitResult(SQLParser.ResultContext ctx) {
        super.exitResult(ctx);
        logger.debug("exitResult: " + ctx.start.getText());
    }

    @Override
    public void enterBoolean_literal(SQLParser.Boolean_literalContext ctx) {
        super.enterBoolean_literal(ctx);
        logger.debug("enterBoolean_literal: " + ctx.start.getText());
    }

    @Override
    public void exitBoolean_literal(SQLParser.Boolean_literalContext ctx) {
        super.exitBoolean_literal(ctx);
        logger.debug("exitBoolean_literal: " + ctx.start.getText());
    }

    @Override
    public void enterCommon_value_expression(SQLParser.Common_value_expressionContext ctx) {
        super.enterCommon_value_expression(ctx);
        logger.debug("enterCommon_value_expression: " + ctx.start.getText());
    }

    @Override
    public void exitCommon_value_expression(SQLParser.Common_value_expressionContext ctx) {
        super.exitCommon_value_expression(ctx);
        logger.debug("exitCommon_value_expression: " + ctx.start.getText());
    }

    @Override
    public void enterBoolean_type(SQLParser.Boolean_typeContext ctx) {
        super.enterBoolean_type(ctx);
        logger.debug("enterBoolean_type: " + ctx.start.getText());
    }

    @Override
    public void exitBoolean_type(SQLParser.Boolean_typeContext ctx) {
        super.exitBoolean_type(ctx);
        logger.debug("exitBoolean_type: " + ctx.start.getText());
    }

    @Override
    public void enterTable_reference(SQLParser.Table_referenceContext ctx) {
        super.enterTable_reference(ctx);
        logger.debug("enterTable_reference: " + ctx.start.getText());
    }

    @Override
    public void exitTable_reference(SQLParser.Table_referenceContext ctx) {
        super.exitTable_reference(ctx);
        logger.debug("exitTable_reference: " + ctx.start.getText());
    }

    @Override
    public void enterTable_or_query_name(SQLParser.Table_or_query_nameContext ctx) {
        super.enterTable_or_query_name(ctx);
        logger.debug("enterTable_or_query_name: " + ctx.start.getText());
    }

    @Override
    public void exitTable_or_query_name(SQLParser.Table_or_query_nameContext ctx) {
        super.exitTable_or_query_name(ctx);
        logger.debug("exitTable_or_query_name: " + ctx.start.getText());
    }

    @Override
    public void enterCase_abbreviation(SQLParser.Case_abbreviationContext ctx) {
        super.enterCase_abbreviation(ctx);
        logger.debug("enterCase_abbreviation: " + ctx.start.getText());
    }

    @Override
    public void exitCase_abbreviation(SQLParser.Case_abbreviationContext ctx) {
        super.exitCase_abbreviation(ctx);
        logger.debug("exitCase_abbreviation: " + ctx.start.getText());
    }

    @Override
    public void enterEmpty_grouping_set(SQLParser.Empty_grouping_setContext ctx) {
        super.enterEmpty_grouping_set(ctx);
        logger.debug("enterEmpty_grouping_set: " + ctx.start.getText());
    }

    @Override
    public void exitEmpty_grouping_set(SQLParser.Empty_grouping_setContext ctx) {
        super.exitEmpty_grouping_set(ctx);
        logger.debug("exitEmpty_grouping_set: " + ctx.start.getText());
    }

    @Override
    public void enterNull_predicate(SQLParser.Null_predicateContext ctx) {
        super.enterNull_predicate(ctx);
        logger.debug("enterNull_predicate: " + ctx.start.getText());
    }

    @Override
    public void exitNull_predicate(SQLParser.Null_predicateContext ctx) {
        super.exitNull_predicate(ctx);
        logger.debug("exitNull_predicate: " + ctx.start.getText());
    }

    @Override
    public void enterOrder_specification(SQLParser.Order_specificationContext ctx) {
        super.enterOrder_specification(ctx);
        logger.debug("enterOrder_specification: " + ctx.start.getText());
    }

    @Override
    public void exitOrder_specification(SQLParser.Order_specificationContext ctx) {
        super.exitOrder_specification(ctx);
        logger.debug("exitOrder_specification: " + ctx.start.getText());
    }

    @Override
    public void enterParenthesized_value_expression(SQLParser.Parenthesized_value_expressionContext ctx) {
        super.enterParenthesized_value_expression(ctx);
        logger.debug("enterParenthesized_value_expression: " + ctx.start.getText());
    }

    @Override
    public void exitParenthesized_value_expression(SQLParser.Parenthesized_value_expressionContext ctx) {
        super.exitParenthesized_value_expression(ctx);
        logger.debug("exitParenthesized_value_expression: " + ctx.start.getText());
    }

    @Override
    public void enterSimple_when_clause(SQLParser.Simple_when_clauseContext ctx) {
        super.enterSimple_when_clause(ctx);
        logger.debug("enterSimple_when_clause: " + ctx.start.getText());
    }

    @Override
    public void exitSimple_when_clause(SQLParser.Simple_when_clauseContext ctx) {
        super.exitSimple_when_clause(ctx);
        logger.debug("exitSimple_when_clause: " + ctx.start.getText());
    }

    @Override
    public void enterList_value_clause_list(SQLParser.List_value_clause_listContext ctx) {
        super.enterList_value_clause_list(ctx);
        logger.debug("enterList_value_clause_list: " + ctx.start.getText());
    }

    @Override
    public void exitList_value_clause_list(SQLParser.List_value_clause_listContext ctx) {
        super.exitList_value_clause_list(ctx);
        logger.debug("exitList_value_clause_list: " + ctx.start.getText());
    }

    @Override
    public void enterFilter_clause(SQLParser.Filter_clauseContext ctx) {
        super.enterFilter_clause(ctx);
        logger.debug("enterFilter_clause: " + ctx.start.getText());
    }

    @Override
    public void exitFilter_clause(SQLParser.Filter_clauseContext ctx) {
        super.exitFilter_clause(ctx);
        logger.debug("exitFilter_clause: " + ctx.start.getText());
    }

    @Override
    public void enterOrderby_clause(SQLParser.Orderby_clauseContext ctx) {
        super.enterOrderby_clause(ctx);
        logger.debug("enterOrderby_clause: " + ctx.start.getText());
    }

    @Override
    public void exitOrderby_clause(SQLParser.Orderby_clauseContext ctx) {
        super.exitOrderby_clause(ctx);
        logger.debug("exitOrderby_clause: " + ctx.start.getText());
    }

    @Override
    public void enterAll(SQLParser.AllContext ctx) {
        super.enterAll(ctx);
        logger.debug("enterAll: " + ctx.start.getText());
    }

    @Override
    public void exitAll(SQLParser.AllContext ctx) {
        super.exitAll(ctx);
        logger.debug("exitAll: " + ctx.start.getText());
    }

    @Override
    public void enterData_change_statement(SQLParser.Data_change_statementContext ctx) {
        super.enterData_change_statement(ctx);
        logger.debug("enterData_change_statement: " + ctx.start.getText());
    }

    @Override
    public void exitData_change_statement(SQLParser.Data_change_statementContext ctx) {
        super.exitData_change_statement(ctx);
        logger.debug("exitData_change_statement: " + ctx.start.getText());
    }

    @Override
    public void enterWhere_clause(SQLParser.Where_clauseContext ctx) {
        super.enterWhere_clause(ctx);
        logger.debug("enterWhere_clause: " + ctx.start.getText());
    }

    @Override
    public void exitWhere_clause(SQLParser.Where_clauseContext ctx) {
        super.exitWhere_clause(ctx);
        logger.debug("exitWhere_clause: " + ctx.start.getText());
    }

    @Override
    public void enterField_element(SQLParser.Field_elementContext ctx) {
        super.enterField_element(ctx);
        logger.debug("enterField_element: " + ctx.start.getText());
    }

    @Override
    public void exitField_element(SQLParser.Field_elementContext ctx) {
        super.exitField_element(ctx);
        logger.debug("exitField_element: " + ctx.start.getText());
    }

    @Override
    public void enterNon_join_query_term(SQLParser.Non_join_query_termContext ctx) {
        super.enterNon_join_query_term(ctx);
        logger.debug("enterNon_join_query_term: " + ctx.start.getText());
    }

    @Override
    public void exitNon_join_query_term(SQLParser.Non_join_query_termContext ctx) {
        super.exitNon_join_query_term(ctx);
        logger.debug("exitNon_join_query_term: " + ctx.start.getText());
    }

    @Override
    public void enterNull_ordering(SQLParser.Null_orderingContext ctx) {
        super.enterNull_ordering(ctx);
        logger.debug("enterNull_ordering: " + ctx.start.getText());
    }

    @Override
    public void exitNull_ordering(SQLParser.Null_orderingContext ctx) {
        super.exitNull_ordering(ctx);
        logger.debug("exitNull_ordering: " + ctx.start.getText());
    }

    @Override
    public void enterNon_second_primary_datetime_field(SQLParser.Non_second_primary_datetime_fieldContext ctx) {
        super.enterNon_second_primary_datetime_field(ctx);
        logger.debug("enterNon_second_primary_datetime_field: " + ctx.start.getText());
    }

    @Override
    public void exitNon_second_primary_datetime_field(SQLParser.Non_second_primary_datetime_fieldContext ctx) {
        super.exitNon_second_primary_datetime_field(ctx);
        logger.debug("exitNon_second_primary_datetime_field: " + ctx.start.getText());
    }

    @Override
    public void enterValue_expression(SQLParser.Value_expressionContext ctx) {
        super.enterValue_expression(ctx);
        logger.debug("enterValue_expression: " + ctx.start.getText());
    }

    @Override
    public void exitValue_expression(SQLParser.Value_expressionContext ctx) {
        super.exitValue_expression(ctx);
        logger.debug("exitValue_expression: " + ctx.start.getText());
    }

    @Override
    public void enterOrdinary_grouping_set_list(SQLParser.Ordinary_grouping_set_listContext ctx) {
        super.enterOrdinary_grouping_set_list(ctx);
        logger.debug("enterOrdinary_grouping_set_list: " + ctx.start.getText());
    }

    @Override
    public void exitOrdinary_grouping_set_list(SQLParser.Ordinary_grouping_set_listContext ctx) {
        super.exitOrdinary_grouping_set_list(ctx);
        logger.debug("exitOrdinary_grouping_set_list: " + ctx.start.getText());
    }

    @Override
    public void enterJoin_condition(SQLParser.Join_conditionContext ctx) {
        super.enterJoin_condition(ctx);
        logger.debug("enterJoin_condition: " + ctx.start.getText());
    }

    @Override
    public void exitJoin_condition(SQLParser.Join_conditionContext ctx) {
        super.exitJoin_condition(ctx);
        logger.debug("exitJoin_condition: " + ctx.start.getText());
    }

    @Override
    public void enterNegativable_matcher(SQLParser.Negativable_matcherContext ctx) {
        super.enterNegativable_matcher(ctx);
        logger.debug("enterNegativable_matcher: " + ctx.start.getText());
    }

    @Override
    public void exitNegativable_matcher(SQLParser.Negativable_matcherContext ctx) {
        super.exitNegativable_matcher(ctx);
        logger.debug("exitNegativable_matcher: " + ctx.start.getText());
    }

    @Override
    public void enterQualified_asterisk(SQLParser.Qualified_asteriskContext ctx) {
        super.enterQualified_asterisk(ctx);
        logger.debug("enterQualified_asterisk: " + ctx.start.getText());
    }

    @Override
    public void exitQualified_asterisk(SQLParser.Qualified_asteriskContext ctx) {
        super.exitQualified_asterisk(ctx);
        logger.debug("exitQualified_asterisk: " + ctx.start.getText());
    }

    @Override
    public void enterGeneral_set_function(SQLParser.General_set_functionContext ctx) {
        super.enterGeneral_set_function(ctx);
        logger.debug("enterGeneral_set_function: " + ctx.start.getText());
    }

    @Override
    public void exitGeneral_set_function(SQLParser.General_set_functionContext ctx) {
        super.exitGeneral_set_function(ctx);
        logger.debug("exitGeneral_set_function: " + ctx.start.getText());
    }

    @Override
    public void enterNon_join_query_primary(SQLParser.Non_join_query_primaryContext ctx) {
        super.enterNon_join_query_primary(ctx);
        logger.debug("enterNon_join_query_primary: " + ctx.start.getText());
    }

    @Override
    public void exitNon_join_query_primary(SQLParser.Non_join_query_primaryContext ctx) {
        super.exitNon_join_query_primary(ctx);
        logger.debug("exitNon_join_query_primary: " + ctx.start.getText());
    }

    @Override
    public void enterQuantifier(SQLParser.QuantifierContext ctx) {
        super.enterQuantifier(ctx);
        logger.debug("enterQuantifier: " + ctx.start.getText());
    }

    @Override
    public void exitQuantifier(SQLParser.QuantifierContext ctx) {
        super.exitQuantifier(ctx);
        logger.debug("exitQuantifier: " + ctx.start.getText());
    }

    @Override
    public void enterIndex_statement(SQLParser.Index_statementContext ctx) {
        super.enterIndex_statement(ctx);
        logger.debug("enterIndex_statement: " + ctx.start.getText());
    }

    @Override
    public void exitIndex_statement(SQLParser.Index_statementContext ctx) {
        super.exitIndex_statement(ctx);
        logger.debug("exitIndex_statement: " + ctx.start.getText());
    }

    @Override
    public void enterBetween_predicate(SQLParser.Between_predicateContext ctx) {
        super.enterBetween_predicate(ctx);
        logger.debug("enterBetween_predicate: " + ctx.start.getText());
    }

    @Override
    public void exitBetween_predicate(SQLParser.Between_predicateContext ctx) {
        super.exitBetween_predicate(ctx);
        logger.debug("exitBetween_predicate: " + ctx.start.getText());
    }

    @Override
    public void enterPrimary_datetime_field(SQLParser.Primary_datetime_fieldContext ctx) {
        super.enterPrimary_datetime_field(ctx);
        logger.debug("enterPrimary_datetime_field: " + ctx.start.getText());
    }

    @Override
    public void exitPrimary_datetime_field(SQLParser.Primary_datetime_fieldContext ctx) {
        super.exitPrimary_datetime_field(ctx);
        logger.debug("exitPrimary_datetime_field: " + ctx.start.getText());
    }

    @Override
    public void enterSigned_numerical_literal(SQLParser.Signed_numerical_literalContext ctx) {
        super.enterSigned_numerical_literal(ctx);
        logger.debug("enterSigned_numerical_literal: " + ctx.start.getText());
    }

    @Override
    public void exitSigned_numerical_literal(SQLParser.Signed_numerical_literalContext ctx) {
        super.exitSigned_numerical_literal(ctx);
        logger.debug("exitSigned_numerical_literal: " + ctx.start.getText());
    }

    @Override
    public void enterNatural_join(SQLParser.Natural_joinContext ctx) {
        super.enterNatural_join(ctx);
        logger.debug("enterNatural_join: " + ctx.start.getText());
    }

    @Override
    public void exitNatural_join(SQLParser.Natural_joinContext ctx) {
        super.exitNatural_join(ctx);
        logger.debug("exitNatural_join: " + ctx.start.getText());
    }

    @Override
    public void enterSql_argument_list(SQLParser.Sql_argument_listContext ctx) {
        super.enterSql_argument_list(ctx);
        logger.debug("enterSql_argument_list: " + ctx.start.getText());
    }

    @Override
    public void exitSql_argument_list(SQLParser.Sql_argument_listContext ctx) {
        super.exitSql_argument_list(ctx);
        logger.debug("exitSql_argument_list: " + ctx.start.getText());
    }

    @Override
    public void enterUnique_predicate(SQLParser.Unique_predicateContext ctx) {
        super.enterUnique_predicate(ctx);
        logger.debug("enterUnique_predicate: " + ctx.start.getText());
    }

    @Override
    public void exitUnique_predicate(SQLParser.Unique_predicateContext ctx) {
        super.exitUnique_predicate(ctx);
        logger.debug("exitUnique_predicate: " + ctx.start.getText());
    }

    @Override
    public void enterAnd_predicate(SQLParser.And_predicateContext ctx) {
        super.enterAnd_predicate(ctx);
        logger.debug("enterAnd_predicate: " + ctx.start.getText());
    }

    @Override
    public void exitAnd_predicate(SQLParser.And_predicateContext ctx) {
        super.exitAnd_predicate(ctx);
        logger.debug("exitAnd_predicate: " + ctx.start.getText());
    }

    @Override
    public void enterQuery_specification(SQLParser.Query_specificationContext ctx) {
        super.enterQuery_specification(ctx);
        logger.debug("enterQuery_specification: " + ctx.start.getText());
    }

    @Override
    public void exitQuery_specification(SQLParser.Query_specificationContext ctx) {
        super.exitQuery_specification(ctx);
        logger.debug("exitQuery_specification: " + ctx.start.getText());
    }

    @Override
    public void enterExtract_expression(SQLParser.Extract_expressionContext ctx) {
        super.enterExtract_expression(ctx);
        logger.debug("enterExtract_expression: " + ctx.start.getText());
    }

    @Override
    public void exitExtract_expression(SQLParser.Extract_expressionContext ctx) {
        super.exitExtract_expression(ctx);
        logger.debug("exitExtract_expression: " + ctx.start.getText());
    }

    @Override
    public void enterGrouping_element(SQLParser.Grouping_elementContext ctx) {
        super.enterGrouping_element(ctx);
        logger.debug("enterGrouping_element: " + ctx.start.getText());
    }

    @Override
    public void exitGrouping_element(SQLParser.Grouping_elementContext ctx) {
        super.exitGrouping_element(ctx);
        logger.debug("exitGrouping_element: " + ctx.start.getText());
    }

    @Override
    public void enterNonreserved_keywords(SQLParser.Nonreserved_keywordsContext ctx) {
        super.enterNonreserved_keywords(ctx);
        logger.debug("enterNonreserved_keywords: " + ctx.start.getText());
    }

    @Override
    public void exitNonreserved_keywords(SQLParser.Nonreserved_keywordsContext ctx) {
        super.exitNonreserved_keywords(ctx);
        logger.debug("exitNonreserved_keywords: " + ctx.start.getText());
    }

    @Override
    public void enterSet_function_specification(SQLParser.Set_function_specificationContext ctx) {
        super.enterSet_function_specification(ctx);
        logger.debug("enterSet_function_specification: " + ctx.start.getText());
    }

    @Override
    public void exitSet_function_specification(SQLParser.Set_function_specificationContext ctx) {
        super.exitSet_function_specification(ctx);
        logger.debug("exitSet_function_specification: " + ctx.start.getText());
    }

    @Override
    public void enterSome(SQLParser.SomeContext ctx) {
        super.enterSome(ctx);
        logger.debug("enterSome: " + ctx.start.getText());
    }

    @Override
    public void exitSome(SQLParser.SomeContext ctx) {
        super.exitSome(ctx);
        logger.debug("exitSome: " + ctx.start.getText());
    }

    @Override
    public void enterTruth_value(SQLParser.Truth_valueContext ctx) {
        super.enterTruth_value(ctx);
        logger.debug("enterTruth_value: " + ctx.start.getText());
    }

    @Override
    public void exitTruth_value(SQLParser.Truth_valueContext ctx) {
        super.exitTruth_value(ctx);
        logger.debug("exitTruth_value: " + ctx.start.getText());
    }

    @Override
    public void enterDatetime_type(SQLParser.Datetime_typeContext ctx) {
        super.enterDatetime_type(ctx);
        logger.debug("enterDatetime_type: " + ctx.start.getText());
    }

    @Override
    public void exitDatetime_type(SQLParser.Datetime_typeContext ctx) {
        super.exitDatetime_type(ctx);
        logger.debug("exitDatetime_type: " + ctx.start.getText());
    }

    @Override
    public void enterSet_qualifier(SQLParser.Set_qualifierContext ctx) {
        super.enterSet_qualifier(ctx);
        logger.debug("enterSet_qualifier: " + ctx.start.getText());
    }

    @Override
    public void exitSet_qualifier(SQLParser.Set_qualifierContext ctx) {
        super.exitSet_qualifier(ctx);
        logger.debug("exitSet_qualifier: " + ctx.start.getText());
    }

    @Override
    public void enterExplicit_table(SQLParser.Explicit_tableContext ctx) {
        super.enterExplicit_table(ctx);
        logger.debug("enterExplicit_table: " + ctx.start.getText());
    }

    @Override
    public void exitExplicit_table(SQLParser.Explicit_tableContext ctx) {
        super.exitExplicit_table(ctx);
        logger.debug("exitExplicit_table: " + ctx.start.getText());
    }

    @Override
    public void enterUnsigned_numeric_literal(SQLParser.Unsigned_numeric_literalContext ctx) {
        super.enterUnsigned_numeric_literal(ctx);
        logger.debug("enterUnsigned_numeric_literal: " + ctx.start.getText());
    }

    @Override
    public void exitUnsigned_numeric_literal(SQLParser.Unsigned_numeric_literalContext ctx) {
        super.exitUnsigned_numeric_literal(ctx);
        logger.debug("exitUnsigned_numeric_literal: " + ctx.start.getText());
    }

    @Override
    public void enterBoolean_predicand(SQLParser.Boolean_predicandContext ctx) {
        super.enterBoolean_predicand(ctx);
        logger.debug("enterBoolean_predicand: " + ctx.start.getText());
    }

    @Override
    public void exitBoolean_predicand(SQLParser.Boolean_predicandContext ctx) {
        super.exitBoolean_predicand(ctx);
        logger.debug("exitBoolean_predicand: " + ctx.start.getText());
    }

    @Override
    public void enterSearch_condition(SQLParser.Search_conditionContext ctx) {
        super.enterSearch_condition(ctx);
        logger.debug("enterSearch_condition: " + ctx.start.getText());
    }

    @Override
    public void exitSearch_condition(SQLParser.Search_conditionContext ctx) {
        super.exitSearch_condition(ctx);
        logger.debug("exitSearch_condition: " + ctx.start.getText());
    }

    @Override
    public void enterBoolean_primary(SQLParser.Boolean_primaryContext ctx) {
        super.enterBoolean_primary(ctx);
        logger.debug("enterBoolean_primary: " + ctx.start.getText());
    }

    @Override
    public void exitBoolean_primary(SQLParser.Boolean_primaryContext ctx) {
        super.exitBoolean_primary(ctx);
        logger.debug("exitBoolean_primary: " + ctx.start.getText());
    }

    @Override
    public void enterSearched_when_clause(SQLParser.Searched_when_clauseContext ctx) {
        super.enterSearched_when_clause(ctx);
        logger.debug("enterSearched_when_clause: " + ctx.start.getText());
    }

    @Override
    public void exitSearched_when_clause(SQLParser.Searched_when_clauseContext ctx) {
        super.exitSearched_when_clause(ctx);
        logger.debug("exitSearched_when_clause: " + ctx.start.getText());
    }

    @Override
    public void enterDate_literal(SQLParser.Date_literalContext ctx) {
        super.enterDate_literal(ctx);
        logger.debug("enterDate_literal: " + ctx.start.getText());
    }

    @Override
    public void exitDate_literal(SQLParser.Date_literalContext ctx) {
        super.exitDate_literal(ctx);
        logger.debug("exitDate_literal: " + ctx.start.getText());
    }

    @Override
    public void enterCharacter_primary(SQLParser.Character_primaryContext ctx) {
        super.enterCharacter_primary(ctx);
        logger.debug("enterCharacter_primary: " + ctx.start.getText());
    }

    @Override
    public void exitCharacter_primary(SQLParser.Character_primaryContext ctx) {
        super.exitCharacter_primary(ctx);
        logger.debug("exitCharacter_primary: " + ctx.start.getText());
    }

    @Override
    public void enterSimple_case(SQLParser.Simple_caseContext ctx) {
        super.enterSimple_case(ctx);
        logger.debug("enterSimple_case: " + ctx.start.getText());
    }

    @Override
    public void exitSimple_case(SQLParser.Simple_caseContext ctx) {
        super.exitSimple_case(ctx);
        logger.debug("exitSimple_case: " + ctx.start.getText());
    }

    @Override
    public void enterRange_value_clause(SQLParser.Range_value_clauseContext ctx) {
        super.enterRange_value_clause(ctx);
        logger.debug("enterRange_value_clause: " + ctx.start.getText());
    }

    @Override
    public void exitRange_value_clause(SQLParser.Range_value_clauseContext ctx) {
        super.exitRange_value_clause(ctx);
        logger.debug("exitRange_value_clause: " + ctx.start.getText());
    }

    @Override
    public void enterApproximate_numeric_type(SQLParser.Approximate_numeric_typeContext ctx) {
        super.enterApproximate_numeric_type(ctx);
        logger.debug("enterApproximate_numeric_type: " + ctx.start.getText());
    }

    @Override
    public void exitApproximate_numeric_type(SQLParser.Approximate_numeric_typeContext ctx) {
        super.exitApproximate_numeric_type(ctx);
        logger.debug("exitApproximate_numeric_type: " + ctx.start.getText());
    }

    @Override
    public void enterGrouping_operation(SQLParser.Grouping_operationContext ctx) {
        super.enterGrouping_operation(ctx);
        logger.debug("enterGrouping_operation: " + ctx.start.getText());
    }

    @Override
    public void exitGrouping_operation(SQLParser.Grouping_operationContext ctx) {
        super.exitGrouping_operation(ctx);
        logger.debug("exitGrouping_operation: " + ctx.start.getText());
    }

    @Override
    public void enterTrim_operands(SQLParser.Trim_operandsContext ctx) {
        super.enterTrim_operands(ctx);
        logger.debug("enterTrim_operands: " + ctx.start.getText());
    }

    @Override
    public void exitTrim_operands(SQLParser.Trim_operandsContext ctx) {
        super.exitTrim_operands(ctx);
        logger.debug("exitTrim_operands: " + ctx.start.getText());
    }

    @Override
    public void enterSql(SQLParser.SqlContext ctx) {
        super.enterSql(ctx);
        logger.debug("enterSql: " + ctx.start.getText());
    }

    @Override
    public void exitSql(SQLParser.SqlContext ctx) {
        super.exitSql(ctx);
        logger.debug("exitSql: " + ctx.start.getText());
    }

    @Override
    public void enterTable_subquery(SQLParser.Table_subqueryContext ctx) {
        super.enterTable_subquery(ctx);
        logger.debug("enterTable_subquery: " + ctx.start.getText());
    }

    @Override
    public void exitTable_subquery(SQLParser.Table_subqueryContext ctx) {
        super.exitTable_subquery(ctx);
        logger.debug("exitTable_subquery: " + ctx.start.getText());
    }

    @Override
    public void enterOuter_join_type_part2(SQLParser.Outer_join_type_part2Context ctx) {
        super.enterOuter_join_type_part2(ctx);
        logger.debug("enterOuter_join_type_part2: " + ctx.start.getText());
    }

    @Override
    public void exitOuter_join_type_part2(SQLParser.Outer_join_type_part2Context ctx) {
        super.exitOuter_join_type_part2(ctx);
        logger.debug("exitOuter_join_type_part2: " + ctx.start.getText());
    }

    @Override
    public void enterCross_join(SQLParser.Cross_joinContext ctx) {
        super.enterCross_join(ctx);
        logger.debug("enterCross_join: " + ctx.start.getText());
    }

    @Override
    public void exitCross_join(SQLParser.Cross_joinContext ctx) {
        super.exitCross_join(ctx);
        logger.debug("exitCross_join: " + ctx.start.getText());
    }

    @Override
    public void enterUnsigned_literal(SQLParser.Unsigned_literalContext ctx) {
        super.enterUnsigned_literal(ctx);
        logger.debug("enterUnsigned_literal: " + ctx.start.getText());
    }

    @Override
    public void exitUnsigned_literal(SQLParser.Unsigned_literalContext ctx) {
        super.exitUnsigned_literal(ctx);
        logger.debug("exitUnsigned_literal: " + ctx.start.getText());
    }

    @Override
    public void enterRow_subquery(SQLParser.Row_subqueryContext ctx) {
        super.enterRow_subquery(ctx);
        logger.debug("enterRow_subquery: " + ctx.start.getText());
    }

    @Override
    public void exitRow_subquery(SQLParser.Row_subqueryContext ctx) {
        super.exitRow_subquery(ctx);
        logger.debug("exitRow_subquery: " + ctx.start.getText());
    }

    @Override
    public void enterNumeric_value_function(SQLParser.Numeric_value_functionContext ctx) {
        super.enterNumeric_value_function(ctx);
        logger.debug("enterNumeric_value_function: " + ctx.start.getText());
    }

    @Override
    public void exitNumeric_value_function(SQLParser.Numeric_value_functionContext ctx) {
        super.exitNumeric_value_function(ctx);
        logger.debug("exitNumeric_value_function: " + ctx.start.getText());
    }

    @Override
    public void enterPredefined_type(SQLParser.Predefined_typeContext ctx) {
        super.enterPredefined_type(ctx);
        logger.debug("enterPredefined_type: " + ctx.start.getText());
    }

    @Override
    public void exitPredefined_type(SQLParser.Predefined_typeContext ctx) {
        super.exitPredefined_type(ctx);
        logger.debug("exitPredefined_type: " + ctx.start.getText());
    }

    @Override
    public void enterSelect_sublist(SQLParser.Select_sublistContext ctx) {
        super.enterSelect_sublist(ctx);
        logger.debug("enterSelect_sublist: " + ctx.start.getText());
    }

    @Override
    public void exitSelect_sublist(SQLParser.Select_sublistContext ctx) {
        super.exitSelect_sublist(ctx);
        logger.debug("exitSelect_sublist: " + ctx.start.getText());
    }

    @Override
    public void enterQuantified_comparison_predicate(SQLParser.Quantified_comparison_predicateContext ctx) {
        super.enterQuantified_comparison_predicate(ctx);
        logger.debug("enterQuantified_comparison_predicate: " + ctx.start.getText());
    }

    @Override
    public void exitQuantified_comparison_predicate(SQLParser.Quantified_comparison_predicateContext ctx) {
        super.exitQuantified_comparison_predicate(ctx);
        logger.debug("exitQuantified_comparison_predicate: " + ctx.start.getText());
    }

    @Override
    public void enterTable_partitioning_clauses(SQLParser.Table_partitioning_clausesContext ctx) {
        super.enterTable_partitioning_clauses(ctx);
        logger.debug("enterTable_partitioning_clauses: " + ctx.start.getText());
    }

    @Override
    public void exitTable_partitioning_clauses(SQLParser.Table_partitioning_clausesContext ctx) {
        super.exitTable_partitioning_clauses(ctx);
        logger.debug("exitTable_partitioning_clauses: " + ctx.start.getText());
    }

    @Override
    public void enterExact_numeric_type(SQLParser.Exact_numeric_typeContext ctx) {
        super.enterExact_numeric_type(ctx);
        logger.debug("enterExact_numeric_type: " + ctx.start.getText());
    }

    @Override
    public void exitExact_numeric_type(SQLParser.Exact_numeric_typeContext ctx) {
        super.exitExact_numeric_type(ctx);
        logger.debug("exitExact_numeric_type: " + ctx.start.getText());
    }

    @Override
    public void enterIn_value_list(SQLParser.In_value_listContext ctx) {
        super.enterIn_value_list(ctx);
        logger.debug("enterIn_value_list: " + ctx.start.getText());
    }

    @Override
    public void exitIn_value_list(SQLParser.In_value_listContext ctx) {
        super.exitIn_value_list(ctx);
        logger.debug("exitIn_value_list: " + ctx.start.getText());
    }

    @Override
    public void enterNumeric_primary(SQLParser.Numeric_primaryContext ctx) {
        super.enterNumeric_primary(ctx);
        logger.debug("enterNumeric_primary: " + ctx.start.getText());
    }

    @Override
    public void exitNumeric_primary(SQLParser.Numeric_primaryContext ctx) {
        super.exitNumeric_primary(ctx);
        logger.debug("exitNumeric_primary: " + ctx.start.getText());
    }

    @Override
    public void enterTrim_function(SQLParser.Trim_functionContext ctx) {
        super.enterTrim_function(ctx);
        logger.debug("enterTrim_function: " + ctx.start.getText());
    }

    @Override
    public void exitTrim_function(SQLParser.Trim_functionContext ctx) {
        super.exitTrim_function(ctx);
        logger.debug("exitTrim_function: " + ctx.start.getText());
    }

    @Override
    public void enterGroupby_clause(SQLParser.Groupby_clauseContext ctx) {
        super.enterGroupby_clause(ctx);
        logger.debug("enterGroupby_clause: " + ctx.start.getText());
    }

    @Override
    public void exitGroupby_clause(SQLParser.Groupby_clauseContext ctx) {
        super.exitGroupby_clause(ctx);
        logger.debug("exitGroupby_clause: " + ctx.start.getText());
    }

    @Override
    public void enterRow_value_expression(SQLParser.Row_value_expressionContext ctx) {
        super.enterRow_value_expression(ctx);
        logger.debug("enterRow_value_expression: " + ctx.start.getText());
    }

    @Override
    public void exitRow_value_expression(SQLParser.Row_value_expressionContext ctx) {
        super.exitRow_value_expression(ctx);
        logger.debug("exitRow_value_expression: " + ctx.start.getText());
    }

    @Override
    public void enterNational_character_string_type(SQLParser.National_character_string_typeContext ctx) {
        super.enterNational_character_string_type(ctx);
        logger.debug("enterNational_character_string_type: " + ctx.start.getText());
    }

    @Override
    public void exitNational_character_string_type(SQLParser.National_character_string_typeContext ctx) {
        super.exitNational_character_string_type(ctx);
        logger.debug("exitNational_character_string_type: " + ctx.start.getText());
    }

    @Override
    public void enterTrim_specification(SQLParser.Trim_specificationContext ctx) {
        super.enterTrim_specification(ctx);
        logger.debug("enterTrim_specification: " + ctx.start.getText());
    }

    @Override
    public void exitTrim_specification(SQLParser.Trim_specificationContext ctx) {
        super.exitTrim_specification(ctx);
        logger.debug("exitTrim_specification: " + ctx.start.getText());
    }

    @Override
    public void enterRow_value_predicand(SQLParser.Row_value_predicandContext ctx) {
        super.enterRow_value_predicand(ctx);
        logger.debug("enterRow_value_predicand: " + ctx.start.getText());
    }

    @Override
    public void exitRow_value_predicand(SQLParser.Row_value_predicandContext ctx) {
        super.exitRow_value_predicand(ctx);
        logger.debug("exitRow_value_predicand: " + ctx.start.getText());
    }

    @Override
    public void enterQualified_join(SQLParser.Qualified_joinContext ctx) {
        super.enterQualified_join(ctx);
        logger.debug("enterQualified_join: " + ctx.start.getText());
    }

    @Override
    public void exitQualified_join(SQLParser.Qualified_joinContext ctx) {
        super.exitQualified_join(ctx);
        logger.debug("exitQualified_join: " + ctx.start.getText());
    }

    @Override
    public void enterType_length(SQLParser.Type_lengthContext ctx) {
        super.enterType_length(ctx);
        logger.debug("enterType_length: " + ctx.start.getText());
    }

    @Override
    public void exitType_length(SQLParser.Type_lengthContext ctx) {
        super.exitType_length(ctx);
        logger.debug("exitType_length: " + ctx.start.getText());
    }

    @Override
    public void enterElse_clause(SQLParser.Else_clauseContext ctx) {
        super.enterElse_clause(ctx);
        logger.debug("enterElse_clause: " + ctx.start.getText());
    }

    @Override
    public void exitElse_clause(SQLParser.Else_clauseContext ctx) {
        super.exitElse_clause(ctx);
        logger.debug("exitElse_clause: " + ctx.start.getText());
    }

    @Override
    public void enterStatement(SQLParser.StatementContext ctx) {
        super.enterStatement(ctx);
        logger.debug("enterStatement: " + ctx.start.getText());
    }

    @Override
    public void exitStatement(SQLParser.StatementContext ctx) {
        super.exitStatement(ctx);
        logger.debug("exitStatement: " + ctx.start.getText());
    }

    @Override
    public void enterColumn_reference(SQLParser.Column_referenceContext ctx) {
        super.enterColumn_reference(ctx);
        logger.debug("enterColumn_reference: " + ctx.start.getText());
    }

    @Override
    public void exitColumn_reference(SQLParser.Column_referenceContext ctx) {
        super.exitColumn_reference(ctx);
        logger.debug("exitColumn_reference: " + ctx.start.getText());
    }

    @Override
    public void enterUnion_join(SQLParser.Union_joinContext ctx) {
        super.enterUnion_join(ctx);
        logger.debug("enterUnion_join: " + ctx.start.getText());
    }

    @Override
    public void exitUnion_join(SQLParser.Union_joinContext ctx) {
        super.exitUnion_join(ctx);
        logger.debug("exitUnion_join: " + ctx.start.getText());
    }

    @Override
    public void enterTimestamp_literal(SQLParser.Timestamp_literalContext ctx) {
        super.enterTimestamp_literal(ctx);
        logger.debug("enterTimestamp_literal: " + ctx.start.getText());
    }

    @Override
    public void exitTimestamp_literal(SQLParser.Timestamp_literalContext ctx) {
        super.exitTimestamp_literal(ctx);
        logger.debug("exitTimestamp_literal: " + ctx.start.getText());
    }

    @Override
    public void enterPartition_name(SQLParser.Partition_nameContext ctx) {
        super.enterPartition_name(ctx);
        logger.debug("enterPartition_name: " + ctx.start.getText());
    }

    @Override
    public void exitPartition_name(SQLParser.Partition_nameContext ctx) {
        super.exitPartition_name(ctx);
        logger.debug("exitPartition_name: " + ctx.start.getText());
    }

    @Override
    public void enterBoolean_value_expression(SQLParser.Boolean_value_expressionContext ctx) {
        super.enterBoolean_value_expression(ctx);
        logger.debug("enterBoolean_value_expression: " + ctx.start.getText());
    }

    @Override
    public void exitBoolean_value_expression(SQLParser.Boolean_value_expressionContext ctx) {
        super.exitBoolean_value_expression(ctx);
        logger.debug("exitBoolean_value_expression: " + ctx.start.getText());
    }

    @Override
    public void enterHash_partitions_by_quantity(SQLParser.Hash_partitions_by_quantityContext ctx) {
        super.enterHash_partitions_by_quantity(ctx);
        logger.debug("enterHash_partitions_by_quantity: " + ctx.start.getText());
    }

    @Override
    public void exitHash_partitions_by_quantity(SQLParser.Hash_partitions_by_quantityContext ctx) {
        super.exitHash_partitions_by_quantity(ctx);
        logger.debug("exitHash_partitions_by_quantity: " + ctx.start.getText());
    }

    @Override
    public void enterExtract_source(SQLParser.Extract_sourceContext ctx) {
        super.enterExtract_source(ctx);
        logger.debug("enterExtract_source: " + ctx.start.getText());
    }

    @Override
    public void exitExtract_source(SQLParser.Extract_sourceContext ctx) {
        super.exitExtract_source(ctx);
        logger.debug("exitExtract_source: " + ctx.start.getText());
    }

    @Override
    public void enterTable_space_name(SQLParser.Table_space_nameContext ctx) {
        super.enterTable_space_name(ctx);
        logger.debug("enterTable_space_name: " + ctx.start.getText());
    }

    @Override
    public void exitTable_space_name(SQLParser.Table_space_nameContext ctx) {
        super.exitTable_space_name(ctx);
        logger.debug("exitTable_space_name: " + ctx.start.getText());
    }

    @Override
    public void enterList_value_partition(SQLParser.List_value_partitionContext ctx) {
        super.enterList_value_partition(ctx);
        logger.debug("enterList_value_partition: " + ctx.start.getText());
    }

    @Override
    public void exitList_value_partition(SQLParser.List_value_partitionContext ctx) {
        super.exitList_value_partition(ctx);
        logger.debug("exitList_value_partition: " + ctx.start.getText());
    }

    @Override
    public void enterList_partitions(SQLParser.List_partitionsContext ctx) {
        super.enterList_partitions(ctx);
        logger.debug("enterList_partitions: " + ctx.start.getText());
    }

    @Override
    public void exitList_partitions(SQLParser.List_partitionsContext ctx) {
        super.exitList_partitions(ctx);
        logger.debug("exitList_partitions: " + ctx.start.getText());
    }

    @Override
    public void enterExtract_field(SQLParser.Extract_fieldContext ctx) {
        super.enterExtract_field(ctx);
        logger.debug("enterExtract_field: " + ctx.start.getText());
    }

    @Override
    public void exitExtract_field(SQLParser.Extract_fieldContext ctx) {
        super.exitExtract_field(ctx);
        logger.debug("exitExtract_field: " + ctx.start.getText());
    }

    @Override
    public void enterHash_partitions(SQLParser.Hash_partitionsContext ctx) {
        super.enterHash_partitions(ctx);
        logger.debug("enterHash_partitions: " + ctx.start.getText());
    }

    @Override
    public void exitHash_partitions(SQLParser.Hash_partitionsContext ctx) {
        super.exitHash_partitions(ctx);
        logger.debug("exitHash_partitions: " + ctx.start.getText());
    }

    @Override
    public void enterCreate_table_statement(SQLParser.Create_table_statementContext ctx) {
        super.enterCreate_table_statement(ctx);
        logger.debug("enterCreate_table_statement: " + ctx.start.getText());
    }

    @Override
    public void exitCreate_table_statement(SQLParser.Create_table_statementContext ctx) {
        super.exitCreate_table_statement(ctx);
        logger.debug("exitCreate_table_statement: " + ctx.start.getText());
    }

    @Override
    public void enterCast_specification(SQLParser.Cast_specificationContext ctx) {
        super.enterCast_specification(ctx);
        logger.debug("enterCast_specification: " + ctx.start.getText());
    }

    @Override
    public void exitCast_specification(SQLParser.Cast_specificationContext ctx) {
        super.exitCast_specification(ctx);
        logger.debug("exitCast_specification: " + ctx.start.getText());
    }

    @Override
    public void enterTable_name(SQLParser.Table_nameContext ctx) {
        super.enterTable_name(ctx);
        logger.debug("enterTable_name: " + ctx.start.getText());
    }

    @Override
    public void exitTable_name(SQLParser.Table_nameContext ctx) {
        super.exitTable_name(ctx);
        logger.debug("exitTable_name: " + ctx.start.getText());
    }

    @Override
    public void enterAs_clause(SQLParser.As_clauseContext ctx) {
        super.enterAs_clause(ctx);
        logger.debug("enterAs_clause: " + ctx.start.getText());
    }

    @Override
    public void exitAs_clause(SQLParser.As_clauseContext ctx) {
        super.exitAs_clause(ctx);
        logger.debug("exitAs_clause: " + ctx.start.getText());
    }

    @Override
    public void enterSort_specifier(SQLParser.Sort_specifierContext ctx) {
        super.enterSort_specifier(ctx);
        logger.debug("enterSort_specifier: " + ctx.start.getText());
    }

    @Override
    public void exitSort_specifier(SQLParser.Sort_specifierContext ctx) {
        super.exitSort_specifier(ctx);
        logger.debug("exitSort_specifier: " + ctx.start.getText());
    }

    @Override
    public void enterRow_value_predicand_list(SQLParser.Row_value_predicand_listContext ctx) {
        super.enterRow_value_predicand_list(ctx);
        logger.debug("enterRow_value_predicand_list: " + ctx.start.getText());
    }

    @Override
    public void exitRow_value_predicand_list(SQLParser.Row_value_predicand_listContext ctx) {
        super.exitRow_value_predicand_list(ctx);
        logger.debug("exitRow_value_predicand_list: " + ctx.start.getText());
    }

    @Override
    public void enterTable_elements(SQLParser.Table_elementsContext ctx) {
        super.enterTable_elements(ctx);
        logger.debug("enterTable_elements: " + ctx.start.getText());
    }

    @Override
    public void exitTable_elements(SQLParser.Table_elementsContext ctx) {
        super.exitTable_elements(ctx);
        logger.debug("exitTable_elements: " + ctx.start.getText());
    }

    @Override
    public void enterRange_value_clause_list(SQLParser.Range_value_clause_listContext ctx) {
        super.enterRange_value_clause_list(ctx);
        logger.debug("enterRange_value_clause_list: " + ctx.start.getText());
    }

    @Override
    public void exitRange_value_clause_list(SQLParser.Range_value_clause_listContext ctx) {
        super.exitRange_value_clause_list(ctx);
        logger.debug("exitRange_value_clause_list: " + ctx.start.getText());
    }

    @Override
    public void enterIs_clause(SQLParser.Is_clauseContext ctx) {
        super.enterIs_clause(ctx);
        logger.debug("enterIs_clause: " + ctx.start.getText());
    }

    @Override
    public void exitIs_clause(SQLParser.Is_clauseContext ctx) {
        super.exitIs_clause(ctx);
        logger.debug("exitIs_clause: " + ctx.start.getText());
    }

    @Override
    public void enterMethod_specifier(SQLParser.Method_specifierContext ctx) {
        super.enterMethod_specifier(ctx);
        logger.debug("enterMethod_specifier: " + ctx.start.getText());
    }

    @Override
    public void exitMethod_specifier(SQLParser.Method_specifierContext ctx) {
        super.exitMethod_specifier(ctx);
        logger.debug("exitMethod_specifier: " + ctx.start.getText());
    }

    @Override
    public void enterBetween_predicate_part_2(SQLParser.Between_predicate_part_2Context ctx) {
        super.enterBetween_predicate_part_2(ctx);
        logger.debug("enterBetween_predicate_part_2: " + ctx.start.getText());
    }

    @Override
    public void exitBetween_predicate_part_2(SQLParser.Between_predicate_part_2Context ctx) {
        super.exitBetween_predicate_part_2(ctx);
        logger.debug("exitBetween_predicate_part_2: " + ctx.start.getText());
    }

    @Override
    public void enterSubquery(SQLParser.SubqueryContext ctx) {
        super.enterSubquery(ctx);
        logger.debug("enterSubquery: " + ctx.start.getText());
    }

    @Override
    public void exitSubquery(SQLParser.SubqueryContext ctx) {
        super.exitSubquery(ctx);
        logger.debug("exitSubquery: " + ctx.start.getText());
    }

    @Override
    public void enterFunction_names_for_reserved_words(SQLParser.Function_names_for_reserved_wordsContext ctx) {
        super.enterFunction_names_for_reserved_words(ctx);
        logger.debug("enterFunction_names_for_reserved_words: " + ctx.start.getText());
    }

    @Override
    public void exitFunction_names_for_reserved_words(SQLParser.Function_names_for_reserved_wordsContext ctx) {
        super.exitFunction_names_for_reserved_words(ctx);
        logger.debug("exitFunction_names_for_reserved_words: " + ctx.start.getText());
    }

    @Override
    public void enterSearched_case(SQLParser.Searched_caseContext ctx) {
        super.enterSearched_case(ctx);
        logger.debug("enterSearched_case: " + ctx.start.getText());
    }

    @Override
    public void exitSearched_case(SQLParser.Searched_caseContext ctx) {
        super.exitSearched_case(ctx);
        logger.debug("exitSearched_case: " + ctx.start.getText());
    }

    @Override
    public void enterNonparenthesized_value_expression_primary(
            SQLParser.Nonparenthesized_value_expression_primaryContext ctx) {
        super.enterNonparenthesized_value_expression_primary(ctx);
        logger.debug("enterNonparenthesized_value_expression_primary: " + ctx.start.getText());
    }

    @Override
    public void exitNonparenthesized_value_expression_primary(
            SQLParser.Nonparenthesized_value_expression_primaryContext ctx) {
        super.exitNonparenthesized_value_expression_primary(ctx);
        logger.debug("exitNonparenthesized_value_expression_primary: " + ctx.start.getText());
    }

    @Override
    public void enterRegex_matcher(SQLParser.Regex_matcherContext ctx) {
        super.enterRegex_matcher(ctx);
        logger.debug("enterRegex_matcher: " + ctx.start.getText());
    }

    @Override
    public void exitRegex_matcher(SQLParser.Regex_matcherContext ctx) {
        super.exitRegex_matcher(ctx);
        logger.debug("exitRegex_matcher: " + ctx.start.getText());
    }

    @Override
    public void enterTerm(SQLParser.TermContext ctx) {
        super.enterTerm(ctx);
        logger.debug("enterTerm: " + ctx.start.getText());
    }

    @Override
    public void exitTerm(SQLParser.TermContext ctx) {
        super.exitTerm(ctx);
        logger.debug("exitTerm: " + ctx.start.getText());
    }

    @Override
    public void enterTable_space_specifier(SQLParser.Table_space_specifierContext ctx) {
        super.enterTable_space_specifier(ctx);
        logger.debug("enterTable_space_specifier: " + ctx.start.getText());
    }

    @Override
    public void exitTable_space_specifier(SQLParser.Table_space_specifierContext ctx) {
        super.exitTable_space_specifier(ctx);
        logger.debug("exitTable_space_specifier: " + ctx.start.getText());
    }

    @Override
    public void enterTable_primary(SQLParser.Table_primaryContext ctx) {
        super.enterTable_primary(ctx);
        logger.debug("enterTable_primary: " + ctx.start.getText());
    }

    @Override
    public void exitTable_primary(SQLParser.Table_primaryContext ctx) {
        super.exitTable_primary(ctx);
        logger.debug("exitTable_primary: " + ctx.start.getText());
    }

    @Override
    public void visitTerminal(TerminalNode node) {
        super.visitTerminal(node);
        logger.debug("visitTerminal: " + node.getText());
    }

    @Override
    public void visitErrorNode(ErrorNode node) {
        super.visitErrorNode(node);
        logger.debug("visitErrorNode: " + node.getText());
    }
}