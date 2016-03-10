// AbstractActionModel.java
package apps;

import apps.startup.StartupActionModelUtil;
import jmri.InstanceManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provide services for invoking actions during configuration and startup.
 * <P>
 * The action classes and corresponding human-readable names are kept in the
 * apps.ActionListBundle properties file (which can be translated). They are
 * displayed in lexical order by human-readable name.
 * <P>
 * @author	Bob Jacobsen Copyright 2003, 2007, 2014
 * @version $Revision$
 * @see apps.startup.AbstractActionModelFactory
 */
public abstract class AbstractActionModel implements StartupModel {

    public AbstractActionModel() {
        className = "";
    }
    //TODO At some point this class might need to consider which system connection memo is being against certain system specific items
    String className;

    public String getClassName() {
        return className;
    }

    @Override
    public String getName() {
        return InstanceManager.getDefault(StartupActionModelUtil.class).getActionName(className);
    }

    @Override
    public void setName(String n) {
        this.className = InstanceManager.getDefault(StartupActionModelUtil.class).getClassName(n);
    }

    public void setClassName(String n) {
        className = n;
    }

    // initialize logging
    static Logger log = LoggerFactory.getLogger(AbstractActionModel.class.getName());

}
