/*
 * Copyright 2013 Harald Wellmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.pax.shiro.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.BindException;
import java.util.Arrays;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public abstract class AbstractHtmlUnitContainerTest {

    public static final int MAX_PORT = 9200;

    protected static PauseableServer server;

    private static int port = 9180;

    protected WebDriver webDriver = new HtmlUnitDriver();
    
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void logOut() throws IOException {
        // Make sure we are logged out
        webDriver.get(getBaseUri());
        try {
            webDriver.findElement(By.partialLinkText("Log out")).click();
        }
        catch (NoSuchElementException e) {
            //Ignore
        }
    }

    @BeforeClass
    public static void startContainer() throws Exception {
        while (server == null && port < MAX_PORT) {
            try {
                server = createAndStartServer(port);
            }
            catch (BindException e) {
                System.err.printf("Unable to listen on port %d.  Trying next port.", port);
                port++;
            }
        }
        assertTrue(server.isStarted());
    }

    private static PauseableServer createAndStartServer(final int port) throws Exception {
        PauseableServer server = new PauseableServer();
        Connector connector = new SelectChannelConnector();
        connector.setPort(port);
        server.setConnectors(new Connector[] { connector });
        WebAppContext webAppContext = new WebAppContext("src/main/webapp", "/");
        webAppContext.setOverrideDescriptors(Arrays
            .asList("src/main/jetty/override-myfaces-web.xml"));
        server.setHandler(webAppContext);
        server.start();
        return server;
    }

    protected static String getBaseUri() {
        return "http://localhost:" + port + "/";
    }

    public void pauseServer(boolean paused) {
        if (server != null)
            server.pause(paused);
    }

    public static class PauseableServer extends Server {

        public synchronized void pause(boolean paused) {
            try {
                if (paused) {
                    for (Connector connector : getConnectors()) {
                        connector.stop();
                    }
                }
                else {
                    for (Connector connector : getConnectors()) {
                        connector.start();
                    }
                }
            }
            catch (Exception e) {
            }
        }
    }
    
    protected void logIn(String username, String password) {
        webDriver.get(getBaseUri() + "login.jsf");
        webDriver.findElement(By.name("login:username")).sendKeys(username);
        webDriver.findElement(By.name("login:password")).sendKeys(password);
        webDriver.findElement(By.name("login:submit")).click();        
    }
}
