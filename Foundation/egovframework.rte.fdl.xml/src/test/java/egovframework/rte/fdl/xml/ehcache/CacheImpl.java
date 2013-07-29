package egovframework.rte.fdl.xml.ehcache;

import net.sf.ehcache.*;
import java.io.Serializable;
import java.net.*;

import org.apache.log4j.Logger;

import egovframework.rte.fdl.xml.XmlLog;


public class CacheImpl 
{
 private static CacheManager manager;
 private static Cache cache; 
 Logger logger  = XmlLog.getLogger(CacheImpl.class);
 public CacheImpl() {
  // Default constructor.
 }
 
 public CacheManager getCacheManager()
 {
  try {
   URL url = getClass().getResource("xml/ehcache.xml");
   logger.debug(url.toString());
   manager = CacheManager.create(url);
   
  } catch (CacheException e) {
   e.printStackTrace();
  }
  return manager;
 }
 
 public CacheManager getCacheManager(String configFilePath)
 {
  try {
   manager = CacheManager.create(configFilePath); 
   logger.debug("Cache Name:"+manager.getName());
  } catch (CacheException e) {
   e.printStackTrace();
  }
  return manager;
 }
 
 /**
  * Get cache using key stored name.
  * @param cacheName
  * @return
  */
 public Cache getCache(String cacheName)
 {
  cache = (Cache)manager.getCache(cacheName);
  return cache;
 }
 
 /**
  * 
  * @param name
  * @param value
  */
 public void storeCache(String name , Serializable value) throws CacheException
 {
  Element element = new Element(name, value);
  cache.put(element);
 }
 
 public Serializable retrieveCache(String name) throws CacheException
 {
  return cache.get(name);
 }
}