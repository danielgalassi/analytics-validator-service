# Tests and Results #


## Anatomy of Test ##

Tests are the basic validation building block and consist of a set of rules to identify valid configurations as well as anomalies. Rules such as "a dimension hierarchy **must be** configured for each logical dimension table" can be validated by counting specific metadata tags or attributes.

The criteria used to design tests is described below:
  * A (one) test **must** evaluate one rule for one specific type of object.
  * Each test **must** have a (unique) name and a description.
  * Each test **must** evaluate that rule comprehensively to produce a unique result value.
  * A description of the anomaly (if found) **should** be provided with the results as a succinct comment.

The key words "MUST", "MUST NOT", "REQUIRED", "SHALL", "SHALL NOT", "SHOULD", "SHOULD NOT", "RECOMMENDED",  "MAY", and "OPTIONAL" in this document are to be interpreted as described in [RFC 2119](http://www.ietf.org/rfc/rfc2119.txt).



## About Results ##

When a test is executed, the Validator Service uses each rule as a conditional statement.
This evaluation results in one (and only one) of the four categories:

  * **Pass**  : the object is properly configured.
  * **Fail**  : the object is not properly configured.
  * **N/A**   : the amount of metadata is insufficient to evaluate the object.**<sup>1</sup>**


**<sup>1</sup>** Some tests may require additional metadata that can be produced by OOTB features such as row counts or estimated number of elements per level.