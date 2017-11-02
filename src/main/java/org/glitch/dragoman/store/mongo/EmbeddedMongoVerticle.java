package org.glitch.dragoman.store.mongo;

import de.flapdoodle.embed.mongo.Command;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.runtime.Network;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import org.glitch.dragoman.configuration.ApplicationConfiguration;
import org.glitch.dragoman.configuration.guice.MongoModule;
import org.glitch.dragoman.dataset.canned.CannedDatasetsWriter;
import org.glitch.dragoman.util.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

public class EmbeddedMongoVerticle extends AbstractVerticle {
    private static final Logger logger = LoggerFactory.getLogger(MongoModule.class);
    private static final Logger mongoLogger = LoggerFactory.getLogger("embedded-mongo");

    // https://github.com/flapdoodle-oss/de.flapdoodle.embed.mongo#usage---optimization
    //    private static final MongodStarter starter = MongodStarter.getInstance(
    //            new RuntimeConfigBuilder()
    //                    .defaults(Command.MongoD)
    //                    .processOutput(ProcessOutput.getInstance("embedded-mongo", mongoLogger))
    //                    .build()
    //    );

    private final int port;
    private final CannedDatasetsWriter cannedDatasetsWriter;
    private MongodExecutable mongodExe;
    private MongodProcess mongod;

    @Inject
    public EmbeddedMongoVerticle(ApplicationConfiguration configuration, CannedDatasetsWriter cannedDatasetsWriter) {
        this.port = configuration.getMongoPort();
        this.cannedDatasetsWriter = cannedDatasetsWriter;
    }

    @Override
    public void start(Future<Void> future) throws Exception {
        // take the (rather slow) call to start embedded Mongo off the event loop thread
        vertx.executeBlocking((Handler<Future<Void>>) blockingFuture -> {
            try {
                StopWatch stopWatch = StopWatch.startForSplits();
                MongodStarter starter = MongodStarter.getInstance(
                        new RuntimeConfigBuilder()
                                .defaults(Command.MongoD)
                                .processOutput(ProcessOutput.getInstance("embedded-mongo", mongoLogger))
                                .build()
                );

                mongodExe = starter.prepare(new MongodConfigBuilder()
                        .version(Version.Main.PRODUCTION)
                        .net(new Net("localhost", port, Network.localhostIsIPv6()))
                        .build());
                logger.info("Prepared embedded Mongo starter in {}ms", stopWatch.split());

                mongod = mongodExe.start();
                logger.info("Started embedded Mongo in {}ms in port: {}", stopWatch.split(), port);

                blockingFuture.complete();
            } catch (Exception ex) {
                logger.warn("Exception occurred when starting embedded Mongo!", ex);
                future.fail(ex);
                return;
            }
        }, blockingFuture -> {
            if (blockingFuture.succeeded()) {
                // if embedded Mongo started succssfully then let's populate it with canned datasets (if any)
                StopWatch stopWatch = StopWatch.start();
                int count = cannedDatasetsWriter.write();
                logger.info("Loaded {} canned datasets into embedded Mongo in {}ms", count, stopWatch.stop());
            }
            future.complete();
        });
    }

    @Override
    public void stop(Future<Void> future) throws Exception {
        if (mongod != null) {
            mongod.stop();
            mongod = null;
        }
        if (mongodExe != null) {
            mongodExe.stop();
            mongodExe = null;
        }
        future.complete();
    }
}