package utb.fai.RESTAPIServer;

public record MyUserDTO(String name, String email, String phoneNumber){
    public static MyUserDTO from(MyUser user){
        return new MyUserDTO(user.getName(), user.getEmail(), user.getPhoneNumber());
    }    
}
