/*
 * $Id$
 *
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
 */

package org.apache.tiles.definition.dao;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.tiles.Definition;
import org.apache.tiles.definition.DefinitionsFactoryException;
import org.apache.tiles.util.LocaleUtil;

/**
 * A definitions DAO (loading URLs and using Locale as a customization key) that
 * caches definitions that have been loaded in a raw way (i.e. with inheritance
 * that is not resolved).
 *
 * @version $Rev$ $Date$
 * @since 2.1.0
 */
public class CachingLocaleUrlDefinitionDAO extends BaseLocaleUrlDefinitionDAO {

    /**
     * The locale-specific set of definitions objects.
     */
    private Map<Locale, Map<String, Definition>> locale2definitionMap;

    /**
     * Constructor.
     *
     * @since 2.1.0
     */
    public CachingLocaleUrlDefinitionDAO() {
        locale2definitionMap = new HashMap<Locale, Map<String, Definition>>();
    }

    /** {@inheritDoc} */
    public Definition getDefinition(String name, Locale customizationKey) {
        Definition retValue = null;
        Map<String, Definition> definitions = getDefinitions(customizationKey);
        if (definitions != null) {
            retValue = definitions.get(name);
        }

        return retValue;
    }

    /** {@inheritDoc} */
    public Map<String, Definition> getDefinitions(Locale customizationKey) {
        if (customizationKey == null) {
            customizationKey = LocaleUtil.NULL_LOCALE;
        }
        Map<String, Definition> retValue = locale2definitionMap
                .get(customizationKey);
        if (retValue == null || refreshRequired()) {
            retValue = checkAndloadDefinitions(customizationKey);
        }
        return retValue;
    }

    /**
     * Checks if URLs have changed. If yes, it clears the cache. Then continues
     * loading definitions.
     *
     * @param customizationKey The locale to use when loading URLs.
     * @return The loaded definitions.
     * @since 2.1.0
     */
    protected synchronized Map<String, Definition> checkAndloadDefinitions(
            Locale customizationKey) {
        if (refreshRequired()) {
            locale2definitionMap.clear();
        }
        return loadDefinitions(customizationKey);
    }

    /**
     * Loads definitions from the URLs..
     *
     * @param customizationKey The locale to use when loading URLs.
     * @return The loaded definitions.
     * @since 2.1.0
     */
    protected Map<String, Definition> loadDefinitions(Locale customizationKey) {
        Map<String, Definition> localeDefsMap = locale2definitionMap
                .get(customizationKey);
        if (localeDefsMap != null) {
            return localeDefsMap;
        }

        String postfix = LocaleUtil.calculatePostfix(customizationKey);
        Locale parentLocale = LocaleUtil.getParentLocale(customizationKey);
        localeDefsMap = new HashMap<String, Definition>();
        if (parentLocale != null) {
            Map<String, Definition> parentDefs = loadDefinitions(parentLocale);
            if (parentDefs != null) {
                localeDefsMap.putAll(parentDefs);
            }
        }
        // For each source, the URL must be loaded.
        for (URL url : sourceURLs) {
            String path = url.toExternalForm();

            String newPath = LocaleUtil.concatPostfix(path, postfix);
            try {
                URL newUrl = new URL(newPath);
                Map<String, Definition> defsMap = loadDefinitionsFromURL(newUrl);
                if (defsMap != null) {
                    localeDefsMap.putAll(defsMap);
                }
            } catch (MalformedURLException e) {
                throw new DefinitionsFactoryException("Error parsing URL "
                        + newPath, e);
            }
        }
        locale2definitionMap.put(customizationKey, localeDefsMap);
        return localeDefsMap;
    }
}
