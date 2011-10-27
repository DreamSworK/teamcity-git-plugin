/*
 * Copyright 2000-2011 JetBrains s.r.o.
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

package jetbrains.buildServer.buildTriggers.vcs.git.tests;

import jetbrains.buildServer.TempFiles;
import jetbrains.buildServer.buildTriggers.vcs.git.*;
import jetbrains.buildServer.serverSide.ServerPaths;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.URIish;
import org.jetbrains.annotations.NotNull;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.concurrent.locks.ReadWriteLock;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertSame;

/**
 * @author dmitry.neverov
 */
@Test
public class RepositoryManagerTest {

  private TempFiles myTempFiles;
  private RepositoryManager myRepositoryManager;


  @BeforeMethod
  public void setUp() throws Exception {
    myTempFiles = new TempFiles();
    ServerPaths paths = new ServerPaths(myTempFiles.createTempDir().getAbsolutePath());
    ServerPluginConfig config = new PluginConfigBuilder(paths).build();
    MirrorManager mirrorManager = new MirrorManagerImpl(config, new HashCalculatorImpl());
    myRepositoryManager = new RepositoryManagerImpl(config, mirrorManager);
  }


  public void tearDown() {
    myTempFiles.cleanup();
  }


  private void should_use_same_dir_for_same_urls() throws Exception {
    Repository noAuthRepository = myRepositoryManager.openRepository(new URIish("ssh://some.org/repository.git"));
    String path = noAuthRepository.getDirectory().getCanonicalPath();
    assertEquals(path, getRepositoryPath("ssh://some.org/repository.git"));
    assertEquals(path, getRepositoryPath("ssh://name@some.org/repository.git"));
    assertEquals(path, getRepositoryPath("ssh://name:pass@some.org/repository.git"));
    assertEquals(path, getRepositoryPath("ssh://other-name@some.org/repository.git"));
    assertEquals(path, getRepositoryPath("ssh://other-name:pass@some.org/repository.git"));
  }


  public void should_return_same_lock_for_files_point_to_same_dir() throws Exception {
    File dir1 = new File(".");
    ReadWriteLock rmLock1 = myRepositoryManager.getRmLock(dir1);
    ReadWriteLock rmLock2 = myRepositoryManager.getRmLock(new File(".." + File.separator + dir1.getCanonicalFile().getName()));
    assertSame(rmLock1, rmLock2);
  }


  private String getRepositoryPath(@NotNull final String url) throws Exception {
    Repository repository = myRepositoryManager.openRepository(new URIish(url));
    return repository.getDirectory().getCanonicalPath();
  }
}
