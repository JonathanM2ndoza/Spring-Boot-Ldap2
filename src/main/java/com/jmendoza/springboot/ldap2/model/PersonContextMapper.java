package com.jmendoza.springboot.ldap2.model;

import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.support.AbstractContextMapper;

public class PersonContextMapper extends AbstractContextMapper<Person> {
    @Override
    protected Person doMapFromContext(DirContextOperations dirContextOperations) {
        Person person = new Person();
        person.setFullName(dirContextOperations.getStringAttribute("cn"));
        person.setLastName(dirContextOperations.getStringAttribute("sn"));
        person.setUid(dirContextOperations.getStringAttribute("uid"));
        person.setEmail(dirContextOperations.getStringAttribute("mail"));
        return person;
    }
}
