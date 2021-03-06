/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2011-2012 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2012 The OpenNMS Group, Inc.
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

/*
* OCA CONTRIBUTION ACKNOWLEDGEMENT - NOT PART OF LEGAL BOILERPLATE
* DO NOT DUPLICATE THIS COMMENT BLOCK WHEN CREATING NEW FILES!
*
* This file was contributed to the OpenNMS(R) project under the
* terms of the OpenNMS Contributor Agreement (OCA).  For details on
* the OCA, see http://www.opennms.org/index.php/Contributor_Agreement
*
* Contributed under the terms of the OCA by:
*
* Bobby Krupczak <rdk@krupczak.org>
* THE KRUPCZAK ORGANIZATION, LLC
* http://www.krupczak.org/
*/

/** XmpCollectionResource contains a set of AttributeGroups which
    in turn contain actual attributes or XmpVars.  Attribute groups
    closely mirror the data collection config file.
    @author <a href="mailto:rdk@krupczak.org">Bobby Krupczak</a>
    @version $Id: XmpCollectionResource.java 38 2008-07-24 13:39:32Z rdk $
 **/

package org.opennms.netmgt.protocols.xmp.collector;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.opennms.netmgt.collectd.AbstractCollectionResource;
import org.opennms.netmgt.collectd.CollectionAgent;
import org.opennms.netmgt.config.collector.AttributeGroup;
import org.opennms.netmgt.config.collector.CollectionSetVisitor;
import org.opennms.netmgt.model.RrdRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class XmpCollectionResource extends AbstractCollectionResource 
{
    /* class variables and methods *********************** */
	private static final Logger LOG = LoggerFactory.getLogger(XmpCollectionResource.class);


    /* instance variables ******************************** */
    private final String m_nodeTypeName;
    private final String m_instance;
    private final String m_resourceType;
    private final int m_nodeType;
    private final Set<AttributeGroup> m_listOfGroups;
    private final CollectionAgent m_agent;

    /* constructors  ************************************* */
    public XmpCollectionResource(CollectionAgent agent, String resourceType, String nodeTypeName, String instance) 
    {
        super(agent);

        // node type can be "node" for scalars or
        // "if" for network interface resources and
        // "*" for all other resource types
        // or anything but 'node' (e.g. table name)

        // resourceType tells us if we are writing under a separate RRD
        // subdir

        this.m_agent = agent;
        this.m_nodeTypeName = nodeTypeName;
        if ((resourceType == null) || (resourceType.length() == 0)) {
            this.m_resourceType = null;
        } else {
            this.m_resourceType = resourceType;
        }
        m_nodeType = -1;

        // filter the instance so it does not have slashes (/) nor colons 
        // in it as they can munge our rrd file layout

        // filter so there are not spaces either just so that
        // it makes directory structures less annoying to deal with
        // rdk - 9/11/2009

        if (instance != null) {
            String instanceValue = instance.replace('/','_');
            instanceValue = instanceValue.replace('\\','_');
            instanceValue = instanceValue.replace(':','_');
            instanceValue = instanceValue.replace(' ','_');
            this.m_instance = instanceValue;
        }
        else {
            this.m_instance = null;
        }

        m_listOfGroups = new HashSet<AttributeGroup>();
    }

    /* private methods *********************************** */
    

    /* public methods ************************************ */

    // get the location where we are supposed to write our data to
    /** {@inheritDoc} */
    @Override
    public File getResourceDir(RrdRepository repository)
    {

        // if we are a collection resource for scalars,
        // return what our super class would return

        if (m_nodeTypeName.equalsIgnoreCase("node")) {
            return new File(repository.getRrdBaseDir(), getParent());
        }

        // we are a collection resource for tabular data
        // return essentially share/rrd/snmp/NodeId/resourceType/instance
        // for now; the problem with using key/instance is that
        // it can change for some tables (e.g. proc table)
        // whoever instantiates this object is responsible for
        // passing in an instance that will be unique;
        // if we want a specific instance of a table, we will use
        // the instance/key that was used for the query; if not,
        // we will use the key returned per table row

        File instDir, rtDir;

        File rrdBaseDir = repository.getRrdBaseDir();
        File nodeDir = new File(rrdBaseDir, getParent());

        // if we have a resourceType, put instances under it
        if (m_resourceType != null) {
            rtDir = new File(nodeDir,m_resourceType);
            instDir = new File(rtDir,m_instance);
        }
        else {
            instDir = new File(nodeDir,m_instance);
        }

        return instDir;
    }

    /**
     * <p>addAttributeGroup</p>
     *
     * @param aGroup a {@link org.opennms.netmgt.config.collector.AttributeGroup} object.
     */
    public void addAttributeGroup(AttributeGroup aGroup)  
    {  
        m_listOfGroups.add(aGroup);
    }

    /**
     * <p>Getter for the field <code>instance</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String getInstance()
    {
        // for node level resources, no instance
        return m_instance;
    }

    /**
     * <p>getResourceTypeName</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String getResourceTypeName() { return m_nodeTypeName; };


    /**
     * <p>getType</p>
     * return -1 for non-tabular; what do we return for 
     * for interface or tabular data?
     *
     * @return a int.
     */
    @Override
    public int getType() { return m_nodeType; }

    /**
     * <p>getGroups</p>
     *
     * @return a {@link java.util.Collection} object.
     */
    public Collection<AttributeGroup>getGroups() { return m_listOfGroups; }

    /**
     * <p>toString</p>
     *
     * @return a {@link java.lang.String} object.
     */
    @Override
    public String toString() { return "XmpCollectionResource for "+m_agent+" resType="+m_resourceType+" instance="+m_instance+" nodeType="+m_nodeTypeName+" nodeType="+m_nodeType; }

    /**
     * @deprecated This class should be changed to store its {@link AttributeGroup}
     * collection in {@link #m_attributeGroups} like all of the other implementations do. 
     */
    @Override
    public void visit(CollectionSetVisitor visitor) 
    { 
        LOG.debug("XmpCollectionResource: visit starting with {} attribute groups", getGroups().size());

        visitor.visitResource(this);

        // visit the attribute groups one at a time
        for (AttributeGroup ag: getGroups()) {
            ag.visit(visitor);
        }

        visitor.completeResource(this);

        LOG.debug("XmpCollectionResource: visit finished for {}", m_agent);

    } /* visit */

    @Override
    public String getParent() {
        return m_agent.getStorageDir().toString();
    }

} /* class XmpCollectionResource */
