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
package org.jboss.seam.config.xml.test;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.weld.environment.se.Weld;
import org.jboss.weld.environment.se.WeldContainer;
import org.junit.After;
import org.junit.Before;

public abstract class AbstractXMLTest {
    protected BeanManager manager;

    Weld weld;

    protected abstract String getXmlFileName();

    @Before
    public void setup() {
        String fileName = getClass().getPackage().getName().replace('.', '/') + "/" + getXmlFileName();
        SimpleXmlProvider.fileName = fileName;
        weld = new Weld();
        WeldContainer container = weld.initialize();
        manager = container.getBeanManager();
    }

    @After
    public void teardown() {
        weld.shutdown();
    }

    public <T> T getReference(Class<T> clazz, Annotation... bindings) {
        Set<Bean<?>> beans = manager.getBeans(clazz, bindings);
        if (beans.isEmpty()) {
            throw new RuntimeException("No bean found with class: " + clazz + " and bindings " + Arrays.toString(bindings));
        } else if (beans.size() != 1) {
            StringBuilder bs = new StringBuilder("[");
            for (Annotation a : bindings) {
                bs.append(a.toString() + ",");
            }
            bs.append("]");
            throw new RuntimeException("More than one bean found with class: " + clazz + " and bindings " + bs);
        }
        Bean<?> bean = beans.iterator().next();
        return (T) manager.getReference(bean, clazz, manager.createCreationalContext(bean));
    }

}
