/*
 * Copyright 2000-2012 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package jetbrains.buildServer.buildTriggers.vcs.git.agent.command.impl;

import com.intellij.execution.configurations.GeneralCommandLine;
import jetbrains.buildServer.ExecResult;
import jetbrains.buildServer.buildTriggers.vcs.git.agent.command.SetConfigCommand;
import jetbrains.buildServer.vcs.VcsException;
import org.jetbrains.annotations.NotNull;

/**
 * @author dmitry.neverov
 */
public class SetConfigCommandImpl implements SetConfigCommand {

  private final GeneralCommandLine myCmd;
  private String myPropertyName;
  private String myValue;

  public SetConfigCommandImpl(@NotNull GeneralCommandLine cmd) {
    myCmd = cmd;
  }

  @NotNull
  public SetConfigCommand setPropertyName(@NotNull String name) {
    myPropertyName = name;
    return this;
  }

  @NotNull
  public SetConfigCommand setValue(@NotNull String value) {
    myValue = value;
    return this;
  }

  public void call() throws VcsException {
    myCmd.addParameters("config", myPropertyName, myValue);
    ExecResult r = CommandUtil.runCommand(myCmd);
    CommandUtil.failIfNotEmptyStdErr(myCmd, r);
  }
}
