package utb.fai.RESTAPIServer;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<MyUser, Long> {
    List<MyUser> findUserByName(String name);
    List<MyUser> findUserByEmail(String email);
    List<MyUser> findUserByPhoneNumber(String phoneNumber);
}
