package oal.oracle.apps.scm.drm.orbit;

import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import oracle.security.jps.JpsException;
import oracle.security.jps.service.JpsServiceLocator;
import oracle.security.jps.service.ServiceLocator;
import oracle.security.jps.service.credstore.CredentialStore;
import oracle.security.jps.service.credstore.PasswordCredential;


public class KeyStoreAccessor {
    private KeyStoreAccessor() {
        super();
    }
    private static KeyStoreAccessor keyStoreAccessor;
    private String userName;
    private String password;
    
    public static KeyStoreAccessor getInstance() {
        if(keyStoreAccessor==null)
            keyStoreAccessor=new KeyStoreAccessor();
        return keyStoreAccessor;
    }
    
    //Method to fetch credentials for generic user from CDP keystore
    public void getCredentials(String map, String key) {
                PasswordCredential credentials = null;
                PrivilegedExceptionAction<PasswordCredential> action =
                    new PrivilegedExceptionAction<PasswordCredential>() {
                        public PasswordCredential run() throws JpsException {
                            ServiceLocator locator = JpsServiceLocator.getServiceLocator();
                            CredentialStore store = locator.lookup(CredentialStore.class);
                            return (PasswordCredential) store.getCredential(map, key);
                        }
                    };

                try {
                    credentials = AccessController.doPrivileged(action);
                    userName = credentials.getName();
                    password = new String(credentials.getPassword());
                } catch (PrivilegedActionException e) {
                }
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }
}