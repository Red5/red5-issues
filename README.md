red5-issues
=============

Code used to test or verify issues submitted to the Red5 project. Feel free to fork and add your issue code. To "activate" your application class, go to the <tt>WEB-INF/red5-web.xml</tt> and modify the "web.handler" bean.

```
    <bean id="web.handler" class="org.red5.issues.issue39.Application"/>
```

Following the format above will keep things from getting confusing.

```
[package].[issue short name].Application
```

=======
How to proceed

1. Create an issue report on red5-server

2. Include your reproduction steps

3. Pull this repo

4. Create an application class to demonstrate the issue (use the existing code as an example)

5. Create a pull request with your code

6. Update your issue report

We'll look into the issue as soon as possible and your example app will go a long way towards getting a fix in-place.
