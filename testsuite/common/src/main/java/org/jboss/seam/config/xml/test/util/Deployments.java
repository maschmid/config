/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.config.xml.test.util;

import org.jboss.seam.config.xml.test.AbstractXMLTest;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

public class Deployments {
    
        public static WebArchive baseDeployment(Class<?> klass) {
            return ShrinkWrap.create(WebArchive.class, "test.war")                  
                .addAsLibraries(
                    /* We cannot use MavenDependencyResolver for getting artifacts from the reactor,                            
                     * so we use the hacky MavenArtifactResolver for this */                            
                    MavenArtifactResolver.resolve("org.jboss.seam.config", "seam-config-xml"))
                .addAsLibraries(
                    DependencyResolvers.use(MavenDependencyResolver.class)
                    .loadReposFromPom("pom.xml")
                    .artifact("org.jboss.seam.solder:seam-solder")
                    .resolveAs(JavaArchive.class))                         
                .addClass(AbstractXMLTest.class)
                .addClass(klass);
        }
    
        public static WebArchive baseDeployment(Class<?> klass, String xmlFileName) {
            
            String fileName = klass.getPackage().getName().replace('.', '/') + "/" + xmlFileName;
            
            WebArchive archive = baseDeployment(klass)
                // weld-embedded reads it from META-INF/seam-beans.xml, not from WEB-INF/classes/META-INF/seam-beans.xml as it should
                // so we add it to both locations
                .addAsResource(fileName, "META-INF/seam-beans.xml")
                .addAsManifestResource(fileName, "seam-beans.xml");
                        
            return archive;
        }

}
