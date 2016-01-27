/*
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
 * Copyright 2016 Azul Systems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/**
 * @test TestOnSpinWait
 * @summary C2: (x86 only) checks that java.lang.Runtime.onSpinWait is intrinsified
 * @bug 8147844
 * @library /testlibrary
 * @requires os.arch=="x86" | os.arch=="amd64" | os.arch=="x86_64"
 * @run main/othervm TestOnSpinWait
 */

import java.lang.invoke.*;
import jdk.test.lib.*;
import static jdk.test.lib.Asserts.*;

public class TestOnSpinWait {

    public static void main(String[] args) throws Exception {
        ProcessBuilder pb = ProcessTools.createJavaProcessBuilder(
          "-XX:+IgnoreUnrecognizedVMOptions", "-showversion",
          "-server", "-XX:-TieredCompilation", "-Xbatch",
          "-XX:+UseOnSpinWaitIntrinsic", "-Xcomp",
          "-XX:+PrintCompilation", "-XX:+UnlockDiagnosticVMOptions", 
          "-XX:+PrintInlining", "TestOnSpinWait$Launcher");

        OutputAnalyzer analyzer = new OutputAnalyzer(pb.start());

        analyzer.shouldHaveExitValue(0);

        // The test is applicable only to C2 (present in Server VM).
        if (analyzer.getStderr().contains("Server VM")) {
            analyzer.shouldContain("java.lang.Runtime::onSpinWait (1 bytes)   (intrinsic)");
        }
    }

    static class Launcher {

        public static void main(final String[] args) throws Exception {
            int end = 20_000;

            for (int i=0; i < end; i++) {
                java.lang.Runtime.onSpinWait();
            }
        }
    }
}
