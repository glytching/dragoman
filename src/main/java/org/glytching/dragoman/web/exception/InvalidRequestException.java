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
package org.glytching.dragoman.web.exception;

import static java.lang.String.format;

/**
 * Extends {@link RuntimeException} to provide a specific exception for a HTTP {@code 400}.
 */
public class InvalidRequestException extends RuntimeException {
    public static InvalidRequestException missingParameter(String parameterName) {
        return new InvalidRequestException(format("The parameter: %s must be supplied!", parameterName));
    }

    public static InvalidRequestException invalidBody(String request, String targetType) {
        return new InvalidRequestException(format("Failed to deserialise request body: %s to type: %s!", request,
                targetType));
    }

    private InvalidRequestException(String message) {
        super(message);
    }

    public static InvalidRequestException invalidValue(String parameterName, String parameterValue,
                                                       String expectedValuePattern) {
        return new InvalidRequestException(format("Invalid value: %s for parameter: %s, valid values are: %s!",
                parameterValue, parameterName, expectedValuePattern));
    }

    public static InvalidRequestException create(String message) {
        return new InvalidRequestException(message);
    }
}