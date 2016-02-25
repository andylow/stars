Stars is the extension that brings full stack development experience to Stripes users.

##Stars & Stripes
Stars is the extension that brings full stack development experience to Stripes users.

##The benefit of using Stars
* Be able to change the service provider easily (Just a few configurations)
* Be able to reuse existing knowledge because you can use target framework's native annotations which you are already familiar with such as `@EJB`, `@Autowired`, etc.
* Small footprint and lightweight.

##Features
* Stars enhances existing ActionBean classes by providing dependency injection capability.
* JSR 250's annotations `@PostConstruct`, `@PreDestroy` and `@Resource`
* Spring integration - `@Autowired` annotation support and Spring callback interfaces such as InitializingBean, DisposableBean, ApplicationContextAware, BeanFactoryAware.
* EJB integration - `@EJB` annotation support.
* For Java Persistence API - `@PersistenceUnit`, `@PersistenceContext` with multiple persistence units support. Transaction type support depends on configuration and service provider.
* Bootstrap class with dependency injection support
* Built-in service container called Stars local service which supports annotation configuration as well. For example `@Service`, `@ServiceBean`
* It's easy to configure and customize.
* Useful taglibs

##Up Coming Features
* Generic DAO
* Security Framework Ingration (Added in 0.5.0 Release)
* CDI
* Web Service
* UI Helper
* Scripting

##Code Snippet Example
The below snippet show how to implement bootstrap and use annotation in Stars. Stars does not manage transaction for Bootstrap class even you're using Stars local service, so you have to manage your own transaction. I use @SkipInjectionError to mark the bootstrap class in order to avoid injection error blocks the bootstrap job.

##Injection error? 
Yes! because Stars supports multiple service providers and therefore fail to inject the missing resource. 
In this example, if you run the web application in Java EE container managed environment, the UserTransaction resource will be avaiable; otherwise the resource will be missing. That's why `@SkipInjectionError` comes into play.
Why Stars does not set the null value to attribute for unavailable resource instead of throwing error?
The answer is http://en.wikipedia.org/wiki/Fail-fast'>Fail-fast

**BugzookyBootstrap.java**
```java
@SkipInjectionError 
public class BugzookyBootstrap implements StarsBootstrap {

@PersistenceContext
private EntityManager em;

@Resource
private UserTransaction userTx;

@Override
public void execute(ServletContext servletContext) throws Exception {
    if(ServiceProvider.isEjb(servletContext)){
        userTx.begin();
    }else{
        if(!em.getTransaction().isActive()) 
            em.getTransaction().begin();
    }
```

The below snippet show how to inject service instance to ActionBean's attribute using annotation configuration. There are 3 annotations for 3 types of service provider- Stars local service, Spring, and EJB respectively. The amazing feature of Stars, you can change service provider easily by changing a few line of configuration (No code change!!!). It means that the below ActionBean can use 3 difference types of service by just changing a few configurations without code changing. That's cool, isn't it. You can touch this cool feature with your hand by downloading the stars-examples right now.

**MultiBugActionBean.java** 
```java
@UrlBinding("/bugzooky/multiBug.action") public class MultiBugActionBean extends BugzookyActionBean {

@Service(impl=BugManagerImpl.class)
@Autowired
@EJB
private BugManager bugManager;
... ```
