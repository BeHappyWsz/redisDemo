package redis;

import java.util.List;

import org.junit.Test;

import redis.clients.jedis.Jedis;
/**
 * 动态字符串,采用预分配冗余空间来减少内存的频繁分配,实际分配的字符串空间capacity一般大于实际字符串的长度len
 * len<1M,扩容时加倍
 * len>=1M,扩容时每次多扩1M的空间
 * max(len)<=512M
 * @author wsz
 * @date 2018年9月10日
 */
public class StringTest {

	private Jedis jedis = new Jedis("127.0.0.1", 6379);
	
	private static final String KEY = "strKey";
	private static final String VALUE = "strValue";
	private static final int EXPIRE_TIME = 5;
	
	@Test
	public void add() {
		// 设置key的过期时间,单位S
		jedis.set(KEY, VALUE);
		jedis.expire(KEY, EXPIRE_TIME);
		// 新增key并设置过期时间
		jedis.setex(KEY, EXPIRE_TIME, VALUE);
		// set if not exits:不存在即新增
		jedis.setnx(KEY, VALUE);
		// 批量添加:key-value成对出现,成功返回OK
		String mset = jedis.mset("a", "b", "c", "d");
		System.out.println(mset);
		// 批量不存在时添加:key-value成对出现,成功返回1,至少有一个未设值成功则为0
		Long msetnx = jedis.msetnx("a", "w", "e", "r");
		System.out.println(msetnx);
	}
	
	@Test
	public void get() {
		// 单个获取
		String value = jedis.get(KEY);
		System.out.println(value);
		// 批量获取
		List<String> mget = jedis.mget("0", "1", "2");
		System.out.println(mget.toString());
	}
	
	@Test
	public void del() {
		//单个删除
		Long del = jedis.del("1");
		System.out.println(del);
		//批量删除 返回为0即key均不存在，返回N大于0即N个key删除成功
		Long del2 = jedis.del("a", "b");
		System.out.println(del2);
	}
	
	/**
	 * incr的value必须为interger类型且在singed long范围内,否则报错ERR value is not an integer or out of range
	 * incrByFloat自增后变为存在小数点，不可再incr；不存在小数点，可再incr
	 */
	
	@Test
	public void increment() {//自增,范围与signed long一致
		// 自增+1，返回自增后的值
		Long incr = jedis.incr("1");
		System.out.println("incr:"+incr);
		// 自增，增值可为负数
		Long incrBy = jedis.incrBy("1", 5);
		System.out.println("incrBy+5:"+incrBy);
		// 自增，Float值，value将变成float类型
		Double incrByFloat = jedis.incrByFloat("1", -6.2F);
		System.out.println(incrByFloat);
		
		// integer值的范围
		jedis.set("longMax", String.valueOf(Long.MAX_VALUE));
		System.out.println(jedis.get("longMax"));
//		Long longMax = jedis.incr("longMax");// overflow
//		System.out.println(longMax);
		
		// 无法自增字符串 ERR value is not an integer or out of range
//		jedis.set("incrStr", "a");
//		System.out.println(jedis.incr("incrStr"));
	}
}
