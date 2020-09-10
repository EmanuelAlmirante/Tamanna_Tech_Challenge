# Tamanna Tech Challenge

This application is an interview calendar API. It exposes endpoints to create interviewers and candidates and 


### Necessary Tools:

You will need to install **Intellij IDEA** (https://www.jetbrains.com/idea/download/#section=linux), **Docker** (https://docs.docker.com/get-docker/), **Postman** (https://www.postman.com/downloads/), and **Java 11** (Windows - https://docs.oracle.com/en/java/javase/11/install/installation-jdk-microsoft-windows-platforms.html#GUID-0DB9580B-1ACA-4C13-8A83-9780BEDF30BB | Linux - http://ubuntuhandbook.org/index.php/2018/11/how-to-install-oracle-java-11-in-ubuntu-18-04-18-10/ | MacOS - https://docs.oracle.com/en/java/javase/11/install/installation-jdk-macos.html#GUID-E8A251B6-D9A9-4276-ABC8-CC0DAD62EA33). 


### Tech Stack:

- Java 11
- Spring Boot
- Hibernate
- H2 Database (in-memory)
- JUnit
- Mockito


### Setup:

- Clone project to a folder
- Run the application with:
  - _mvn clean install_
  - _mvn spring-boot:run_
- Test the application with:
  - _mvn test_ -> run all tests
  - _mvn -Dtest=TestClass test_ -> run a single test class
  - _mvn -Dtest=TestClass1,TestClass2 test_ -> run multiple test classes
- Package the application with _mvn package_
- Test using Postman

### To Use With Docker:
  - Install Docker on your machine
  - Launch Docker
  - Run the command _sudo systemctl status docker_ to confirm Docker is running
  - Open terminal in the project folder
  - Run the command _mvn clean install_
  - Run the command _sudo docker build -t [NAME_OF_IMAGE] ._ to create the Docker image. Replace _NAME_OF_IMAGE_ with a name for that image like, for example, _uphill-solution_
  - Run the command _sudo docker run -p 8080:8080 [NAME_OF_IMAGE]_ to launch the application
  - Test using Postman
  
  
### Endpoints:

Below are documented the endpoints of the API of this project. There are also some examples of possible outcomes that might happen when using the API. 

For testing the API use Postman and the file in the _postman_collections_ folder. 

### Code Quality Requirements

All delivered code must adhere to Clean Code principle. Code is clean if it can be understood easily by everyone on the team. Clean code can be read and enhanced by a developer other than its original author. With understandability comes readability, changeability, extensibility and maintainability.

* General rules:
	1. Follow standard conventions.
	2. Keep it simple. Simpler is always better. Reduce complexity as much as possible.
	3. Boy scout rule. Leave the campground cleaner than you found it.
	4. If you have a problem, always find root cause, do not just fix immediate issue and ignore what caused it in the first place.
	
* Design rules:
	1. Keep configurable data at high levels.
	2. Prefer polymorphism to if/else or switch/case.
	3. Separate multi-threading code.
	4. Prevent over-configurability.
	5. Use dependency injection.
	6. Follow Law of Demeter. A class should know only its direct dependencies.
	
* Understandability tips:
	1. Be consistent. If you do something a certain way, do all similar things in the same way.
	2. Use explanatory variables.
	3. Encapsulate boundary conditions. Boundary conditions are hard to keep track of. Put the processing for them in one place.
	4. Prefer dedicated value objects to primitive type.
	5. Avoid logical dependency. Don't write methods which works correctly depending on something else in the same class.
	6. Avoid negative conditionals.
	
* Names rules:
	1. Choose descriptive and unambiguous names.
	2. Make meaningful distinction.
	3. Use pronounceable names.
	4. Use searchable names.
	5. Replace magic numbers with named constants.
	6. Avoid encodings. Don't append prefixes or type information.
	
* Functions rules:
	1. Small.
	2. Do one thing.
	3. Use descriptive names.
	4. Prefer fewer arguments.
	5. Have no side effects.
	6. Don't use flag arguments. Split method into several independent methods that can be called from the client without the flag.
	
* Comments rules:
	1. Always try to explain yourself in code.
	2. Don't be redundant.
	3. Don't add obvious noise.
	4. Don't use closing brace comments.
	5. Don't comment out code. Just remove.
	6. Use as explanation of intent.
	7. Use as clarification of code.
	8. Use as warning of consequences.
	
* Source code structure:
	1. Separate concepts vertically.
	2. Related code should appear vertically dense.
	3. Declare variables close to their usage.
	4. Dependent functions should be close.
	5. Similar functions should be close.
	6. Place functions in the downward direction.
	7. Keep lines short.
	8. Don't use horizontal alignment.
	9. Use white space to associate related things and disassociate weakly related.
	10. Don't break indentation.
	
* Objects and data structures:
	1. Hide internal structure.
	2. Prefer data structures.
	3. Avoid hybrids structures (half object and half data).
	4. Should be small.
	5. Do one thing.
	6. Small number of instance variables.
	7. Base class should know nothing about their derivatives.
	8. Better to have many functions than to pass some code into a function to select a behavior.
	9. Prefer non-static methods to static methods.
	
* Tests:
	1. One assert per test.
	2. Readable.
	3. Fast.
	4. Independent.
	5. Repeatable.
	
* Code smells:
	1. Rigidity. The software is difficult to change. A small change causes a cascade of subsequent changes.
	2. Fragility. The software breaks in many places due to a single change.
	3. Immobility. You cannot reuse parts of the code in other projects because of involved risks and high effort.
	4. Needless Complexity.
	5. Needless Repetition.
	6. Opacity. The code is hard to understand.

### Tests Implementation Guide

#### Scope

The first thing to consider when implementing/defining tests and test cases is the testing scope:

- What are we going to test?

• Test scenarios/Test objectives that will be validated.

- How are we going to test it?

• Automated Tests

• Manual tests

We need to define test scenarios and/or objectives in order to decide how are we going to test it; Depending on each scenario, we may need to choose a different approach.

#### Manual vs Automated Tests

When deciding between manual and automated tests, we need to consider the following:
 
- How much time are willing to spend with the test runs?

Manual testing is more time-consuming, since it is performed by human resources;

On the other hand, automatic tests take less time to run, since a software tool executes the tests (and we can let the tests run overnight);

- If we are going to automate, how much effort will be needed to implement these tests/scripts?
	
Some automated tests can take too much time to develop, so maybe we need to find a middle point between the two approaches.
	
- How many times are we going to run a specific test?

This is a very important point to consider: tests that need to be run more often are more likely candidates to be automated.
 
In short, manual testing is best suited to the following areas/scenarios:

• Exploratory Testing: 

This type of testing requires the tester’s knowledge, experience, analytical/logical skills, creativity, and intuition.
The test is characterized here by poorly written specification documentation, and/or a short time for execution. We need the human skills to execute the testing process in this scenario.

• Usability Testing: 

This is an area in which we need to measure how user-friendly, efficient, or convenient the software or product is for the end users. Here, human observation is the most important factor, so a manual approach is preferable.

• Ad-hoc Testing: 

In this scenario, there is no specific approach. It is a totally unplanned method of testing where the understanding and insight of the tester is the only important factor.

#### Defining Test Cases

“A TEST CASE is a set of conditions or variables under which a tester will determine whether a system under test satisfies requirements or works correctly”.

In other words, a test case is a single executable test which a tester carries out. It guides them through the steps of the test. You can think of a test case as a set of step-by-step instructions to verify something behaves as it is required to behave.

The process of developing test cases can also help find problems in the requirements or design of an application.

Test cases definition can begin at the same time as the development.

A test case usually contains, but is not limited to:

• Title

• Description

• Test steps

• Expected result

• Actual result (once tested)

This can be done using a test document (i.e., xlsx) or a test management tool.

#### Types of Tests

After defining the test cases, we can decide in which kind of testing we’ll use.

Test cases that will be automated will likely fall in one of these categories:

- Unit tests:

The goal of Unit Testing is to isolate each part of the program and show that the individual parts are correct.

Due to the modular nature of the unit testing, we can test parts of the project without waiting for others to be completed.

May require the use of stubs/mocks.

- Integration Tests:

In Integration Testing, individual software modules are integrated logically and tested as a group.

A typical software project consists of multiple software modules, coded by different programmers.

Integration Testing focuses on checking data communication amongst these modules.

These broader tests are used to test the app's infrastructure and whole framework, often including the following components:

Database, File system, Request-response pipeline

- System Tests:

Testing the fully integrated applications including external peripherals in order to check how components interact with one another and with the system as a whole. This is also called End to End testing scenario. GUI testing falls in this kind of tests.

- Another type of test that we need to take into account is Regression Testing:

Regression Testing is defined as a type of software testing to confirm that a recent program or code change has not adversely affected existing features.

Regression Testing is nothing but full or partial selection of already executed test caseswhich are re-executed to ensure existing functionalities work fine.

This testing is done to make sure that new code changes should not have side effects on the existing functionalities. It ensures that old code still works once the new code changes are done.

#### Best Practices

- Naming conventions:

• The name of your test should consist of three parts: the name of the method being tested, the scenario under which it's being tested, the expected behavior when the scenario is invoked.

- Examples:

• MethodName_StateUnderTest_ExpectedBehavior -> admitStudent_MissingMandatoryFields_FailToAdmit

• MethodName_ExpectedBehavior_StateUnderTest -> testStudentIsNotAdmittedIfMandatoryFieldsAreMissing

• When_StateUnderTest_Expect_ExpectedBehavior -> StudentIsNotAdmittedIfMandatoryFieldsAreMissing

- Avoid multiple asserts:
 
If one Assert fails, the subsequent Asserts will not be evaluated.

Ensures you are not asserting multiple cases in your tests.

Gives you the entire picture as to why your tests are failing.
 
- Avoid Test Interdependence:

Each test should handle its own setup and tear down.

- Use Descriptive Messages in Assert Methods:

Describe the WHY and not the WHAT, not like Assert.assertEquals( a, b, "must be equal"). This helps to avoid too many comments in the test cases and increases the maintainability.

- Don't Forget to Refactor the Test Code:

Also maintain your test code (especially when after refactoring the code under test).   

- For integration tests:
Keep the fast (unit) and slow (integration) tests separate, so that you can run them separately. Use whatever method for grouping/categorizing the tests is provided the testing framework.

Test one piece of integration at a time.

Leave the DB in such a state that it can run multiple tests and they should not affect each other.
