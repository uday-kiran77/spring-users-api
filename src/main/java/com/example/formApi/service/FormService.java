package com.example.formApi.service;

import com.example.formApi.domain.Users;
import com.example.formApi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormService {
    @Autowired
    private UserRepository repo;
    public List<Users> listAll() {

        return repo.findAll();
    }

    public Users save(Users std) {
        return repo.save(std);
    }

    public Users get(long id) {
        return repo.findById(id).get();
    }

    public void delete(long id) {
        repo.deleteById(id);
    }


}
