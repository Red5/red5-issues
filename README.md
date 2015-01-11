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
