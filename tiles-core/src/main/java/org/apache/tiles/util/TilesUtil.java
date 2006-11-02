/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.apache.tiles.util;

import org.apache.tiles.TilesApplicationContext;
import org.apache.tiles.TilesRequestContext;
import org.apache.tiles.preparer.PreparerFactory;
import org.apache.tiles.preparer.BasicPreparerFactory;
import org.apache.tiles.definition.*;

import javax.servlet.jsp.PageContext;
import java.io.IOException;


/**
 * Class containing utility methods for Tiles.
 * Methods of this class are static and thereby accessible from anywhere.
 * The underlying implementation can be changed with
 * {@link #setTilesUtil(TilesUtilImpl)}.
 * <br>
 * Real implementation classes should derive from the {@link TilesUtilImpl} class.
 * <br>
 * Some methods are specified to throw the <code>UnsupportedOperationException</code>
 * if the underlying implementation doesn't support the operation.
 */
public class TilesUtil {

    /**
     * The implementation of tilesUtilImpl
     */
    protected static TilesUtilImpl tilesUtilImpl;

    /**
     * Get the real implementation.
     *
     * @return The underlying implementation object.
     */
    static public TilesUtilImpl getTilesUtil() {
        return tilesUtilImpl;
    }

    /**
     * Set the real implementation.
     * This method should be called only once.
     * Successive calls have no effect.
     *
     * @param tilesUtil The implementaion.
     */
    static public void setTilesUtil(TilesUtilImpl tilesUtil) {
        if (implAlreadySet) {
            return;
        }
        tilesUtilImpl = tilesUtil;
        implAlreadySet = true;
    }

    /**
     * Getter to know if the underlying implementation is already set to another
     * value than the default value.
     *
     * @return <code>true</code> if {@link #setTilesUtil} has already been called.
     */
    static boolean isTilesUtilImplSet() {
        return implAlreadySet;
    }

    /**
     * Flag to know if internal implementation has been set by the setter method
     */
    private static boolean implAlreadySet = false;

    public static TilesApplicationContext getApplicationContext() {
        return tilesUtilImpl.getApplicationContext();
    }

    public static TilesRequestContext createRequestContext(Object request, Object response) {
        return tilesUtilImpl.createRequestContext(request, response);
    }

    /**
     * Do a forward using request dispatcher.
     * <p/>
     * This method is used by the Tiles package anytime a forward is required.
     *
     * @param uri          Uri or Definition name to forward.
     * @param tilesContext Current Tiles application context.
     */
    public static void doForward(
        String uri,
        TilesRequestContext tilesContext)
        throws IOException, Exception {

        tilesUtilImpl.doForward(uri, tilesContext);
    }

    /**
     * Do an include using request dispatcher.
     * <p/>
     * This method is used by the Tiles package when an include is required.
     * The Tiles package can use indifferently any form of this method.
     *
     * @param uri          Uri or Definition name to forward.
     * @param tilesContext Current Tiles application context.
     */
    public static void doInclude(
        String uri,
        TilesRequestContext tilesContext)
        throws IOException, Exception {

        tilesUtilImpl.doInclude(uri, tilesContext);
    }

    /**
     * Do an include using PageContext.include().
     * <p/>
     * This method is used by the Tiles package when an include is required.
     * The Tiles package can use indifferently any form of this method.
     *
     * @param uri         Uri or Definition name to forward.
     * @param pageContext Current page context.
     */
    public static void doInclude(String uri, PageContext pageContext)
        throws IOException, Exception {
        doInclude(uri, pageContext, true);
    }

    /**
     * Do an include using PageContext.include().
     * <p/>
     * This method is used by the Tiles package when an include is required.
     * The Tiles package can use indifferently any form of this method.
     *
     * @param uri         Uri or Definition name to forward.
     * @param flush       If the writer should be flushed before the include
     * @param pageContext Current page context.
     */
    public static void doInclude(String uri, PageContext pageContext, boolean flush)
        throws IOException, Exception {
        tilesUtilImpl.doInclude(uri, pageContext, flush);
    }

    /**
     * Get definition impl from appropriate servlet context.
     *
     * @return Definitions impl or <code>null</code> if not found.
     */
    public static DefinitionsFactory getDefinitionsFactory() {
        return tilesUtilImpl.getDefinitionsFactory();
    }

    /**
     * Get preparer factory
     */
    public static PreparerFactory getPreparerFactory() {
        return tilesUtilImpl.getPreparerFactory();
    }

    /**
     * Create Definition impl from specified configuration object.
     * Create a ConfigurableDefinitionsFactory and initialize it with the configuration
     * object. This later can contain the impl classname to use.
     * Factory is made accessible from tags.
     * <p/>
     * Fallback of several impl creation methods.
     *
     * @param factoryConfig Configuration object passed to impl.
     * @return newly created impl of type ConfigurableDefinitionsFactory.
     * @throws org.apache.tiles.definition.DefinitionsFactoryException
     *          If an error occur while initializing impl
     */
    public static DefinitionsFactory createDefinitionsFactory(
        DefinitionsFactoryConfig factoryConfig)
        throws DefinitionsFactoryException {
        return tilesUtilImpl.createDefinitionsFactory(factoryConfig);
    }

    /**
     * Get a definition by its name.
     * First, retrieve definition impl and then get requested definition.
     * Throw appropriate exception if definition or definition impl is not found.
     *
     * @param definitionName Name of requested definition.
     * @param tilesContext   Current Tiles application context.
     * @throws FactoryNotFoundException Can't find definition impl.
     * @throws org.apache.tiles.definition.DefinitionsFactoryException
     *                                  General error in impl while getting definition.
     * @throws org.apache.tiles.definition.NoSuchDefinitionException
     *                                  No definition found for specified name
     */
    public static ComponentDefinition getDefinition(
        String definitionName,
        TilesRequestContext tilesContext)
        throws FactoryNotFoundException, DefinitionsFactoryException {
        return tilesUtilImpl.getDefinition(definitionName, tilesContext);
    }

    /**
     * Reset internal state.
     * This method is used by test suites to reset the class to its original state.
     */
    protected static void testReset() {
        implAlreadySet = false;
        tilesUtilImpl = new TilesUtilImpl(null);
    }

    public static void createPreparerFactory() {
        tilesUtilImpl.createPreparerFactory();

    }
}