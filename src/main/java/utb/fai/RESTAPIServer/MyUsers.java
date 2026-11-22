package utb.fai.RESTAPIServer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController 
@RequestMapping("/users")
public class MyUsers {

    @Autowired
    UserRepository userRepository;

    @GetMapping
    public List<MyUser> getUsers(){
        if(userRepository.findAll().isEmpty()){
            return List.of();
        }
        return userRepository.findAll();
    }
    
    @GetMapping("/search")
    @ResponseBody
    public List<MyUser> getUsersByQuery(@Nullable @RequestParam("name") String name, @Nullable @RequestParam("email") String email, @Nullable @RequestParam("phoneNumber") String phoneNumber){
        ArrayList<MyUser> users = new ArrayList<MyUser>();
        if(email != null){
            users.addAll(this.userRepository.findUserByPhoneNumber(phoneNumber));
        }
        if(name != null){
            users.addAll(this.userRepository.findUserByName(name));
        }
        if(phoneNumber != null){
            users.addAll(this.userRepository.findUserByPhoneNumber(phoneNumber));
        }

        if(email == null && name == null && phoneNumber == null){
            return List.of();
        }
        return users;
        
    }

    @GetMapping("/{id}")
    public MyUserDTO getUserById(@PathVariable Long id){
        return MyUserDTO.from(findUser(id));
    }

    private MyUser findUser(Long id){
        return this.userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + id + " not found"));
    }

    @DeleteMapping
    public List<MyUser> deleteAllUsers(){
        this.userRepository.deleteAll();
        return getUsers();
    }

    @DeleteMapping("/{id}")
    public MyUserDTO deleteUser(@PathVariable Long id){
        MyUser deletedUser = findUser(id);
        this.userRepository.delete(deletedUser);
        return MyUserDTO.from(deletedUser);
    }

    @PostMapping
    public MyUserDTO createUser(@RequestBody MyUserDTO newUser){
        MyUser user = new MyUser();
        user.setName(newUser.name());
        user.setEmail(newUser.email());
        user.setPhoneNumber(newUser.phoneNumber());
        boolean isDataValid = user.isUserDataValid();
        if(!isDataValid){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User couldn't be created, check the sent parameters");
        }
        return MyUserDTO.from(this.userRepository.save(user));
    }

    @PutMapping("/{id}")
    public MyUserDTO updateUser(@PathVariable Long id, @RequestBody String name, @RequestBody String email, @RequestBody String phoneNumber){
        return MyUserDTO.from(findUser(id));
    }
}
