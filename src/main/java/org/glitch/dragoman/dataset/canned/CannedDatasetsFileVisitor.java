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
package org.glitch.dragoman.dataset.canned;

import com.google.common.collect.Lists;
import org.glitch.dragoman.transform.JsonTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

public class CannedDatasetsFileVisitor extends SimpleFileVisitor<Path> {
    private static final Logger logger = LoggerFactory.getLogger(CannedDatasetsFileVisitor.class);

    private final JsonTransformer jsonTransformer;
    private final List<CannedDataset> canned;

    public CannedDatasetsFileVisitor(JsonTransformer jsonTransformer) {
        this.jsonTransformer = jsonTransformer;
        this.canned = Lists.newArrayList();
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        if (file.getFileName().toString().endsWith("json")) {
            String content = new String(Files.readAllBytes(file));
            CannedDataset cannedDataset = jsonTransformer.transform(CannedDataset.class, content);
            canned.add(cannedDataset.finish());
        } else {
            logger.info("Ignoring a file of unknown type: {} in the canned datasets directory!", file.getFileName()
                    .toString());
        }

        return FileVisitResult.CONTINUE;
    }

    public List<CannedDataset> getCanned() {
        return canned;
    }
}