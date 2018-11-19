/*
 * GRAKN.AI - THE KNOWLEDGE GRAPH
 * Copyright (C) 2018 Grakn Labs Ltd
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package grakn.core.distribution;

import grakn.core.common.config.ConfigKey;
import grakn.core.common.config.Config;
import org.junit.Assert;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class DistributionE2EConstants {
    public static final Path GRAKN_TARGET_DIRECTORY = Paths.get("dist");
    public static final Path ZIP_FULLPATH = Paths.get(GRAKN_TARGET_DIRECTORY.toString(), "grakn-core-all.zip");
    public static final Path GRAKN_UNZIPPED_DIRECTORY = Paths.get(GRAKN_TARGET_DIRECTORY.toString(), "distribution test", "grakn-core-all");

    public static void assertGraknRunning() {
        Config config = Config.read(GRAKN_UNZIPPED_DIRECTORY.resolve("conf").resolve("grakn.properties"));
        boolean serverReady = isServerReady(config.getProperty(ConfigKey.SERVER_HOST_NAME), config.getProperty(ConfigKey.GRPC_PORT));
        assertThat("assertGraknRunning() failed because ", serverReady, equalTo(true));
    }

    public static void assertGraknStopped() {
        Config config = Config.read(GRAKN_UNZIPPED_DIRECTORY.resolve("conf").resolve("grakn.properties"));
        boolean serverReady = isServerReady(config.getProperty(ConfigKey.SERVER_HOST_NAME), config.getProperty(ConfigKey.GRPC_PORT));
        assertThat("assertGraknRunning() failed because ", serverReady, equalTo(false));
    }

    public static void assertZipExists() {
        if(!ZIP_FULLPATH.toFile().exists()) {
            Assert.fail("Grakn distribution '" + ZIP_FULLPATH.toAbsolutePath().toString() + "' could not be found. Please ensure it has been build (ie., run `mvn package`)");
        }
    }

    public static void unzipGrakn() throws IOException, InterruptedException, TimeoutException {
        System.out.println("Unzipped Grakn to: " + GRAKN_UNZIPPED_DIRECTORY.toAbsolutePath());
        new ProcessExecutor()
                .command("unzip", ZIP_FULLPATH.toString(), "-d", GRAKN_UNZIPPED_DIRECTORY.getParent().toString()).execute();
    }

    private static boolean isServerReady(String host, int port) {
        try {
            Socket s = new Socket(host, port);
            s.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}