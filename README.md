# Selenium-Amazon-Test
## Prerequisites:
- Java JDK installed (used version: 23.0.2)
- IntelliJ IDEA IDE installed
- Maven installed (as a plugin in intelliJ IDE and enabled in settings)
- Chrome browser installed
- Git installed

## Setup project:
- Clone the repo and open it in IntelliJ IDEA IDE.
- Open pom.xml file -> right-click -> Maven -> Sync project.
- Add environment variables in IntelliJ with values for email and password for amazon account.
    - AMAZON_EMAIL & AMAZON_PASSWORD
- Go to and Run src -> test -> java -> amazonTestCases package -> buyVideoGame.
- Go to and Run src -> test -> java -> apiTests -> RestAssuredReqresTest.

## Possible issues:
- An “Allow Remote Automation” Operating System/browser permission for IntelliJ IDEA IDE might be needed to be granted to be able to run the tests in the browser.