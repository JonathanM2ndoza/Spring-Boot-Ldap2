package com.jmendoza.springboot.ldap2.repository;

import com.jmendoza.springboot.ldap2.model.Person;
import com.jmendoza.springboot.ldap2.model.PersonContextMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.BaseLdapNameAware;
import org.springframework.ldap.query.LdapQuery;
import org.springframework.stereotype.Repository;

import javax.naming.ldap.LdapName;
import java.util.List;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

@Repository
public class PersonRepository implements BaseLdapNameAware {

    private final String ATTRIBUTE_UID = "uid";
    private final String ORGANIZATIONAL_UNIT = "ou";

    @Autowired
    private LdapTemplate ldapTemplate;
    private LdapName baseLdapPath;

    @Override
    public void setBaseLdapPath(LdapName ldapName) {
        this.baseLdapPath = ldapName;
    }

    public void authenticate(String user, String password) {
        LdapQuery query = query().where(ATTRIBUTE_UID)
                .is(user);
        ldapTemplate.authenticate(query, password);
    }

    public List<Person> searchPersonByUid(String user) {
        LdapQuery query = query().where(ATTRIBUTE_UID)
                .is(user);
        return ldapTemplate.search(query, new PersonContextMapper());
    }

    public void updatePerson(String user, Person person) {

        DirContextOperations context = ldapTemplate.lookupContext(query().where(ATTRIBUTE_UID)
                .is(user).base());

        //context.setAttributeValues("objectclass", new String[]{"top", "person", "organizationalPerson", "inetOrgPerson"});
        context.setAttributeValue("cn", person.getFullName());
        context.setAttributeValue("sn", person.getLastName());
        context.setAttributeValue("mail", person.getEmail());
        //I have a error
        ldapTemplate.modifyAttributes(context);

    }
}
