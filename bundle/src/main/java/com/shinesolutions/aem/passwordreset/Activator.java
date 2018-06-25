package com.shinesolutions.aem.passwordreset;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Deactivate;

import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.osgi.PropertiesUtil;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;
import java.util.Dictionary;

/**
 * This bundle activator resets the password of the authorizable IDs specified in the corresponding
 * OSGi configuration. If no configuration is specified, it will reset the password of the admin user only.
 */
@Component(immediate = true)

public class Activator implements BundleActivator {


    protected static final String AUTHORIZABLE_IDS="pwdreset.authorizables";
    ResourceResolverFactory resolverFactory;
    ConfigurationAdmin configurationAdmin;

    @Reference
    void setResourceResolverFactory(ResourceResolverFactory resolverFactory) {
        this.resolverFactory = resolverFactory;
    }

    @Reference
    void setConfigurationAdmin(ConfigurationAdmin configurationAdmin) {
        this.configurationAdmin = configurationAdmin;
    }

    private final String SERVICE_PID = getClass().getName();
    private static final String DEFAULT_AUTHORIZABLE = "admin";

    private static final Logger log = LoggerFactory.getLogger(Activator.class);

    @Activate
    public void start(BundleContext bundleContext) throws Exception {
        String[] authorizableIds;

        // attempt to find a matching configuration for this service
        Configuration config = configurationAdmin.getConfiguration(SERVICE_PID);

        Dictionary<String, Object> properties = config.getProperties();


        if(properties != null) {
            authorizableIds = PropertiesUtil.toStringArray(properties.get(AUTHORIZABLE_IDS));
        } else {
            authorizableIds = new String[]{DEFAULT_AUTHORIZABLE};
        }

        Session session = null;
        try {
            ResourceResolver resolver = resolverFactory.getAdministrativeResourceResolver(null);

            UserManager userManager = resolver.adaptTo(UserManager.class);
            session = resolver.adaptTo(Session.class);

            for (String authorizable : authorizableIds) {
                try {
                    Authorizable user = userManager.getAuthorizable(authorizable);
                    if (user != null) {
                        ((User) user).changePassword(authorizable);
                        if (!userManager.isAutoSave()) {
                            session.save();
                        }
                        log.info("Changed the password for {}", authorizable);
                    } else {
                        log.error("Could not find authorizable {}", authorizable);
                    }
                } catch (RepositoryException repEx) {
                    log.error("Could not change password for {}", authorizable, repEx);
                }
            }
        } catch (LoginException loginEx) {
            log.error("Could not login to the repository", loginEx);
        } finally {
            if(session != null) {
                session.logout();
            }
        }
    }

    @Deactivate
    public void stop(BundleContext bundleContext) throws Exception { }
}
