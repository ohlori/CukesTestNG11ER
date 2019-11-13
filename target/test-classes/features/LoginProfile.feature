@LoginProfile
Feature: Login Profile
  As an employee of the company
  I want to login my employee profile using my credentials
  In order to collaborate with my colleagues

  Background: User navigates to Company home page
    Given Sheetname for datasheet is "SampleLogin"
    #Then I should see "Log In as Employee" message

  Scenario: Sample successful login
    And I fill in the username field with "data1"
    And I fill in the password field with "data2"
    
    Scenario: Update name
    When I click on the "data1" button
    And I fill in "data2" with "data2"
    And I click on the "data3" button
    Then I should see "data4" message

  