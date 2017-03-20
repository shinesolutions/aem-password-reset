package com.shinesolutions.aem.passwordreset;


import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Reference;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import javax.jcr.Session;


@Component(immediate = true)
public class Activator implements BundleActivator {

    @Reference
    protected ResourceResolverFactory resolverFactory;

    private static final String NEW_PASSWORD    = "admin";
    private static final String AUTHORIZABLE_ID = "admin";

    private static final Logger log = LoggerFactory.getLogger(Activator.class);

    @Activate
    public void start(BundleContext bundleContext) throws Exception {
        Session session = null;
        try {
            /*
             * We should be using a system user for this but currently getting a
             * `org.apache.jackrabbit.oak.api.CommitFailedException: OakAccess0000: Access denied`
             * when attempting to change the admin password using a system user with ALL permissions granted.
             *
             * The system user "shinesolutions-password-service" is included as part of this source code and should be
             * used like so once the permission issue is resolved:
             * <code>
             *     ResourceResolver resolver = resolverFactory.getServiceResourceResolver(new HashMap<String, Object>() {{
             *       put(ResourceResolverFactory.SUBSERVICE, "password-service");
             *     }});
             * </code>
             */
            ResourceResolver resolver = resolverFactory.getAdministrativeResourceResolver(null);

            UserManager userManager = resolver.adaptTo(UserManager.class);
            session = resolver.adaptTo(Session.class);

            Authorizable user = userManager.getAuthorizable(AUTHORIZABLE_ID);
            if (user != null) {
                ((User) user).changePassword(NEW_PASSWORD);
                log.info("Changed the admin password");

                if (!userManager.isAutoSave()) {
                    session.save();
                }
            }
        } catch (LoginException loginEx) {
            log.error("Could not login to the repository", loginEx);
        } catch (RepositoryException repRex) {
            log.error("Could not change 'admin' password", repRex);
        } finally {
            if(session != null) {
                session.logout();
            }
        }
    }

    @Deactivate
    public void stop(BundleContext bundleContext) throws Exception { }
}
