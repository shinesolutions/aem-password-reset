package com.shinesolutions.aem.passwordreset;

import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ActivatorTest {

    @Mock
    private ResourceResolverFactory mockResolverFactory;
    @Mock
    private ResourceResolver mockResolver;
    @Mock
    private UserManager mockUserManager;
    @Mock
    private Session mockSession;
    @Mock
    private User mockAuthorizable;
    @Mock
    private ActivatorConfiguration mockConfiguration;


    @Before
    public void before() throws Exception {
        MockitoAnnotations.initMocks(this);

        when(mockConfiguration.pwdreset_authorizables()).thenReturn(new String[]{"admin"});
        when(mockResolverFactory.getAdministrativeResourceResolver(null)).thenReturn(mockResolver);
        when(mockResolver.adaptTo(UserManager.class)).thenReturn(mockUserManager);
        when(mockResolver.adaptTo(Session.class)).thenReturn(mockSession);
    }

    @Test
    public void testPasswordChangeNoAutoSave() throws Exception {
        when(mockUserManager.getAuthorizable(anyString())).thenReturn(mockAuthorizable);
        when(mockUserManager.isAutoSave()).thenReturn(false);

        Activator activator = spy(createActivator());
        activator.start(mockConfiguration);

        verify(mockAuthorizable, times(1)).changePassword(anyString());
        verify(mockSession, times(1)).save();
        verify(mockSession, times(1)).logout();
    }

    @Test
    public void testPasswordChangeAutoSave() throws Exception {
        when(mockUserManager.getAuthorizable(anyString())).thenReturn(mockAuthorizable);
        when(mockUserManager.isAutoSave()).thenReturn(true);

        Activator activator = spy(createActivator());
        activator.start(mockConfiguration);

        verify(mockAuthorizable, times(1)).changePassword(anyString());
        verify(mockSession, times(0)).save();
        verify(mockSession, times(1)).logout();
    }

    @Test
    public void testAdminNotFound() throws Exception {
        when(mockUserManager.getAuthorizable(eq("admin"))).thenReturn(null);
        when(mockUserManager.getAuthorizable(not(eq("admin")))).thenReturn(mockAuthorizable);

        Activator activator = spy(createActivator());
        activator.start(mockConfiguration);

        verify(mockAuthorizable, times(0)).changePassword("admin");
        verify(mockSession, times(0)).save();
        verify(mockSession, times(1)).logout();
    }

    @Test
    public void testAdminLoginError() throws Exception {
        when(mockResolverFactory.getAdministrativeResourceResolver(null)).thenThrow(new LoginException());

        Activator activator = spy(createActivator());
        activator.start(mockConfiguration);

        verify(mockAuthorizable, times(0)).changePassword(anyString());
        verify(mockSession, times(0)).save();
        verify(mockSession, times(0)).logout();
        verifyZeroInteractions(mockResolver);
        verifyZeroInteractions(mockAuthorizable);
        verifyZeroInteractions(mockUserManager);
        verifyZeroInteractions(mockSession);
    }

    @Test
    public void testSessionSaveError() throws Exception {
        when(mockUserManager.getAuthorizable(anyString())).thenReturn(mockAuthorizable);
        when(mockUserManager.isAutoSave()).thenReturn(true);
        doThrow(new RepositoryException()).when(mockSession).save();

        Activator activator = spy(createActivator());
        activator.start(mockConfiguration);

        verify(mockAuthorizable, times(1)).changePassword(anyString());
        verify(mockSession, times(0)).save();
        verify(mockSession, times(1)).logout();
    }

    @Test
    public void testPasswordChangeMultipleIDs() throws Exception {
        when(mockConfiguration.pwdreset_authorizables()).thenReturn(new String[]{"deployer", "importer"});
        when(mockUserManager.getAuthorizable(anyString())).thenReturn(mockAuthorizable);
        when(mockUserManager.isAutoSave()).thenReturn(false);

        Activator activator = spy(createActivator());
        activator.start(mockConfiguration);

        verify(mockAuthorizable, times(2)).changePassword(anyString());
        verify(mockSession, times(2)).save();
        verify(mockSession, times(1)).logout();
    }

    private Activator createActivator() {
        Activator activator = new Activator();
        activator.resolverFactory = mockResolverFactory;
        return activator;
    }
}
