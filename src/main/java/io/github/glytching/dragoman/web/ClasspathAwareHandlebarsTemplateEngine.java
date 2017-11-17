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
package io.github.glytching.dragoman.web;

import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.github.jknack.handlebars.io.TemplateSource;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.impl.Utils;
import io.vertx.ext.web.templ.HandlebarsTemplateEngine;
import io.vertx.ext.web.templ.impl.HandlebarsTemplateEngineImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.String.format;

/**
 * Vertx's {@link HandlebarsTemplateEngine} loads templates from the file system, this approach
 * won't work when the templates are embedded in an uber JAR so this extension of {@link
 * HandlebarsTemplateEngine} provides a classpath aware loader. The extension is crude because
 * {@link HandlebarsTemplateEngine} does not expose a hook for overriding the loader.
 */
public class ClasspathAwareHandlebarsTemplateEngine extends HandlebarsTemplateEngineImpl {
    private static final Logger logger =
            LoggerFactory.getLogger(ClasspathAwareHandlebarsTemplateEngine.class);

    private final Handlebars handlebars;
    private final Loader loader = new Loader();

    public ClasspathAwareHandlebarsTemplateEngine() {
        super();
        handlebars = new Handlebars(loader);
    }

    @Override
    public void render(
            RoutingContext context, String templateFileName, Handler<AsyncResult<Buffer>> handler) {
        try {
            Template template = cache.get(templateFileName);
            if (template == null) {
                synchronized (this) {
                    loader.setVertx(context.vertx());
                    template = handlebars.compile(templateFileName);
                    cache.put(templateFileName, template);
                }
            }
            Context engineContext = Context.newBuilder(context.data()).resolver(getResolvers()).build();
            handler.handle(Future.succeededFuture(Buffer.buffer(template.apply(engineContext))));
        } catch (Exception ex) {
            handler.handle(Future.failedFuture(ex));
        }
    }

    private class Loader implements TemplateLoader {

        private Vertx vertx;

        void setVertx(Vertx vertx) {
            this.vertx = vertx;
        }

        @Override
        public TemplateSource sourceAt(String location) throws IOException {
            String loc = adjustLocation(location);

            logger.debug("Loading template from: {}", loc);

            AtomicReference<String> templ = new AtomicReference<>();

            try {
                // find the template on the classpath
                Buffer buffer = Utils.readResourceToBuffer(loc);
                if (buffer != null) {
                    templ.set(buffer.toString());
                }
            } catch (Exception ex) {
                logger.warn(
                        format("Cannot find template: %s on classpath due to: %s!", loc, ex.getMessage()), ex);
            }

            if (templ.get() == null) {
                throw new IllegalArgumentException("Cannot find resource " + loc);
            }

            long lastMod = System.currentTimeMillis();

            return new TemplateSource() {
                @Override
                public String content() throws IOException {
                    return templ.get();
                }

                @Override
                public String filename() {
                    return loc;
                }

                @Override
                public long lastModified() {
                    return lastMod;
                }
            };
        }

        @Override
        public String resolve(String location) {
            return location;
        }

        @Override
        public String getPrefix() {
            return null;
        }

        @Override
        public void setPrefix(String prefix) {
            // does nothing since TemplateLoader handles the prefix
        }

        @Override
        public String getSuffix() {
            return extension;
        }

        @Override
        public void setSuffix(String suffix) {
            extension = suffix;
        }
    }
}
