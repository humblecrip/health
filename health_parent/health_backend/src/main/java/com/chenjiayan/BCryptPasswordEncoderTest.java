package com.chenjiayan;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class BCryptPasswordEncoderTest {
        public static void main(String[] args) {
            String pass = "1234";
            BCryptPasswordEncoder bcryptPasswordEncoder = new BCryptPasswordEncoder();
            String hashPass = bcryptPasswordEncoder.encode(pass);
            System.out.println(hashPass);
            // $2a$10$u/BcsUUqZNWUxdmDhbnoeeobJy6IBsL1Gn/S0dMxI2RbSgnMKJ.4a
            boolean f = bcryptPasswordEncoder.matches("admin",hashPass);
            System.out.println(f);
        }


}
