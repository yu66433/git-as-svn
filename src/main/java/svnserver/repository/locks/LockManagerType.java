/**
 * This file is part of git-as-svn. It is subject to the license terms
 * in the LICENSE file found in the top-level directory of this distribution
 * and at http://www.gnu.org/licenses/gpl-2.0.html. No part of git-as-svn,
 * including this file, may be copied, modified, propagated, or distributed
 * except according to the terms contained in the LICENSE file.
 */
package svnserver.repository.locks;

import org.jetbrains.annotations.NotNull;
import org.mapdb.TxMaker;

/**
 * @author Marat Radchenko <marat@slonopotamus.org>
 */
public enum LockManagerType {
  InMemory {
    @NotNull
    @Override
    public LockManagerFactory create(@NotNull TxMaker cacheDb) {
      return new InMemoryLockFactory();
    }
  },

  Persistent {
    @NotNull
    @Override
    public LockManagerFactory create(@NotNull TxMaker cacheDb) {
      return new PersistentLockFactory(cacheDb);
    }
  };

  @NotNull
  public abstract LockManagerFactory create(@NotNull TxMaker cacheDb);
}
