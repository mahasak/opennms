package org.opennms.netmgt.config.internal.collection;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.opennms.netmgt.config.api.collection.IColumn;
import org.opennms.netmgt.config.api.collection.IExpression;
import org.opennms.netmgt.config.api.collection.IResourceType;
import org.opennms.netmgt.config.datacollection.ResourceType;

/**
 *  <resourceType name="hrStorageIndex" label="Storage (MIB-2 Host Resources)">
 *	  <resourceName>
 *      <m_template>${hrStorageDescr}</m_template>
 *    </resourceName>
 *    <resourceLabel><m_template>${hrStorageDescr}</m_template></resourceLabel>
 *    <resourceKind><m_template>${hrStorageType}</m_template></resourceKind>
 *    <column oid=".1.3.6.1.2.1.25.2.3.1.2" alias="hrStorageType"  type="string" />
 *    <column oid=".1.3.6.1.2.1.25.2.3.1.3" alias="hrStorageDescr" type="string" />
 *  </resourceType>
 *   
 * @author brozow
 *
 */

@XmlRootElement(name="resourceType")
@XmlAccessorType(XmlAccessType.NONE)
public class ResourceTypeImpl implements IResourceType {

    @XmlAttribute(name="name")
    private String m_name;

    @XmlAttribute(name="label")
    private String m_label;

    @XmlElement(name="resourceName")
    private ExpressionImpl m_resourceNameExpression;

    @XmlElement(name="resourceLabel")
    private ExpressionImpl m_resourceLabelExpression;

    @XmlElement(name="resourceKind")
    private ExpressionImpl m_resourceKindExpression;

    @XmlElement(name="column")
    private ColumnImpl[] m_columns = new ColumnImpl[0];

    public ResourceTypeImpl() {
    }

    public ResourceTypeImpl(final String name, final String label) {
        m_name = name;
        m_label = label;
    }

    public ResourceTypeImpl(final ResourceType oldResourceType) {
        throw new UnsupportedOperationException("Not yet implemented!");
    }

    public String getTypeName() {
        return m_name;
    }

    public void setName(String name) {
        m_name = name;
    }

    public String getLabel() {
        return m_label;
    }

    public void setLabel(String label) {
        m_label = label;
    }

    public IExpression getResourceNameExpression() {
        return m_resourceNameExpression;
    }

    public void setResourceNameExpression(final IExpression expression) {
        m_resourceNameExpression = ExpressionImpl.asExpression(expression);
    }

    public void setResourceNameTemplate(final String template) {
        m_resourceNameExpression = new ExpressionImpl(template);
    }

    public IExpression getResourceLabelExpression() {
        return m_resourceLabelExpression;
    }

    public void setResourceLabelExpression(final IExpression expression) {
        m_resourceLabelExpression = ExpressionImpl.asExpression(expression);
    }

    public void setResourceLabelTemplate(final String template) {
        m_resourceLabelExpression = new ExpressionImpl(template);
    }

    public IExpression getResourceKindExpression() {
        return m_resourceKindExpression;
    }

    public void setResourceKindExpression(final IExpression expression) {
        m_resourceKindExpression = ExpressionImpl.asExpression(expression);
    }

    public void setResourceKindTemplate(final String template) {
        m_resourceKindExpression = new ExpressionImpl(template);
    }

    public IColumn[] getColumns() {
        return m_columns;
    }

    public void setColumns(final IColumn[] columns) {
        m_columns = ColumnImpl.asColumns(columns);
    }

    public void addColumn(final ColumnImpl column) {
        m_columns = ArrayUtils.append(m_columns, column);
    }

    public void addColumn(final String oid, final String alias, final String type) {
        addColumn(new ColumnImpl(oid, alias, type));
    }

    public void addColumn(final String oid, final String alias, final String type, final String displayHint) {
        addColumn(new ColumnImpl(oid, alias, type, displayHint));
    }

    @Override
    public String toString() {
        return "ResourceTypeImpl [name=" + m_name + ", label=" + m_label + ", resourceNameExpression=" + m_resourceNameExpression + ", resourceLabelExpression=" + m_resourceLabelExpression
                + ", resourceKindExpression=" + m_resourceKindExpression + ", columns=" + Arrays.toString(m_columns) + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(m_columns);
        result = prime * result + ((m_label == null) ? 0 : m_label.hashCode());
        result = prime * result + ((m_name == null) ? 0 : m_name.hashCode());
        result = prime * result + ((m_resourceKindExpression == null) ? 0 : m_resourceKindExpression.hashCode());
        result = prime * result + ((m_resourceLabelExpression == null) ? 0 : m_resourceLabelExpression.hashCode());
        result = prime * result + ((m_resourceNameExpression == null) ? 0 : m_resourceNameExpression.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ResourceTypeImpl)) {
            return false;
        }
        final ResourceTypeImpl other = (ResourceTypeImpl) obj;
        if (!Arrays.equals(m_columns, other.m_columns)) {
            return false;
        }
        if (m_label == null) {
            if (other.m_label != null) {
                return false;
            }
        } else if (!m_label.equals(other.m_label)) {
            return false;
        }
        if (m_name == null) {
            if (other.m_name != null) {
                return false;
            }
        } else if (!m_name.equals(other.m_name)) {
            return false;
        }
        if (m_resourceKindExpression == null) {
            if (other.m_resourceKindExpression != null) {
                return false;
            }
        } else if (!m_resourceKindExpression.equals(other.m_resourceKindExpression)) {
            return false;
        }
        if (m_resourceLabelExpression == null) {
            if (other.m_resourceLabelExpression != null) {
                return false;
            }
        } else if (!m_resourceLabelExpression.equals(other.m_resourceLabelExpression)) {
            return false;
        }
        if (m_resourceNameExpression == null) {
            if (other.m_resourceNameExpression != null) {
                return false;
            }
        } else if (!m_resourceNameExpression.equals(other.m_resourceNameExpression)) {
            return false;
        }
        return true;
    }

}
