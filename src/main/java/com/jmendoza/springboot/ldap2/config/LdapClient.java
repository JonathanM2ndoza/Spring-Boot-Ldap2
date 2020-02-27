package com.jmendoza.springboot.ldap2.config;

import com.jmendoza.springboot.ldap2.model.UserAttributesMapper;
import org.pac4j.ldap.profile.LdapProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
 * Another version of LdapConfig with keyStore for ldaps
 *
 * @author Jerome Leleu
 * @author Jonathan Mendoza
 */
@Configuration
public class LdapClient {

    public static final String LDAP_BASE_DN = "ldap.base.dn";
    private final LdapContextSource contextSource;
    private final LdapTemplate ldapTemplate;
    private static LdapClient singleInstance = null;

    @Autowired
    private Environment env;

    public static LdapClient getInstance() {
        if (singleInstance == null)
            singleInstance = new LdapClient();

        return singleInstance;
    }

    public LdapClient() {
        System.setProperty("javax.net.debug", "ssl,handshake");
        System.setProperty("com.sun.jndi.ldap.object.disableEndpointIdentification", "true");
        System.setProperty("javax.net.ssl.keyStore", env.getRequiredProperty("ldap.keyStore"));
        System.setProperty("javax.net.ssl.keyStorePassword", env.getRequiredProperty("ldap.keyStorePassword"));
        System.setProperty("javax.net.ssl.keyStoreType", env.getRequiredProperty("ldap.keyStoreType"));
        System.setProperty("javax.net.ssl.trustStore", env.getRequiredProperty("ldap.keyStore"));
        System.setProperty("javax.net.ssl.trustStorePassword", env.getRequiredProperty("ldap.keyStorePassword"));

        contextSource = new LdapContextSource();
        contextSource.setUrl(env.getRequiredProperty("ldap.url"));
        contextSource.setUserDn(env.getRequiredProperty("ldap.admin.user"));
        contextSource.setPassword(env.getRequiredProperty("ldap.admin.password"));
        contextSource.afterPropertiesSet();
        ldapTemplate = new LdapTemplate(contextSource);
    }

    public void updateAttribute(String username, String attibute, String attributeValue) throws Throwable {

        try {
            LdapProfile ldapProfile = getUserDetails(username);
            Name dn = LdapNameBuilder
                    .newInstance(env.getRequiredProperty(LDAP_BASE_DN))
                    .add(env.getRequiredProperty("ldap.cn"), ldapProfile.getAttribute(env.getRequiredProperty("ldap.cn")).toString())
                    .build();
            DirContextOperations context = ldapTemplate.lookupContext(dn);
            context.setAttributeValues("objectclass", new String[]{"homeInfo", "Top", "ndsLoginProperties", "Person", "organizationalPerson", "inetOrgPerson"});
            context.setAttributeValue(attibute, attributeValue);
            ldapTemplate.modifyAttributes(context);

        } catch (Exception e) {
            throw new Throwable(e);
        }
    }

    public boolean authenticate(String username, String password) throws Throwable {

        try {
            AndFilter filter = new AndFilter();
            filter.and(new EqualsFilter(env.getRequiredProperty("ldap.attribute.dni"), username));
            return ldapTemplate.authenticate(env.getRequiredProperty(LDAP_BASE_DN), filter.encode(), password);
        } catch (Exception e) {
            throw new Throwable(e);
        }
    }

    public LdapProfile getUserDetails(String username) {
        List<LdapProfile> list = ldapTemplate.search(query().base(env.getRequiredProperty(LDAP_BASE_DN)).where(env.getRequiredProperty("ldap.attribute.dni")).is(username), new UserAttributesMapper());
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
}