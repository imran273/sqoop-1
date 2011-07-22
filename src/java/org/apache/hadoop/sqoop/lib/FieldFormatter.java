/**
 * Licensed to Cloudera, Inc. under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  Cloudera, Inc. licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.sqoop.lib;


/**
 * Static helper class that will help format data with quotes and escape chars.
 */
public final class FieldFormatter {

  private FieldFormatter() { }

  /** 
   * Takes an input string representing the value of a field, encloses it in
   * enclosing chars, and escapes any occurrences of such characters in the middle.
   * The escape character itself is also escaped if it appears in the text of the
   * field.
   *
   * The field is enclosed only if:
   *   enclose != '\000', and:
   *     encloseRequired is true, or
   *     one of the characters in the mustEscapeFor list is present in the string.
   *
   * Escaping is not performed if the escape char is '\000'.
   *
   * @param str - The user's string to escape and enclose
   * @param escape - What string to use as the escape sequence. If "" or null, then don't escape.
   * @param enclose - The string to use to enclose str e.g. "quoted". If "" or null, then don't
   *     enclose.
   * @param mustEncloseFor - A list of characters; if one is present in 'str', then str must be
   *     enclosed
   * @param encloseRequired - If true, then always enclose, regardless of mustEscapeFor
   * @return the escaped, enclosed version of 'str'
   */
  public static final String escapeAndEnclose(String str, String escape, String enclose,
      char [] mustEncloseFor, boolean encloseRequired) {

    // true if we can use an escape character.
    boolean escapingLegal = (null != escape && escape.length() > 0 && !escape.equals("\000"));
    String withEscapes;

    if (null == str) {
      return null;
    }

    if (escapingLegal) {
      // escaping is legal. Escape any instances of the escape char itself
      withEscapes = str.replace(escape, escape + escape);
    } else {
      // no need to double-escape
      withEscapes = str;
    }

    if (null == enclose || enclose.length() == 0 || enclose.equals("\000")) {
      // The enclose-with character was left unset, so we can't enclose items. We're done.
      return withEscapes;
    }

    // if we have an enclosing character, and escaping is legal, then the encloser must
    // always be escaped.
    if (escapingLegal) {
      withEscapes = withEscapes.replace(enclose, escape + enclose);
    }

    boolean actuallyDoEnclose = encloseRequired;
    if (!actuallyDoEnclose && mustEncloseFor != null) {
      // check if the string requires enclosing
      for (char reason : mustEncloseFor) {
        if (str.indexOf(reason) != -1) {
          actuallyDoEnclose = true;
          break;
        }
      }
    }

    if (actuallyDoEnclose) {
      return enclose + withEscapes + enclose;
    } else {
      return withEscapes;
    }
  }
}
