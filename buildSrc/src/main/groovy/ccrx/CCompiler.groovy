/*
 * Copyright 2011 the original author or authors.
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
package test.ccrx

import org.gradle.internal.Transformers;
import org.gradle.internal.operations.BuildOperationProcessor;
import org.gradle.nativeplatform.toolchain.internal.CommandLineToolInvocationWorker;
import org.gradle.nativeplatform.toolchain.internal.CommandLineToolContext;
import org.gradle.nativeplatform.toolchain.internal.compilespec.CCompileSpec;

class CCompiler extends CcrxNativeCompiler<CCompileSpec> {

    CCompiler(BuildOperationProcessor buildOperationProcessor, CommandLineToolInvocationWorker commandLineToolInvocationWorker, CommandLineToolContext invocationContext, String objectFileExtension, boolean useCommandFile) {
        super(buildOperationProcessor, commandLineToolInvocationWorker, invocationContext, new CCompileArgsTransformer(), Transformers.<CCompileSpec>noOpTransformer(), objectFileExtension, useCommandFile);
    }

    private static class CCompileArgsTransformer extends CcrxCompilerArgsTransformer<CCompileSpec> {
        @Override
        protected String getLanguage() {
            return "c";
        }
    }
}
