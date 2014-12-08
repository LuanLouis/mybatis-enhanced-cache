package org.luanlouis.mybatis.plugin.cache.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.luanlouis.mybatis.plugin.cache.CacheKeysPool;
import org.luanlouis.mybatis.plugin.cache.EnhancedCachingManager;

public class EnhancedCachingManagerImpl implements EnhancedCachingManager{
	
	//每一个statementId 更新依赖的statementId集合
	private Map<String,Set<String>> observers=new ConcurrentHashMap<String,Set<String>>();
	
	//全局性的  statemntId与CacheKey集合
	private CacheKeysPool sharedCacheKeysPool = new CacheKeysPool();
	//记录每一个statementId 对应的Cache 对象
	private Map<String,Cache> holds = new ConcurrentHashMap<String,Cache>();
	private boolean initialized = false;
	private boolean cacheEnabled = false;
	
	private static EnhancedCachingManagerImpl enhancedCacheManager;

	private EnhancedCachingManagerImpl(){}
	public static EnhancedCachingManagerImpl getInstance()
	{
		return enhancedCacheManager==null ? (enhancedCacheManager =new EnhancedCachingManagerImpl()):enhancedCacheManager;
	}
	
	@Override
	public void refreshCacheKey(CacheKeysPool keysPool) {
		sharedCacheKeysPool.putAll(keysPool);
		//sharedCacheKeysPool.display();
	}
	@Override
	public void clearRelatedCaches(final Set<String> set) {
		//sharedCacheKeysPool.display();
		for(String observable:set)
		{
			Set<String> relatedStatements = observers.get(observable);
			for(String statementId:relatedStatements)
			{
				Cache cache = holds.get(statementId);
				Set<Object> cacheKeys = sharedCacheKeysPool.get(statementId);
				for(Object cacheKey: cacheKeys)
				{
					cache.removeObject(cacheKey);
				}
			}
			// clear shared cacheKey Pool width specific key
			sharedCacheKeysPool.remove(observable);
		}
	}
	@Override
	public boolean isInitialized() {
		return initialized;
	}
	
	@Override
	public void initialize(Properties properties)
	{
		String dependency = properties.getProperty("dependency");
		if(!("".equals(dependency) || dependency==null))
		{
			InputStream inputStream;
			try 
			{
					inputStream = Resources.getResourceAsStream(dependency);
					XPathParser parser = new XPathParser(inputStream);
					List<XNode> statements = parser.evalNodes("/dependencies/statements/statement");
					for(XNode node :statements)
					{
						Set<String> temp = new HashSet<String>();
						List<XNode> obs = node.evalNodes("observer");
						for(XNode observer:obs)
						{
							temp.add(observer.getStringAttribute("id"));
						}
						this.observers.put(node.getStringAttribute("id"), temp);
					}
					initialized = true;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//cacheEnabled
		String cacheEnabled = properties.getProperty("cacheEnabled", "true");
		if("true".equals(cacheEnabled))
		{
			this.cacheEnabled = true;
		}
	}
	@Override
	public void appendStatementCacheMap(String statementId, Cache cache) {
		if(holds.containsKey(statementId)&& holds.get(statementId)!=null)
		{
			return ;
		}
		holds.put(statementId, cache);
	}
	@Override
	public boolean isCacheEnabled() {
		return cacheEnabled;
	}
}

