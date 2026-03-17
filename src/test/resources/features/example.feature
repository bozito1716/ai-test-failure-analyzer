@example @regression
Feature: Example — Email Validation

  # This feature demonstrates the AI Failure Analyzer in action.
  # To trigger AI analysis: intentionally break the step definition
  # (e.g., use a wrong locator) and run with the AIFailureListener registered.

  Scenario: Invalid email shows a validation error
    Given I open the example page "https://www.example.com/signup"
    When I enter an invalid email "notanemail@@@"
    And I click submit
    Then I should see an email validation error
