package vn.com.mbbank.adminportal.core.repository;

import vn.com.mbbank.adminportal.core.model.entity.Role;
import vn.com.mbbank.adminportal.core.model.entity.User;

import java.util.Optional;

public interface CustomizedUserRepository {
    Optional<User> updateUser(User user);
}