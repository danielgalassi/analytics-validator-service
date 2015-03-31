# There's some room for improvement #

Is it just me or BI development practices lag well behind when compared to traditional development projects'? No testing frameworks available, version control is pretty ad-hoc, peer reviews are rare (at least in my experience). The result? Nonexistent OBIEE errors or bugs are called out, poor maintainability, performance issues... you name it...

OBI repositories usually contain thousands of objects... How do we review these solutions? Peer reviews are effective but they require quite a bit of time. Checklists are often overlooked. I believe most of us think a more efficient and effective process should be available. I am not quite sure developers are humans but if that's the case... we do make mistakes! So, why not use an application to speed up the validation of repositories?


## In a nutshell ##
Running out of excuses now. Just upload the metadata repository (RPD) and get an unbiased, on-demand review. The message is simple, I just don't want to cripple my OBIEE environment or waste my life fixing issues... This easy-to-use validator service checks the standards of the OBIEE 11g repository metadata. Try it out!

Interested in new features or tests... [email me](mailto:danielgalassi@gmail.com?subject=Analytics%20Metadata%20Validator&body=Hey%20Daniel,%20I%20got%20an%20idea.).

![http://analytics-validator-service.googlecode.com/svn/trunk/Step1.png](http://analytics-validator-service.googlecode.com/svn/trunk/Step1.png) [More screenshots](Screenshots.md)


This tool **does not** gather statistics, send reports back to the developer or share information with third parties.

The GNU General Public License is a free, copyleft license for software and other kinds of works. Visit [GNU.org](http://www.gnu.org/copyleft/gpl.html) for GPL v3 terms.