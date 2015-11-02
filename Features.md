#Features

## Features ##
  * ActionBean supports JSR 250's annotations (Since 0.1)
> > @javax.annotation.PostConstruct<br />
> > @javax.annotation.PreDestroy<br />
> > @javax.annotation.Resource
  * EJB Integration: ActionBean supports @javax.ejb.EJB (Since 0.1)
  * Spring Integration: ActionBean supports (Since 0.1)
> > @org.springframework.beans.factory.annotation.Autowired<br />
> > and callback interfaces<br />
> > org.springframework.context.ApplicationContextAware<br />
> > org.springframework.beans.factory.BeanFactoryAware<br />
> > org.springframework.beans.factory.InitializingBean<br />
> > org.springframework.beans.factory.DisposableBean
  * Supports Bootstrap with DI (Since 0.1)
  * Stars local service with annotation driven (Since 0.1)
> > @com.siberhus.stars.ServiceBean (to marked POJO as a service)<br />
> > @com.siberhus.stars.Service (to inject the required service)<br />
> > Support request boundary transaction management
  * Supports Java Persistence API annotations(Since 0.1)
> > @javax.persistence.PersistenceUnit<br />
> > @javax.persistence.PersistenceContext<br />
  * ActionBean and Stars' local service support (Since 0.1.1)
> > @com.siberhus.SkipInjectionError