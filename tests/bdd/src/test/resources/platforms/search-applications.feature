Feature: Get applications

  Background:
    Given an authenticated user

#  Scenario: search for an existing application
#    Given a list of applications
#    When searching for one of those applications
#    Then application found

#  Scenario: search for existing application
#    Given a list of applications
#    When searching for some of those applications
#    Then the number of application results is 2

  Scenario: search for an application that doesn't exist
    Given a list of applications
    When searching for an application that does not exist
    Then the number of application results is 0
