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

import org.gradle.internal.operations.BuildOperationProcessor
import org.gradle.language.base.internal.compile.Compiler
import org.gradle.nativeplatform.internal.StaticLibraryArchiverSpec
import org.gradle.nativeplatform.toolchain.internal.compilespec.AssembleSpec
import org.gradle.nativeplatform.toolchain.internal.compilespec.CCompileSpec
import org.gradle.nativeplatform.toolchain.internal.compilespec.CPCHCompileSpec
import org.gradle.nativeplatform.toolchain.internal.compilespec.CppCompileSpec
import org.gradle.nativeplatform.toolchain.internal.compilespec.CppPCHCompileSpec
import org.gradle.nativeplatform.internal.LinkerSpec
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal
import org.gradle.nativeplatform.platform.internal.OperatingSystemInternal
import org.gradle.nativeplatform.toolchain.internal.*
import org.gradle.nativeplatform.toolchain.internal.tools.CommandLineToolConfigurationInternal
import org.gradle.nativeplatform.toolchain.internal.tools.GccCommandLineToolConfigurationInternal
import org.gradle.nativeplatform.toolchain.internal.tools.ToolRegistry
import org.gradle.nativeplatform.toolchain.internal.tools.ToolSearchPath
import org.gradle.nativeplatform.toolchain.GccCommandLineToolConfiguration
import org.gradle.process.internal.ExecActionFactory


class CcrxPlatformToolProvider extends AbstractPlatformToolProvider {
    private final Map<ToolType, CommandLineToolConfigurationInternal> commandLineToolConfigurations;
    private final NativePlatformInternal targetPlatform;
    private final ExecActionFactory execActionFactory;
    private final ToolRegistry toolRegistry;
    private final ToolSearchPath toolSearchPath;
    private final boolean useCommandFile;

    CcrxPlatformToolProvider(BuildOperationProcessor buildOperationProcessor, OperatingSystemInternal operatingSystem, ToolSearchPath toolSearchPath, ToolRegistry toolRegistry, NativePlatformInternal targetPlatform, ExecActionFactory execActionFactory, boolean useCommandFile) {
        super(buildOperationProcessor, operatingSystem);
        this.toolRegistry = toolRegistry;
        this.toolSearchPath = toolSearchPath;
        this.targetPlatform = targetPlatform;
        this.useCommandFile = useCommandFile;
        this.execActionFactory = execActionFactory;
    }

    @Override
    public String getSharedLibraryLinkFileName(String libraryName) {
        return "";//getSharedLibraryName(libraryName).replaceFirst("\\.dll$", ".lib");
    }

    @Override
    protected Compiler<CppCompileSpec> createCppCompiler() {
      GccCommandLineToolConfigurationInternal cppCompilerTool = toolRegistry.getTool(ToolType.CPP_COMPILER);
      CppCompiler cppCompiler = new CppCompiler(buildOperationProcessor, commandLineTool(cppCompilerTool), context(cppCompilerTool), getObjectFileExtension(), useCommandFile);
      return new OutputCleaningCompiler<CppCompileSpec>(cppCompiler, getObjectFileExtension());
    }

    @Override
    protected Compiler<?> createCppPCHCompiler() {
      GccCommandLineToolConfigurationInternal cppCompilerTool = toolRegistry.getTool(ToolType.CPP_COMPILER);
      CppPCHCompiler cppPCHCompiler = new CppPCHCompiler(buildOperationProcessor, commandLineTool(cppCompilerTool), context(cppCompilerTool), getPCHFileExtension(), useCommandFile);
      return new OutputCleaningCompiler<CppPCHCompileSpec>(cppPCHCompiler, getPCHFileExtension());
    }

    @Override
    protected Compiler<CCompileSpec> createCCompiler() {
      GccCommandLineToolConfigurationInternal cCompilerTool = toolRegistry.getTool(ToolType.C_COMPILER);
      CCompiler cCompiler = new CCompiler(buildOperationProcessor, commandLineTool(cCompilerTool), context(cCompilerTool), getObjectFileExtension(), useCommandFile);
      return new OutputCleaningCompiler<CCompileSpec>(cCompiler, getObjectFileExtension());
    }

    @Override
    protected Compiler<?> createCPCHCompiler() {
      GccCommandLineToolConfigurationInternal cCompilerTool = toolRegistry.getTool(ToolType.C_COMPILER);
      CPCHCompiler cpchCompiler = new CPCHCompiler(buildOperationProcessor, commandLineTool(cCompilerTool), context(cCompilerTool), getPCHFileExtension(), useCommandFile);
      return new OutputCleaningCompiler<CPCHCompileSpec>(cpchCompiler, getPCHFileExtension());
    }

    @Override
    protected Compiler<AssembleSpec> createAssembler() {
        GccCommandLineToolConfigurationInternal assemblerTool = toolRegistry.getTool(ToolType.ASSEMBLER);
        return new Assembler(buildOperationProcessor, commandLineTool(assemblerTool), context(assemblerTool), getObjectFileExtension(), false);
    }

    @Override
    protected Compiler<?> createObjectiveCppCompiler() {
        throw unavailableTool("Objective-C++ is not available on the Ccrx toolchain");
    }

    @Override
    protected Compiler<?> createObjectiveCCompiler() {
        throw unavailableTool("Objective-C is not available on the Ccrx toolchain");
    }

    @Override
    protected Compiler<LinkerSpec> createLinker() {
      GccCommandLineToolConfigurationInternal linkerTool = toolRegistry.getTool(ToolType.LINKER);
      return new Linker(buildOperationProcessor, commandLineTool(linkerTool), context(linkerTool), useCommandFile);
    }

    @Override
    protected Compiler<StaticLibraryArchiverSpec> createStaticLibraryArchiver() {
      throw unavailableTool("Static library is not available on the Ccrx toolchain");
      // GccCommandLineToolConfigurationInternal staticLibArchiverTool = toolRegistry.getTool(ToolType.STATIC_LIB_ARCHIVER);
      // return new ArStaticLibraryArchiver(buildOperationProcessor, commandLineTool(staticLibArchiverTool), context(staticLibArchiverTool));
    }

    private CommandLineToolInvocationWorker commandLineTool(GccCommandLineToolConfigurationInternal tool) {
      ToolType key = tool.getToolType();
      String exeName = tool.getExecutable();
      return new DefaultCommandLineToolInvocationWorker(key.getToolName(), toolSearchPath.locate(key, exeName).getTool(), execActionFactory);
    }

    private CommandLineToolContext context(CommandLineToolConfigurationInternal commandLineToolConfiguration) {
        MutableCommandLineToolContext invocationContext = new DefaultMutableCommandLineToolContext();
        invocationContext.addPath(toolSearchPath.getPath());
        invocationContext.setArgAction(commandLineToolConfiguration.getArgAction());
        return invocationContext;
    }

    public String getPCHFileExtension() {
        return ".pch";
    }
}
