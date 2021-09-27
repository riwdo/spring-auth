package com.projects.webapp;

import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
  User getByName(String name);
}
