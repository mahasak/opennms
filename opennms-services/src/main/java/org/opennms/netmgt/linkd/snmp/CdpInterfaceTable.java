/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2012 The OpenNMS Group, Inc.
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

package org.opennms.netmgt.linkd.snmp;

import java.net.InetAddress;

import org.opennms.netmgt.snmp.SnmpInstId;
import org.opennms.netmgt.snmp.SnmpObjId;

/**
 * <P>
 * CdpCacheTable uses a SnmpSession to collect the CdpCache table
 * entries. It implements the SnmpHandler to receive notifications when a reply
 * is received/error occurs in the SnmpSession used to send requests/receive
 * replies.
 * </P>
 *
 * @author <A HREF="mailto:rssntn67@yahoo.it">Antonio Russo </A>
 * @see <A HREF="http://www.ietf.org/rfc/rfc1213.txt">RFC1213 </A>
 * @version $Id: $
 */
public class CdpInterfaceTable extends SnmpTable<CdpInterfaceTableEntry> {

    /**
     * <P>
     * Constructs an CdpInterfaceTable object that is used to collect the Cisco Discovery Protocol
     * elements from the remote agent. Once all the elements are collected, or
     * there is an error in the collection the signaler object is <EM>notified
     * </EM> to inform other threads.
     * </P>
     *
     * @see CdpInterfaceTableEntry
     * @param address a {@link java.net.InetAddress} object.
     */
    public CdpInterfaceTable(InetAddress address)
	{
        super(address, "cdpInterfaceTable", CdpInterfaceTableEntry.cdpInterface_elemList);
	}
    /** {@inheritDoc} */
    @Override
    protected CdpInterfaceTableEntry createTableEntry(SnmpObjId base, SnmpInstId inst, Object val) {
        return new CdpInterfaceTableEntry();
    }

}
