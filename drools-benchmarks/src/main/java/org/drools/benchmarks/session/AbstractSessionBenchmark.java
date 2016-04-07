/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.drools.benchmarks.session;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.SingleShotTime)
@State(Scope.Thread)
@Warmup(iterations = 30)
@Measurement(iterations = 20)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public abstract class AbstractSessionBenchmark {

    protected boolean holdSessionReferences = false;
    protected boolean reuseKieBase = false;

    protected KnowledgeBase kieBase;
    protected StatefulKnowledgeSession statefulSession;
    protected StatelessKnowledgeSession statelessSession;

    public abstract void setup();

    @TearDown(Level.Iteration)
    public void tearDown() {
        if (statefulSession != null) {
            statefulSession.dispose();
            statefulSession = null;
        }
        statelessSession = null;
    }

    protected void createStatefulSession() {
        statefulSession = kieBase.newStatefulKnowledgeSession();
    }

    protected void createStatelessSession() {
        statelessSession = kieBase.newStatelessKnowledgeSession();
    }

    protected void createEmptyKieBase() {
        kieBase = KnowledgeBaseFactory.newKnowledgeBase();
    }

}
