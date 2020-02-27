package com.jmendoza.springboot.ldap2.model;

import org.pac4j.ldap.profile.LdapProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.stereotype.Component;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

@Component
public class UserAttributesMapper implements AttributesMapper<LdapProfile> {

    public static final String LDAP_ATTRIBUTE_USER_NAME = "ldap.attribute.userName";
    public static final String LDAP_ATTRIBUTE_2_FA = "ldap.attribute.2fa";

    @Autowired
    private Environment env;

    @Override
    public LdapProfile mapFromAttributes(Attributes attributes) throws NamingException {

        LdapProfile ldapProfile;
        if (attributes == null) {
            return null;
        }
        ldapProfile = new LdapProfile();
        ldapProfile.addAttribute(env.getRequiredProperty("ldap.cn"), attributes.get(env.getRequiredProperty("ldap.cn")).get().toString());

        if (attributes.get(env.getRequiredProperty(LDAP_ATTRIBUTE_USER_NAME)) != null) {
            ldapProfile.addAttribute(env.getRequiredProperty(LDAP_ATTRIBUTE_USER_NAME), attributes.get(env.getRequiredProperty(LDAP_ATTRIBUTE_USER_NAME)).get().toString());
        }
        if (attributes.get(env.getRequiredProperty(LDAP_ATTRIBUTE_2_FA)) != null) {
            ldapProfile.addAttribute(env.getRequiredProperty(LDAP_ATTRIBUTE_2_FA), attributes.get(env.getRequiredProperty(LDAP_ATTRIBUTE_2_FA)).get().toString());
        }
        return ldapProfile;
    }
}
