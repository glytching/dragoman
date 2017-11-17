/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.glytching.dragoman.util;

import java.io.IOException;
import java.net.ServerSocket;

/** Utility class for network related code. */
public class NetworkUtils {

  /**
   * Gets a free port. Note: although the returned port is free immediately after this method
   * completes there is no guarantee that it will remain free so callers are advised to use it as
   * soon as posisble after getting it.
   *
   * @return a free port
   */
  public static int getFreePort() {
    try {
      ServerSocket socket = new ServerSocket(0);
      int port = socket.getLocalPort();
      socket.close();
      return port;
    } catch (IOException ex) {
      throw new RuntimeException("Failed to get free port!", ex);
    }
  }
}
