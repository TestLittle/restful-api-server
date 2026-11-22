package utb.fai.RESTAPIServer;

public record MyUserDTO(Long id, String name, String email, String phoneNumber){
    public static MyUserDTO from(MyUser user){
        return new MyUserDTO(user.getId(), user.getName(), user.getEmail(), user.getPhoneNumber());
    }    
}
