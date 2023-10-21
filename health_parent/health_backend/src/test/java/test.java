import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
public class test{

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Test
    public void test1(){
        String admin = bCryptPasswordEncoder.encode("admin");
        System.out.println(admin);
    }
}