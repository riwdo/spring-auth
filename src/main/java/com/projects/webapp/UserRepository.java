package com.projects.webapp;

import com.projects.webapp.models.UserEntity;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserEntity, Integer> {
  UserEntity getByName(String name);
}
