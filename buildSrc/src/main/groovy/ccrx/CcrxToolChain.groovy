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

import org.gradle.api.Action
import org.gradle.api.internal.file.FileResolver
import org.gradle.internal.operations.BuildOperationProcessor
import org.gradle.internal.os.OperatingSystem
import org.gradle.internal.reflect.Instantiator
import org.gradle.nativeplatform.platform.internal.NativePlatformInternal
import org.gradle.nativeplatform.toolchain.GccPlatformToolChain
import org.gradle.nativeplatform.toolchain.internal.ExtendableToolChain
import org.gradle.nativeplatform.toolchain.internal.NativeToolChainInternal
import org.gradle.nativeplatform.toolchain.internal.PlatformToolProvider
import org.gradle.nativeplatform.toolchain.internal.tools.DefaultGccCommandLineToolConfiguration
import org.gradle.nativeplatform.toolchain.internal.tools.ToolSearchPath
import org.gradle.nativeplatform.toolchain.internal.ToolType
import org.gradle.nativeplatform.toolchain.internal.UnavailablePlatformToolProvider
import org.gradle.nativeplatform.toolchain.NativePlatformToolChain;
import org.gradle.platform.base.internal.toolchain.ToolChainAvailability
import org.gradle.process.internal.ExecActionFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

public class CcrxToolChain extends ExtendableToolChain<CcrxPlatformToolChain> implements Ccrx, NativeToolChainInternal {

  private final String name;
  private final OperatingSystem operatingSystem;

  protected static final Logger LOGGER = LoggerFactory.getLogger(CcrxToolChain.class);

  public static final String DEFAULT_NAME = "ccrx";

  private final ExecActionFactory execActionFactory;
  private final ToolSearchPath toolSearchPath;
  private final Instantiator instantiator;
  private ToolChainAvailability availability;

  public CcrxToolChain(Instantiator instantiator, String name, BuildOperationProcessor buildOperationProcessor, OperatingSystem operatingSystem, FileResolver fileResolver, ExecActionFactory execActionFactory) {
    this(instantiator, name, buildOperationProcessor, operatingSystem, fileResolver, execActionFactory, new ToolSearchPath(operatingSystem));
  }

  CcrxToolChain(Instantiator instantiator, String name, BuildOperationProcessor buildOperationProcessor, OperatingSystem operatingSystem, FileResolver fileResolver, ExecActionFactory execActionFactory, ToolSearchPath tools) {
    super(name, buildOperationProcessor, operatingSystem, fileResolver);

    this.name = name;
    this.operatingSystem = operatingSystem;
    this.execActionFactory = execActionFactory;
    this.toolSearchPath = tools;
    this.instantiator = instantiator;
  }

  // @Override
  // public List<File> getPath() {
  //   return toolSearchPath.getPath();
  // }
  //
  // @Override
  // public void path(Object... pathEntries) {
  //   for (Object path : pathEntries) {
  //     toolSearchPath.path(resolve(path));
  //   }
  // }

  @Override
  protected String getTypeName() {
    return "Renesas CCRX";
  }

  // @Override
  // public void target(String platformName) {
  //     target(platformName, Actions.<NativePlatformToolChain>doNothing());
  // }
  //
  // @Override
  // public void target(String platformName, Action<? super GccPlatformToolChain> action) {
  //
  // }

  @Override
  public PlatformToolProvider select(NativePlatformInternal targetPlatform) {
      ToolChainAvailability result = new ToolChainAvailability();
      result.mustBeAvailable(getAvailability());
      if (!result.isAvailable()) {
          return new UnavailablePlatformToolProvider(targetPlatform.getOperatingSystem(), result);
      }

      DefaultCcrxPlatformToolChain configurableToolChain = instantiator.newInstance(DefaultCcrxPlatformToolChain.class, targetPlatform, instantiator);
      addDefaultTools(configurableToolChain);
      configureActions.execute(configurableToolChain);

      return new CcrxPlatformToolProvider(buildOperationProcessor, targetPlatform.getOperatingSystem(),
        toolSearchPath, configurableToolChain, targetPlatform,
        null, configurableToolChain.isCanUseCommandFile());
  }

  private void addDefaultTools(DefaultCcrxPlatformToolChain toolChain) {
    toolChain.add(instantiator.newInstance(DefaultGccCommandLineToolConfiguration.class, ToolType.C_COMPILER, "ccrx"));
    toolChain.add(instantiator.newInstance(DefaultGccCommandLineToolConfiguration.class, ToolType.CPP_COMPILER, "ccrx"));
    toolChain.add(instantiator.newInstance(DefaultGccCommandLineToolConfiguration.class, ToolType.LINKER, "rlink"));
    toolChain.add(instantiator.newInstance(DefaultGccCommandLineToolConfiguration.class, ToolType.ASSEMBLER, "ccrx"));
  }

  private ToolChainAvailability getAvailability() {
      if (availability == null) {
          availability = new ToolChainAvailability();
          checkAvailable(availability);
      }
      return availability;
  }

  private void checkAvailable(ToolChainAvailability availability) {
    // Search for toolchain available
  }

  @Override
  public String getName() {
      return name;
  }

  @Override
  public String getDisplayName() {
      return "Tool chain '" + getName() + "' (" + getTypeName() + ")";
  }

}
