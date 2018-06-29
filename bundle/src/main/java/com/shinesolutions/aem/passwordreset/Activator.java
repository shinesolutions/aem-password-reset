package com.shinesolutions.aem.passwordreset;

import org.osgi.service.component.annotations.*;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.metatype.annotations.Designate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * This activator resets the password of the authorizable IDs specified in the corresponding
 * OSGi configuration. If no configuration is specified, it will reset the password of the admin user only.
 */
@Component(immediate = true)
@Designate(ocd = ActivatorConfiguration.class)
public class Activator {

    protected ResourceResolverFactory resolverFactory;

    private static final Logger log = LoggerFactory.getLogger(Activator.class);

    @Reference
    protected void setResourceResolverFactory(ResourceResolverFactory resolverFactory) {
        this.resolverFactory = resolverFactory;
    }

    @Activate
    public void start(ActivatorConfiguration config) {
        String[] authorizableIds = config.pwdreset_authorizables();

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
}
