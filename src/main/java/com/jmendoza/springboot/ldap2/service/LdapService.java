package com.jmendoza.springboot.ldap2.service;

import com.jmendoza.springboot.ldap2.model.Person;

import java.util.List;

public interface LdapService {
    void authenticate(String user, String password);

    List<Person> searchPersonByUid(String user);

    void updatePerson(String user, Person person);
}
