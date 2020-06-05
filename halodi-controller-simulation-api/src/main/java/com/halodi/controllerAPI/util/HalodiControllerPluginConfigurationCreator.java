package com.halodi.controllerAPI.util;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import com.halodi.controllerAPI.NativeHalodiControllerJavaBridge;
import com.halodi.controllerAPI.demo.HalodiControllerDemo;

public class HalodiControllerPluginConfigurationCreator
{
   public static HalodiControllerPluginConfiguration create(Class<? extends NativeHalodiControllerJavaBridge> pluginClass)
   {
      HalodiControllerPluginConfiguration config = new HalodiControllerPluginConfiguration();
      config.name = pluginClass.getSimpleName();
      config.classPath = new ArrayList<String>();
      config.vmArgs = new ArrayList<>();

      ClassLoader systemClassLoader = Thread.currentThread().getContextClassLoader();

      URL[] urls = ((URLClassLoader) systemClassLoader).getURLs();

      for (URL url : urls)
      {
    	  String file;
		try 
		{
			file = new File(url.toURI()).getAbsolutePath();
		} 
		catch (URISyntaxException e) 
		{
			e.printStackTrace();
			continue;
		}
         
         if(ignore(file))
         {
            System.out.println("Ignoring classpath entry " + file);
         }
         else
         {
            config.classPath.add(file);
         }
         
      }

      config.javaHome = System.getProperty("java.home");

      RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
      List<String> arguments = runtimeMxBean.getInputArguments();

      config.vmArgs.addAll(arguments);

      config.mainClass = pluginClass.getCanonicalName();

      return config;
   }

   private static boolean ignore(String file)
   {
      if(file.contains("org.glassfish.jaxb") && file.contains("jaxb-runtime"))
      {
         return true;
      }
      
      if(file.contains("jakarta.xml.bind-api"))
      {
         return true;
      }
      
      return false;
   }

   public static void main(String[] args)
   {
      System.out.println(create(HalodiControllerDemo.class).toString());
   }
}
