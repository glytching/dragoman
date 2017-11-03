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
package org.glitch.dragoman.ql.listener;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.glitch.dragoman.ql.SqlParserException;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the ANTLR error listener hook. Gathers and reports failures in a parser call.
 */
public class ErrorListener extends BaseErrorListener {
    private final List<String> errorMessages = Lists.newArrayList();

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int lineNumber, int positionInLine,
                            String message, RecognitionException e) {
        errorMessages.add(asMessage(lineNumber, positionInLine, message));
    }

    private String asMessage(int lineNumber, int positionInLine, String message) {
        return "Line: " + lineNumber + ", Position: " + positionInLine + ": " + message;
    }

    public Optional<SqlParserException> getException() {
        if (!errorMessages.isEmpty()) {
            Joiner joiner = Joiner.on(", ").skipNulls();
            return Optional.of(new SqlParserException(joiner.join(errorMessages)));
        }
        return Optional.empty();
    }

    public boolean hasErrors() {
        return !errorMessages.isEmpty();
    }
}