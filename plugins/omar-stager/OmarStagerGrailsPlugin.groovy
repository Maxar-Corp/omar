import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import grails.spring.BeanBuilder

class OmarStagerGrailsPlugin
{
  // the plugin version
  def version = "0.3"
  // the version or versions of Grails the plugin is designed for
  def grailsVersion = "1.2.2 > *"
  // the other plugins this plugin depends on
  def dependsOn = [
          ://'backgroundThread': "1.6 > *"
  ]
  // resources that are excluded from plugin packaging
  def pluginExcludes = [
      "grails-app/views/error.gsp"
  ]

  // TODO Fill in these fields
  def author = "Scott Bortman"
  def authorEmail = "sbortman@radiantblue.com"
  def title = "OMAR Staging"
  def description = '''\\
OMAR org.ossim.omar.stager for data discovery
'''

  // URL to the plugin's documentation
  def documentation = "http://grails.org/plugin/omar-stager"

  def doWithWebDescriptor = { xml ->
    // TODO Implement additions to web.xml (optional), this event occurs before
  }

  def doWithSpring = {
      //workerThreadPool(java.util.concurrent.ThreadPoolExecutor){
      //   arguments=[4,
      //           4,
      //           50,
      //           TimeUnit.MILLISECONDS,
      //           new java.util.concurrent.LinkedBlockingQueue() ]
      //}
    // TODO Implement runtime spring config (optional)

//    stagerEventHandler(org.ossim.omar.StagerEventHandler) { bean ->
//      bean.scope = "prototype"
//    }
  }

  def doWithDynamicMethods = { ctx ->
    // TODO Implement registering dynamic methods to classes (optional)
  }

  def doWithApplicationContext = { applicationContext ->
      def nThreads = application.config.stager.worker.threads?:4
      def maxQueueSize = application.config.stager.worker.maxQueueSize?:1000
      // TODO Implement post initialization spring config (optional)
      def beans = beans {
          workerThreadPool(org.ossim.omar.stager.StagerThreadPoolExecutor,//java.util.concurrent.ThreadPoolExecutor,
                  nThreads,
                  nThreads,
                  50,
                  TimeUnit.MILLISECONDS,
                  new java.util.concurrent.LinkedBlockingQueue(maxQueueSize))
      }
      applicationContext.registerBeanDefinition("workerThreadPool",
                                                beans.getBeanDefinition("workerThreadPool"))
  }

  def onChange = { event ->
    // TODO Implement code that is executed when any artefact that this plugin is
    // watching is modified and reloaded. The event contains: event.source,
    // event.application, event.manager, event.ctx, and event.plugin.
  }

  def onConfigChange = { event ->
    // TODO Implement code that is executed when the project configuration changes.
    // The event is the same as for 'onChange'.
  }
}
