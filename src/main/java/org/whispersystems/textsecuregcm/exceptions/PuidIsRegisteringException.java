/**
 * Copyright (C) 2013 Open WhisperSystems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.textsecuregcm.exceptions;

import java.util.LinkedList;
import java.util.List;

public class PuidIsRegisteringException extends Exception {

  private List<String> missing;

  public PuidIsRegisteringException(String user) {
    super(user);
    missing = new LinkedList<>();
    missing.add(user);
  }

  public PuidIsRegisteringException(List<String> missing) {
    this.missing = missing;
  }

  public PuidIsRegisteringException(Exception e) {
    super(e);
  }

  public List<String> getMissing() {
    return missing;
  }
}
