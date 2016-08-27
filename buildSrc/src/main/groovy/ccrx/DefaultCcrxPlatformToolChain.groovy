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

import org.gradle.internal.reflect.Instantiator
import org.gradle.nativeplatform.platform.NativePlatform;
import org.gradle.nativeplatform.toolchain.GccCommandLineToolConfiguration;
import org.gradle.nativeplatform.toolchain.internal.ToolType;
import org.gradle.nativeplatform.toolchain.internal.tools.DefaultGccCommandLineToolConfiguration
import org.gradle.nativeplatform.toolchain.internal.tools.GccCommandLineToolConfigurationInternal
import org.gradle.nativeplatform.toolchain.internal.tools.ToolRegistry

public class DefaultCcrxPlatformToolChain implements CcrxPlatformToolChain, ToolRegistry {
    private final NativePlatform platform;
    protected final Map<ToolType, GccCommandLineToolConfigurationInternal> tools = new HashMap<ToolType, GccCommandLineToolConfigurationInternal>();

    public DefaultCcrxPlatformToolChain(NativePlatform platform, Instantiator instantiator) {
        this.platform = platform;
    }

    public void add(DefaultGccCommandLineToolConfiguration tool) {
      tools.put(tool.getToolType(), tool);
    }

    public boolean isCanUseCommandFile() {
      return false;
    }

    @Override
    public GccCommandLineToolConfigurationInternal getTool(ToolType toolType) {
      return tools.get(toolType);
    }

    @Override
    public GccCommandLineToolConfiguration getcCompiler() {
        return tools.get(ToolType.C_COMPILER);
    }

    @Override
    public GccCommandLineToolConfiguration getCppCompiler() {
        return tools.get(ToolType.CPP_COMPILER);
    }

    @Override
    public GccCommandLineToolConfiguration getAssembler() {
        return tools.get(ToolType.ASSEMBLER);
    }

    @Override
    public GccCommandLineToolConfiguration getLinker() {
        return tools.get(ToolType.LINKER);
    }

    @Override
    public NativePlatform getPlatform() {
        return platform;
    }
}
