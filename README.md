# mybatis-easy-typehandler  

As long as implement `MybatisHandleable`, the instance of the class can be persisted into database, 
the persist value is generated by `MybatisHandleable#constructPersistValue`, 
and deserialize to instance by `MybatisHandleable#parsePersistedValue`. 
  


## Usage
### POJO

Role.java
```java
@Data
public class Role implements MybatisHandleable<String, Role> {
    private String name;
    private Integer roleLevel;
    // getter... setter...
    
    @Override
    public String constructPersistValue() {
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public Role parsePersistedValue(String persistedValue) {
        try {
            return new ObjectMapper().readValue(persistedValue, Role.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
```
User.java
```java
@Data
public class User {
    private Long id;
    private String username;
    private Role role;
    // getter... setter...
}
```
### DAO
UserDao.java
```java
@Mapper
@Repository
public interface UserDao {

    @Insert("insert into test.users (username, role) VALUE (#{username}, #{role})")
    void save(User user);

    @Select("select * from test.users")
    List<User> findAll();
}
```

### Test
```java
@SpringBootTest
@EnableAutoConfiguration
@RunWith(SpringRunner.class)
public class UserTest {

    @Resource
    private UserDao userDao;

    @Test
    public void test() {
        User user = new User();
        user.setUsername("test");
        user.setRole(new Role() {{
            setName("role name");
            setRoleLevel(2);
        }});
        userDao.save(user);
        List<User> all = userDao.findAll();
        assert all != null;
        for (User oneUser : all) {
            assert oneUser != null;
            assert oneUser.getRole() != null;
            assert Objects.equals("role name", oneUser.getRole().getName());
        }
        System.out.println(all);
    }
}
```

test should pass, and console's output will contains something like:  
`[User(id=26, username=test, role=Role(name=role name, roleLevel=2))]`