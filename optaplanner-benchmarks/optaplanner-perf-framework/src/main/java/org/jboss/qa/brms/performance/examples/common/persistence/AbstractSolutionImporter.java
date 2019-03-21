/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.qa.brms.performance.examples.common.persistence;

import java.io.File;
import java.math.BigInteger;
import java.math.RoundingMode;

import org.optaplanner.core.api.domain.solution.PlanningSolution;

/**
 * @param <Solution_> the solution type, the class with the {@link PlanningSolution} annotation
 */
public abstract class AbstractSolutionImporter<Solution_>  {

    public boolean acceptInputFile(File inputFile) {
        if (isInputFileDirectory()) {
            return inputFile.isDirectory();
        }
        return inputFile.getName().endsWith("." + getInputFileSuffix());
    }

    public boolean isInputFileDirectory() {
        return false;
    }

    public abstract String getInputFileSuffix();

    public abstract Solution_ readSolution(File inputFile);

    public static abstract class InputBuilder {

    }

    public static BigInteger factorial(int base) {
        if (base > 100000) {
            // Calculation takes too long
            return null;
        }
        BigInteger value = BigInteger.ONE;
        for (int i = 1; i <= base; i++) {
            value = value.multiply(BigInteger.valueOf(i));
        }
        return value;
    }


}
