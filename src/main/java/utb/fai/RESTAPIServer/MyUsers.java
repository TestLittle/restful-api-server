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
    public List<MyUserDTO> getUsers(){
        return getUsersAndReturnDTO();
    }

    private List<MyUserDTO> getUsersAndReturnDTO(){
        if(userRepository.findAll().isEmpty()){
            return List.of();
        }
        List<MyUser> users = userRepository.findAll();
        System.out.println(users);
        List<MyUserDTO> usersDTO = new ArrayList<MyUserDTO>();
        for(MyUser user : users){
            System.out.println("Adding user: " + user.getId() + " " + user.getName());
            usersDTO.add(MyUserDTO.from(user));
        }
        return usersDTO;
    }
    
    @GetMapping("/search")
    @ResponseBody
    public List<MyUserDTO> getUsersByQuery(@Nullable @RequestParam("name") String name, @Nullable @RequestParam("email") String email, @Nullable @RequestParam("phoneNumber") String phoneNumber){
        return getUsersByQueryAndReturnDTO(name, email, phoneNumber);
    }

    private List<MyUserDTO> getUsersByQueryAndReturnDTO(String name, String email, String phoneNumber){
        ArrayList<MyUser> users = new ArrayList<MyUser>();
        if(email != null){
            System.out.println("Email je prazdny.");
            users.addAll(this.userRepository.findUserByPhoneNumber(phoneNumber));
        }
        if(name != null){
            System.out.println("Jmeno je prazdne.");
            users.addAll(this.userRepository.findUserByName(name));
        }
        if(phoneNumber != null){
            System.out.println("Cislo je prazdne.");
            users.addAll(this.userRepository.findUserByPhoneNumber(phoneNumber));
        }

        if(email == null && name == null && phoneNumber == null){
            System.out.println("Vsechno je prazdne.");
            return getUsers();  
        }

        List<MyUserDTO> usersDTO = new ArrayList<MyUserDTO>();

        for(MyUser user : users){
            usersDTO.add(MyUserDTO.from(user));
        }
        return usersDTO;
    }

    @GetMapping("/{id}")
    public MyUserDTO getUserById(@PathVariable Long id){
        return MyUserDTO.from(findUser(id));
    }

    private MyUser findUser(Long id){
        return this.userRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + id + " not found"));
    }

    @DeleteMapping
    public List<MyUserDTO> deleteAllUsers(){
        this.userRepository.deleteAll();
        return getUsers();
    }

    @DeleteMapping("/{id}")
    public MyUserDTO deleteUser(@PathVariable Long id){
        MyUserDTO deletedUser = MyUserDTO.from(findUser(id));
        this.userRepository.deleteById(id);
        return deletedUser;
    }

    @PostMapping
    public MyUserDTO createUser(@RequestBody MyUserDTO newUser){
        return updateUserData("create", newUser);
    }

    @PutMapping("/{id}")
    public MyUserDTO updateUser(@PathVariable Long id, @RequestBody MyUserDTO updatedUserDTO){
        return updateUserData("update", updatedUserDTO);
    }

    private MyUserDTO updateUserData(String type, MyUserDTO updatedUserDTO){
        MyUser user = new MyUser();
        if(type.equals("create")){
            user.setId(updatedUserDTO.id());
        }else if(type.equals("update")){
            user = findUser(updatedUserDTO.id());
        }
        user.setName(updatedUserDTO.name());
        user.setEmail(updatedUserDTO.email());
        user.setPhoneNumber(updatedUserDTO.phoneNumber());
        boolean isDataValid = user.isUserDataValid();
        if(!isDataValid){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "User couldn't be created, check the sent parameters");
        }
        this.userRepository.save(user);
        return updatedUserDTO;
    }
}
