# How to validate your RPD #


## Step 1 ##

Export metadata using the biserverxmlgen command (shipped with OBIEE OOTB)

  1. go to command line (or shell)
  1. find the biserverxmlgen.exe or biserverxmlgen (in Linux/Unix)
  1. locate the RPD you want to validate
  1. run the following command:
`biserverxmlgen -p <RPD password> -r <path to RPD> -o <path to output file> -n -8 -q`

_Suggestion: compressing the file to zip format will speed up the upload step_
_Also, both commands can be effortlessly added to a batch file or script, avoid typing the same time and time again_


## Step 2 ##

Upload the file
  1. Open your browser and go to `http://your-tomcat-server-IP-or-name:port/analytics-validator-service/`
  1. Select ZIP if you plan to upload a compressed file. XUDML otherwise.
  1. Select the level of details you need to review in the result pages.

Note: when running this application locally use `http://localhost:8080/analytics-validator-service/`


## Step 3 ##

Unless you are one of the lucky few, RPD's usually contain metadata for thousands of objects. You can test all the objects used by a subject area in this step.

  1. Use the drop down to select a subject area.

This step narrows down the number of objects to validate.


## Last step ##

You will be presented with the Summary page.
Review the details or download a ZIP file with both files for further discussion with your peers.

**Done!**