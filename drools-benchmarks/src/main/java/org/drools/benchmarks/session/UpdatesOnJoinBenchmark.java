/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import org.drools.benchmarks.domain.A;
import org.drools.benchmarks.domain.B;
import org.drools.benchmarks.domain.C;
import org.drools.benchmarks.domain.D;
import org.drools.benchmarks.domain.E;
import org.drools.runtime.rule.FactHandle;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Setup;

public class UpdatesOnJoinBenchmark extends AbstractSessionBenchmark {

    @Param({"10", "100", "1000"})
    private int loopCount;

    @Param({"1", "10", "100"})
    private int rulesNr;

    @Param({"1", "10", "100"})
    private int factsNr;

    private A[] as;
    private B[] bs;
    private C[] cs;
    private D[] ds;
    private E[] es;

    private FactHandle[] aFHs;
    private FactHandle[] bFHs;
    private FactHandle[] cFHs;
    private FactHandle[] dFHs;
    private FactHandle[] eFHs;

    @Setup(Level.Iteration)
    @Override
    public void setup() {
        StringBuilder sb = new StringBuilder();
        sb.append( "import org.drools.benchmarks.domain.*;\n" );
        for (int i = 0; i < rulesNr; i++) {
            sb.append( "rule R" + i + " when\n" +
                       "  A( $a : value > " + i + ")\n" +
                       "  B( $b : value > $a)\n" +
                       "  C( $c : value > $b)\n" +
                       "  D( $d : value > $c)\n" +
                       "  E( $e : value > $d)\n" +
                       "then\n" +
                       "end\n" );
        }
        String drl = sb.toString();

        createKieBaseFromString(drl);

        createKieSession();

        as = new A[factsNr];
        bs = new B[factsNr];
        cs = new C[factsNr];
        ds = new D[factsNr];
        es = new E[factsNr];

        aFHs = new FactHandle[factsNr];
        bFHs = new FactHandle[factsNr];
        cFHs = new FactHandle[factsNr];
        dFHs = new FactHandle[factsNr];
        eFHs = new FactHandle[factsNr];
    }

    @Benchmark
    public void testCreateEmptySession() {
        for (int i = 0; i < factsNr; i++) {
            as[i] = new A( rulesNr + 1 );
            aFHs[i] = kieSession.insert( as[i] );
            bs[i] = new B( rulesNr + 3 );
            bFHs[i] = kieSession.insert( bs[i] );
            cs[i] = new C( rulesNr + 5 );
            cFHs[i] = kieSession.insert( cs[i] );
            ds[i] = new D( rulesNr + 7 );
            dFHs[i] = kieSession.insert( ds[i] );
            es[i] = new E( rulesNr + 9 );
            eFHs[i] = kieSession.insert( es[i] );
        }


        for (int i = 0; i < loopCount; i++) {
            for (int j = 0; j < factsNr; j++) {
                as[j].setValue( as[j].getValue() + 1 );
                kieSession.update( aFHs[j], as[j] );
                bs[j].setValue( bs[j].getValue() + 1 );
                kieSession.update( bFHs[j], bs[j] );
                cs[j].setValue( cs[j].getValue() + 1 );
                kieSession.update( cFHs[j], cs[j] );
                ds[j].setValue( ds[j].getValue() + 1 );
                kieSession.update( dFHs[j], ds[j] );
                es[j].setValue( es[j].getValue() + 1 );
                kieSession.update( eFHs[j], es[j] );
            }
        }

        kieSession.fireAllRules();
    }
}
