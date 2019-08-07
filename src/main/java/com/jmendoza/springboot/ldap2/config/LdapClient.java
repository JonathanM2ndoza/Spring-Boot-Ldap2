package com.smartmatic.rcl.security.auth.service;

import com.cgts.core.prop.PropertiesLoader;
import com.smartmatic.rcl.security.auth.domain.AuthenticateException;
import com.smartmatic.rcl.security.auth.domain.UserAttributesMapper;
import org.pac4j.ldap.profile.LdapProfile;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.ldap.filter.AndFilter;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.support.LdapNameBuilder;

import javax.naming.Name;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

/**
 * Basic LDAP client.
 *
 * @author Jerome Leleu
 * @author Jonathan Mendoza
 */
public class LdapClient {

    public static final String LDAP_BASE_DN = "ldap.base.dn";
    private final LdapContextSource contextSource;
    private final LdapTemplate ldapTemplate;
    private static LdapClient singleInstance = null;

    public static LdapClient getInstance() {
        if (singleInstance == null)
            singleInstance = new LdapClient();

        return singleInstance;
    }

    public LdapClient() {
        System.setProperty("javax.net.debug", "ssl,handshake");
        System.setProperty("com.sun.jndi.ldap.object.disableEndpointIdentification", "true");
        System.setProperty("javax.net.ssl.keyStore", PropertiesLoader.getProperty("ldap.keyStore"));
        System.setProperty("javax.net.ssl.keyStorePassword", PropertiesLoader.getProperty("ldap.keyStorePassword"));
        System.setProperty("javax.net.ssl.keyStoreType", PropertiesLoader.getProperty("ldap.keyStoreType"));
        System.setProperty("javax.net.ssl.trustStore", PropertiesLoader.getProperty("ldap.keyStore"));
        System.setProperty("javax.net.ssl.trustStorePassword", PropertiesLoader.getProperty("ldap.keyStorePassword"));

        contextSource = new LdapContextSource();
        contextSource.setUrl(PropertiesLoader.getProperty("ldap.url"));
        contextSource.setUserDn(PropertiesLoader.getProperty("ldap.admin.user"));
        contextSource.setPassword(PropertiesLoader.getProperty("ldap.admin.password"));
        contextSource.afterPropertiesSet();
        ldapTemplate = new LdapTemplate(contextSource);
    }

    public void updateAttribute(String username, String attibute, String attributeValue) throws AuthenticateException {

        try {
            LdapProfile ldapProfile = getUserDetails(username);
            Name dn = LdapNameBuilder
                    .newInstance(PropertiesLoader.getProperty(LDAP_BASE_DN))
                    .add(PropertiesLoader.getProperty("ldap.cn"), ldapProfile.getAttribute(PropertiesLoader.getProperty("ldap.cn")).toString())
                    .build();
            DirContextOperations context = ldapTemplate.lookupContext(dn);
            context.setAttributeValues("objectclass", new String[]{"homeInfo", "Top", "ndsLoginProperties", "Person", "organizationalPerson", "inetOrgPerson"});
            context.setAttributeValue(attibute, attributeValue);
            ldapTemplate.modifyAttributes(context);

        } catch (Exception e) {
            throw new AuthenticateException(e);
        }
    }

    public boolean authenticate(String username, String password) throws AuthenticateException {

        try {
            AndFilter filter = new AndFilter();
            filter.and(new EqualsFilter(PropertiesLoader.getProperty("ldap.attribute.dni"), username));
            return ldapTemplate.authenticate(PropertiesLoader.getProperty(LDAP_BASE_DN), filter.encode(), password);
        } catch (Exception e) {
            throw new AuthenticateException(e);
        }
    }

    public LdapProfile getUserDetails(String username) {
        List<LdapProfile> list = ldapTemplate.search(query().base(PropertiesLoader.getProperty(LDAP_BASE_DN)).where(PropertiesLoader.getProperty("ldap.attribute.dni")).is(username), new UserAttributesMapper());
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
}