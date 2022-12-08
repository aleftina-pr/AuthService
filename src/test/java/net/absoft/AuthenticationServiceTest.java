package net.absoft;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.absoft.data.Response;
import net.absoft.services.AuthenticationService;
import org.testng.annotations.*;
import org.testng.asserts.SoftAssert;

public class AuthenticationServiceTest {

  private AuthenticationService authenticationService;
  private SoftAssert softAssert;

  @BeforeMethod (
          groups = {"positive", "negative"}
  )
  public void setUp(){
    authenticationService = new AuthenticationService();
    softAssert = new SoftAssert();

  }

  @Test (
          groups = "positive",
          dataProvider = "validLogin",
          description = "Successful authentication"
  )
  public void testSuccessfulAuthentication(String email, String password, Response expectedResponse) {
    Response actualResponse = authenticationService.authenticate(email, password);
    assertEquals(actualResponse.getCode(), expectedResponse.getCode(), "Response code should be 200");
    assertTrue(validateToken(actualResponse.getMessage()),
        "Token should be the 32 digits string. Got: " + actualResponse.getMessage());
  }


  @Test (
          groups = "negative",
          dataProvider = "invalidLogins",
          description = "Authentication with incorrect credentials"
  )
  public void testInvalidAuthentication(String email, String password, Response expectedResponse) {
    Response actualResponse = authenticationService
        .authenticate(email, password);
    softAssert.assertEquals(actualResponse.getCode(), expectedResponse.getCode(), "Response code should be 401");
    softAssert.assertEquals(actualResponse.getMessage(), expectedResponse.getMessage(),
        "Response message should be \"Invalid email or password\"");
    softAssert.assertAll();
  }

  /*@Test (
          groups = "positive",
          description = "failing test to check retry analyzer"
  )
  public void failingTest() throws InterruptedException {
    Thread.sleep(2000);
    fail("Failing test!");
  }*/


  private boolean validateToken(String token) {
    final Pattern pattern = Pattern.compile("\\S{32}", Pattern.MULTILINE);
    final Matcher matcher = pattern.matcher(token);
    return matcher.matches();
  }

  @DataProvider(name = "invalidLogins", parallel = true)
  public Object[][] invalidLogins(){
    return new Object[][] {
            new Object[] {"user1@test.com", "wrong_password1", new Response(401, "Invalid email or password")},
            new Object[] {"", "password1", new Response(400, "Email should not be empty string")},
            new Object[] {"user1", "password1", new Response(400, "Invalid email")},
            new Object[] {"user1@test", "", new Response(400, "Password should not be empty string")}
    };
  }

  @DataProvider(name = "validLogin")
  public Object[][] validLogins(){
    return new Object[][]{
      new Object[] {
              "user1@test.com", "password1", new Response(200, "Ok")
      }
    };
  }
}
