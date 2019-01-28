package com.jmendoza.springboot.ldap2.controller;


import com.jmendoza.springboot.ldap2.model.Person;
import com.jmendoza.springboot.ldap2.service.LdapService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/ldap")
public class LdapController {

    @Autowired
    LdapService ldapService;

    @PostMapping("users/auth")
    public void authenticate(@Valid @RequestBody String requestJson) {
        JSONObject json = new JSONObject(requestJson);
        ldapService.authenticate(json.getString("user"), json.getString("password"));
    }

    @GetMapping("users/{uid}")
    public List<Person> getPersonByUid(@PathVariable(value = "uid") String user) {
        return ldapService.searchPersonByUid(user);
    }

    @PutMapping("users/{uid}")
    public void updatePerson(@PathVariable(value = "uid") String user, @Valid @RequestBody Person person) {
        ldapService.updatePerson(user, person);
    }

}
