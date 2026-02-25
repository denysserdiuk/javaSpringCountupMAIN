package denysserdiuk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import denysserdiuk.model.Users;


public interface UserRepository extends JpaRepository<Users, Long> {
    Users findByUsername(String username);
    Users findByEmail(String email);
    @Query("SELECT u FROM Users u WHERE u.username = :username")
    Users findByUsernameCaseSensitive(@Param("username") String username);
}