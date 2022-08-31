package com.example.formApi;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import io.restassured.module.mockmvc.response.MockMvcResponse;
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification;
import io.restassured.response.ResponseOptions;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.Assert;
import java.io.*;
import java.util.ArrayList;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;

@ExtendWith( SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class apitest {
    private static int USERID=0;
    private static ArrayList<String> UserIDs = new ArrayList<String>();

    @Autowired
    private MockMvc mockMvc;
    
    @BeforeEach
    void setUp() {
        RestAssuredMockMvc.mockMvc(mockMvc);
    }

    @Test
    @Order(1)
    public void getAllUsers() {
        MockMvcResponse response= RestAssuredMockMvc.get("http://localhost:7050/api/users");
        if(response.getStatusCode()==204){
            System.out.println(response.asString());
        }
        else{
            Assert.assertEquals(response.getStatusCode() , 200 , "Status Code");
            System.out.println(response.asString());
        }
    }


    @Test
    @Order(2)
    void createUser() throws IOException {

        JSONObject user=getUserFromCsv("src\\users.csv",1);
        user.remove("id");

        MockMvcRequestSpecification request = given();
        ResponseOptions response = given()
                .spec(request).header("Content-Type", "application/json")
                .body(user.toJSONString())
                .post("http://localhost:7050/api/user/new");

        if(response.getStatusCode()!=200){
            String error=response.getBody().asString();
            validateFields(user,error);
        }else{
            Assert.assertEquals(response.getStatusCode(),200);
            String userId=response.getBody().asString();
            System.out.println("User Created Id: "+userId);
            apitest.USERID= Math.toIntExact(Long.valueOf(userId));
        }
  }


    @Test
    @Order(4)
    void editUser() throws IOException {

        JSONObject user=getUserFromCsv("src\\users.csv",2);

        if(USERID>0){
            user.put("id",Long.valueOf(USERID));
        }

        // https://stackoverflow.com/questions/47247196/how-to-disable-rest-assured-debug-printing-to-console

        MockMvcRequestSpecification request = given();
        ResponseOptions response = given()
                .spec(request).header("Content-Type", "application/json")
                .body(user.toJSONString())
                .post("http://localhost:7050/api/user/edit");

        if(response.getStatusCode()!=200){
            String error=response.getBody().asString();
            validateFields(user,error);
        }else{
            Assert.assertEquals(response.getStatusCode(),200);
            String userId=response.getBody().asString();
            System.out.println("User Updated: "+user);
        }
    }
    @Test
    @Order(5)
    public void getUser() throws IOException {
        JSONObject user=getUserFromCsv("src\\users.csv",3);
        if(USERID>0){
            user.put("id",Long.valueOf(USERID));
        }

        MockMvcRequestSpecification request = given();
        ResponseOptions response = given()
                .spec(request).header("Content-Type", "application/json")
                .get("http://localhost:7050/api/user/"+user.get("id"));

        if(response.getStatusCode()!=200){
            System.out.println(response.getBody().asString());
            Assert.fail(response.getBody().asString() + " with id: "+ user.get("id"));
        }
        else{
            Assert.assertEquals(response.getStatusCode() , 200 , "Status Code");
            System.out.println(response.getBody().asString());
        }
    }

    @Test
    public void deleteUser() throws InterruptedException, IOException {
        Long id = null;

        if(USERID>0){
            id= Long.valueOf(USERID);
        }
        else{
            JSONObject user=getUserFromCsv("src\\users.csv",3);
            System.out.println(user);
            if(user.containsKey("id")){
                id= Long.parseLong(String.valueOf(user.get("id")));
            }
        }

        if(id==null){
            Assert.fail("No User Id Given");
        }else{
            MockMvcResponse response= RestAssuredMockMvc.delete("http://localhost:7050/api/user/"+id);
            if(response.getStatusCode()!=200){
                System.out.println(response.asString());
                Assert.fail(response.asString() + "with user id :"+id);
            }
            else{
                Assert.assertEquals(response.getStatusCode() , 200 , "Status Code");
                System.out.println(response.asString()+ " user id: "+id);
            }
        }
    }


    private JSONObject getUserFromCsv(String file,int count) throws IOException {
        BufferedReader reader=null;
        String line="";
        ArrayList<String> keys = new ArrayList<String>();
        ArrayList<String> values = new ArrayList<String>();
        JSONObject user = new JSONObject();
        int i=0;

        try{
            reader = new BufferedReader(new FileReader(file));
            while((line= reader.readLine())!=null){
                String[] row =line.split(",");
                for(String index:row){
                    if(count!=3 && i==0){
                            keys.add(index);
                    }
                    if(i==count){
                        if(count==3){
                            values.add(index);
                        }else{
                            values.add(index);
                        }
                    }
                }
                i++;
            }
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            reader.close();
        }
        if(count==3){
            keys.add("id");
        }
        for (int j = 0 ; j < keys.size(); j++) {
            user.put(keys.get(j),values.get(j));

        }
        return user;
    }

    public void validateFields(JSONObject user,String error){
      if(error.equals("Invalid Email")){
          System.out.println("Invalid Email");
          String email= String.valueOf(user.get("email"));
          if(email.length() > 256){
              System.out.println("Email Length Exceeded, Max 256");
              Assert.fail("Invalid Email");
          }
          if(!email.matches("[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")){
              System.out.println("Email Invalid format entered :" +email);
              Assert.fail("Invalid Email");
          }
      }
      else if(error.contains("Required")){
          System.out.println("Error: "+error);
          Assert.fail(error);
      } else if (error.contains("Unable to find User Id")) {
          System.out.println("User Id Not Found");
          Assert.fail(error);
      } else{
          System.out.println("Error: "+error);
          Assert.fail(error);
      }
  }
}