package com.vranic.zavrsnirad.repository;

import com.vranic.zavrsnirad.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUserName(String userName);

    @Query(value = "SELECT user FROM User user ORDER BY user.userName ASC")
    List<User> getAllUsers();

    @Query(value = "SELECT user FROM User user WHERE user.userName = :username ORDER BY user.userName DESC")
    List<User> getUsersByUsername(@Param("username") String username);

    @Query(value = "SELECT user FROM User user WHERE user.active = true ORDER BY user.userName DESC")
    List<User> getActiveUsers();

    @Query(value = "SELECT user FROM User user WHERE user.active = false ORDER BY user.userName DESC")
    List<User> getInactiveUsers();

    @Modifying
    @Query(value = "UPDATE user u SET u.password = :newPassword WHERE u.id = :userId", nativeQuery = true)
    void updatePassword(@Param("userId") Long userId, @Param("newPassword") String newPassword);

    @Modifying
    @Query(value = "UPDATE user u SET u.active = :active, u.user_name = :username, u.roles = :roles, u.first_name = :firstName, u.last_name = :lastName WHERE u.id = :userId", nativeQuery = true)
    void updateUser(@Param("userId") Long userId, @Param("username") String username, @Param("active") Boolean active, @Param("roles") String roles, @Param("firstName") String firstName, @Param("lastName") String lastName);
}
