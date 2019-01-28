package com.jmendoza.springboot.ldap2.service.impl;

import com.jmendoza.springboot.ldap2.model.Person;
import com.jmendoza.springboot.ldap2.repository.PersonRepository;
import com.jmendoza.springboot.ldap2.service.LdapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service(value = "ldapService")
public class LdapServiceImpl implements LdapService {

    @Autowired
    PersonRepository personRepository;

    @Override
    public void authenticate(String user, String password) {
        personRepository.authenticate(user, password);
    }

    @Override
    public List<Person> searchPersonByUid(String user) {
        return personRepository.searchPersonByUid(user);
    }

    @Override
    public void updatePerson(String user, Person person) {
        personRepository.updatePerson(user, person);
    }
}
