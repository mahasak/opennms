/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2013 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2013 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.features.vaadin.nodemaps.internal;

import java.util.ArrayList;
import java.util.List;

import org.opennms.features.geocoder.GeocoderService;
import org.opennms.netmgt.dao.AlarmDao;
import org.opennms.netmgt.dao.AssetRecordDao;
import org.opennms.netmgt.dao.NodeDao;
import org.ops4j.pax.vaadin.AbstractApplicationFactory;
import org.ops4j.pax.vaadin.ScriptTag;
import org.springframework.transaction.support.TransactionOperations;

import com.vaadin.ui.UI;

/**
 * A factory for creating NodeMapsApplication objects.
 * 
 * @author <a href="mailto:agalue@opennms.org">Alejandro Galue</a>
 */
public class NodeMapsApplicationFactory extends AbstractApplicationFactory {
    private NodeDao m_nodeDao;

    private AssetRecordDao m_assetDao;

    private AlarmDao m_alarmDao;

    private GeocoderService m_geocoder;

    private TransactionOperations m_transaction;

    @Override
    public Class<? extends UI> getUIClass() {
        return NodeMapsApplication.class;
    }

    @Override
    public List<ScriptTag> getAdditionalScripts() {
        final List<ScriptTag> tags = new ArrayList<ScriptTag>();
        tags.add(new ScriptTag("http://maps.google.com/maps/api/js?sensor=false", "text/javascript", null));
        tags.add(new ScriptTag("openlayers/OpenLayers.js", "text/javascript", null));
        tags.add(new ScriptTag("Google.js", "text/javascript", null));
        tags.add(new ScriptTag("markercluster/leaflet.markercluster.js", "text/javascript", null));
        tags.add(new ScriptTag("libs/leaflet/c1d410f2703f0832618c997225e7360f6a292c58/leaflet-src.js", "text/javascript", null));
        return tags;
    }

    /**
     * Sets the OpenNMS Node DAO.
     * 
     * @param m_nodeDao
     *            the new OpenNMS Node DAO
     */
    public void setNodeDao(final NodeDao nodeDao) {
        m_nodeDao = nodeDao;
    }

    public void setAssetDao(final AssetRecordDao assetDao) {
        m_assetDao = assetDao;
    }

    public void setAlarmDao(final AlarmDao alarmDao) {
        m_alarmDao = alarmDao;
    }

    public void setGeocoderService(final GeocoderService geocoderService) {
        m_geocoder = geocoderService;
    }
    
    public void setTransactionOperations(final TransactionOperations tx) {
        m_transaction = tx;
    }

    @Override
    public UI getUI() {
        if (m_nodeDao == null) {
            throw new RuntimeException("m_nodeDao cannot be null.");
        }
        final NodeMapsApplication app = new NodeMapsApplication();
        app.setNodeDao(m_nodeDao);
        app.setAssetRecordDao(m_assetDao);
        app.setAlarmDao(m_alarmDao);
        app.setGeocoderService(m_geocoder);
        app.setTransactionOperations(m_transaction);
        return app;
    }
}
